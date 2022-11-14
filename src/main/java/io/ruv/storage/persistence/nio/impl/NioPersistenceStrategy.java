package io.ruv.storage.persistence.nio.impl;

import io.ruv.storage.persistence.PersistenceException;
import io.ruv.storage.persistence.PersistenceStrategy;
import io.ruv.storage.util.properties.NioPersistenceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Implementation of {@link PersistenceStrategy} using nio to persist storage to files
 */
@Slf4j
@RequiredArgsConstructor
public class NioPersistenceStrategy implements PersistenceStrategy {

    private final NioPersistenceProperties properties;

    private final Set<OpenOption> writeOpenOptions = Set.of(
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING);


    /**
     * {@inheritDoc}
     */
    @Override
    public void persist(Stream<Map.Entry<String, Supplier<InputStream>>> stream) throws PersistenceException {

        try (val fileStream = Files.walk(properties.getBasePath())) {

            fileStream.forEach(path -> {

                try {

                    Files.delete(path);
                } catch (IOException e) {

                    throw PersistenceException.cleaningStorage(e);
                }
            });
        } catch (IOException e) {

            throw PersistenceException.cleaningStorage(e);
        }

        stream.forEach(entry -> persistOne(entry.getKey(), entry.getValue()));
    }

    public void persistOne(String key, Supplier<InputStream> value) throws PersistenceException {

        val path = properties.getBasePath().resolve(key);

        try (val inputChannel = Channels.newChannel(value.get())) {

            try (val outputChannel = FileChannel.open(path, writeOpenOptions)) {

                val bufferSize = properties.getBufferSize();
                val buffer = ByteBuffer.allocate(bufferSize);

                while (inputChannel.read(buffer) > 0) {

                    buffer.flip();
                    outputChannel.write(buffer);
                }
            }
        } catch (IOException e) {

            throw PersistenceException.writing(key, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(BiConsumer<String, Supplier<InputStream>> loadAction) throws PersistenceException {

        try (val fileStream = Files.walk(properties.getBasePath())) {

            fileStream.forEach(path -> loadOne(path, loadAction));
        } catch (IOException e) {

            throw PersistenceException.readingStorage(e);
        }
    }

    private void loadOne(Path path, BiConsumer<String, Supplier<InputStream>> loadAction) {

        val key = path.getFileName().toString();

        try {

            val bytes = Files.readAllBytes(path);
            Supplier<InputStream> value = () -> new ByteArrayInputStream(bytes);

            loadAction.accept(key, value);

            Files.delete(path);
        } catch (IOException e) {

            throw PersistenceException.reading(key, e);
        }
    }
}

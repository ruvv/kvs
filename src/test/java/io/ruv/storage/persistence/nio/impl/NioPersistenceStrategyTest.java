package io.ruv.storage.persistence.nio.impl;


import io.ruv.storage.persistence.PersistenceException;
import io.ruv.storage.util.exception.ErrorCode;
import io.ruv.storage.util.properties.NioPersistenceProperties;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Execution(ExecutionMode.SAME_THREAD)
public class NioPersistenceStrategyTest {

    @TempDir
    private Path tempDir;

    private final NioPersistenceProperties properties = new NioPersistenceProperties();

    {
        properties.setBufferSize(4096);
    }

    private final NioPersistenceStrategy persistenceStrategy = new NioPersistenceStrategy(properties);

    private final String key = "key";
    private final String otherKey = "otherKey";
    private final byte[] value = "value".getBytes(StandardCharsets.UTF_8);
    private final byte[] otherValue = "otherValue".getBytes(StandardCharsets.UTF_8);

    @BeforeEach
    public void setTempDir() {

        properties.setBasePath(tempDir);
    }

    @Test
    public void emptyStreamResultsInEmptyStorage() {

        Stream<Map.Entry<String, Supplier<InputStream>>> emptyStream = Stream.of();

        persistenceStrategy.persist(emptyStream);

        Assertions.assertThat(properties.getBasePath()).isEmptyDirectory();
    }

    @Test
    public void nonEmptyStreamResultsInFilesWithData() throws IOException {

        Supplier<InputStream> supplier = () -> new ByteArrayInputStream(value);
        Supplier<InputStream> otherSupplier = () -> new ByteArrayInputStream(otherValue);

        val stream = Map.of(key, supplier, otherKey, otherSupplier)
                .entrySet().stream();

        persistenceStrategy.persist(stream);

        Assertions.assertThat(properties.getBasePath()).isNotEmptyDirectory();

        val file = properties.getBasePath().resolve(key);
        val otherFile = properties.getBasePath().resolve(otherKey);

        Assertions.assertThat(file).isNotEmptyFile();
        Assertions.assertThat(otherFile).isNotEmptyFile();

        val bytes = Files.readAllBytes(file);
        val otherBytes = Files.readAllBytes(otherFile);

        Assertions.assertThat(bytes).isEqualTo(value);
        Assertions.assertThat(otherBytes).isEqualTo(otherValue);
    }

    @Test
    public void persistNonExistingStorageThrowsException() throws IOException {

        Supplier<InputStream> supplier = () -> new ByteArrayInputStream(value);

        val stream = Map.of(key, supplier)
                .entrySet().stream();

        Files.delete(tempDir);

        Assertions.assertThatThrownBy(() -> persistenceStrategy.persist(stream))
                .isInstanceOf(PersistenceException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PERSISTENCE_CLEAN_STORAGE);
    }

    @Test
    public void emptyStorageLoadsNothing() {

        val resultMap = new HashMap<String, Supplier<InputStream>>();
        val spy = Mockito.spy(resultMap);

        persistenceStrategy.load(resultMap::put);

        Mockito.verify(spy, Mockito.never()).put(Mockito.any(), Mockito.any());

        Assertions.assertThat(resultMap).isEmpty();
    }

    @Test
    public void nonEmptyStorageLoadsRecords() throws IOException {

        val file = properties.getBasePath().resolve(key);
        val otherFile = properties.getBasePath().resolve(otherKey);

        Files.write(file, value, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        Files.write(otherFile, otherValue, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        val resultMap = new HashMap<String, Supplier<InputStream>>();

        persistenceStrategy.load(resultMap::put);

        Assertions.assertThat(resultMap).isNotEmpty();
        Assertions.assertThat(resultMap).containsKey(key);
        Assertions.assertThat(resultMap).containsKey(otherKey);
        Assertions.assertThat(resultMap.get(key).get()).hasBinaryContent(value);
        Assertions.assertThat(resultMap.get(otherKey).get()).hasBinaryContent(otherValue);
    }

    @Test
    public void loadNonExistingStorageThrowsException() throws IOException {

        Files.delete(tempDir);

        Assertions.assertThatThrownBy(() -> persistenceStrategy.load((k, v) -> {
                }))
                .isInstanceOf(PersistenceException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PERSISTENCE_READ_STORAGE);
    }
}

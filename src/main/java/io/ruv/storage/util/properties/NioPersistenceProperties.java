package io.ruv.storage.util.properties;

import io.ruv.storage.persistence.nio.impl.NioPersistenceStrategy;
import io.ruv.storage.util.exception.InitializationException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Externalized properties for file-based storage
 *
 * @see NioPersistenceStrategy
 */
@Data
@ConfigurationProperties("storage.persistence.nio")
public class NioPersistenceProperties {

    /**
     * Base filesystem path for nio file storage. Must be an existing directory.
     */
    private Path basePath;

    /**
     * Buffer size for i/o operations. Must be greater than zero.
     */
    private int bufferSize;

    @PostConstruct
    public void validate() {

        if (!Files.exists(basePath)) {

            throw new InitializationException(String.format("Directory '%s' does not exist.", basePath.toString()));
        }

        if (!Files.isDirectory(basePath)) {

            throw new InitializationException(String.format("File '%s' is not a directory.", basePath.toString()));
        }

        if (bufferSize <= 0) {

            throw new InitializationException(String.format("Illegal buffer size value '%d'. Must be greater than 0.", bufferSize));
        }
    }
}

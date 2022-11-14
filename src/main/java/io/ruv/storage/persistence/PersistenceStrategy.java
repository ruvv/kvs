package io.ruv.storage.persistence;

import java.io.InputStream;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Strategy for storage persistence between application restarts
 */
public interface PersistenceStrategy {

    /**
     * Saves key and associated value to persistent storage
     *
     * @param stream a stream of key-value pairs to persist
     * @throws PersistenceException when underlying persistence mechanism fails
     */
    void persist(Stream<Map.Entry<String, Supplier<InputStream>>> stream) throws PersistenceException;

    /**
     * Loads keys and associated values from persistent storage
     *
     * @param loadAction consumer callback to apply to each loaded key-value pair
     * @throws PersistenceException when underlying persistence mechanism fails
     */
    void load(BiConsumer<String, Supplier<InputStream>> loadAction) throws PersistenceException;
}

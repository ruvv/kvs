package io.ruv.service;

import java.io.InputStream;

/**
 * Key-value storage provider contract
 */
public interface StorageService {

    /**
     * Stores provided value
     *
     * @param key   key to associate provided value with
     * @param value value to store
     * @throws KeyExistsException when provided key is already associated with some value
     */
    void store(String key, byte[] value) throws KeyExistsException;

    /**
     * @param key key associated with requested value
     * @return stream with stored data
     * @throws KeyNotExistsException when provided key is not associated with a value
     */
    InputStream retrieve(String key) throws KeyNotExistsException;

    /**
     * @param key key associated with value to delete
     * @throws KeyNotExistsException when provided key is not associated with a value
     */
    void delete(String key) throws KeyNotExistsException;

    /**
     * Exception indicating key collision on store operation
     */
    class KeyExistsException extends RuntimeException {

        public KeyExistsException(String message) {
            super(message);
        }

        public KeyExistsException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception indicating missing key on retrieve and delete operations
     */
    class KeyNotExistsException extends RuntimeException {

        public KeyNotExistsException(String message) {
            super(message);
        }

        public KeyNotExistsException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

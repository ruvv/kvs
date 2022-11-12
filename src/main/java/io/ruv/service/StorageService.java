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
     * @throws DuplicateKeyException when provided key is already associated with some value
     */
    void store(String key, byte[] value) throws DuplicateKeyException;

    /**
     * @param key key associated with requested value
     * @return stream with stored data
     * @throws MissingKeyException when provided key is not associated with a value
     */
    InputStream retrieve(String key) throws MissingKeyException;

    /**
     * @param key key associated with value to delete
     * @throws MissingKeyException when provided key is not associated with a value
     */
    void delete(String key) throws MissingKeyException;

}

package io.ruv.storage.service;

import io.ruv.storage.persistence.PersistenceException;

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
     * Retrieves value associated with provided key
     *
     * @param key key associated with requested value
     * @return stream with stored data
     * @throws MissingKeyException when provided key is not associated with a value
     */
    InputStream retrieve(String key) throws MissingKeyException;

    /**
     * Removes value associated with provided key
     *
     * @param key key associated with value to delete
     * @throws MissingKeyException when provided key is not associated with a value
     */
    void delete(String key) throws MissingKeyException;

    /**
     * Saves all key-value associations to persistent storage
     *
     * @throws PersistenceException when underlying persistence mechanism fails
     * @see io.ruv.storage.persistence.PersistenceStrategy
     */
    void save() throws PersistenceException;

    /**
     * Loads all key-value associations from persistent storage
     *
     * @throws PersistenceException when underlying persistence mechanism fails
     * @see io.ruv.storage.persistence.PersistenceStrategy
     */
    void load() throws PersistenceException;
}

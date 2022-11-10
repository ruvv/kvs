package io.ruv.service;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Implementation of {@link StorageService} with {@link ConcurrentHashMap} as a backing storage
 */
@Slf4j
public class HashStorageService implements StorageService {

    private final ConcurrentHashMap<String, Supplier<InputStream>> storage = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void store(String key, byte[] value) throws KeyExistsException {

        Supplier<InputStream> wrapper = () -> new ByteArrayInputStream(value);

        if (wrapper != this.storage.putIfAbsent(key, wrapper)) {

            throw new KeyExistsException(String.format("Key '%s' is already associated with a value.", key));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream retrieve(String key) throws KeyNotExistsException {

        Supplier<InputStream> wrapper = storage.get(key);

        if (wrapper != null) {

            return wrapper.get();
        } else {

            throw new KeyNotExistsException(String.format("Key '%s' is not associated with a value.", key));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) throws KeyNotExistsException {

        Supplier<InputStream> wrapper = storage.remove(key);

        if (wrapper == null) {

            throw new KeyNotExistsException(String.format("Key '%s' is not associated with a value.", key));
        }
    }
}

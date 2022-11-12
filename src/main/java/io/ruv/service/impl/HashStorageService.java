package io.ruv.service.impl;

import io.ruv.service.DuplicateKeyException;
import io.ruv.service.MissingKeyException;
import io.ruv.service.StorageService;
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
    public void store(String key, byte[] value) throws DuplicateKeyException {

        Supplier<InputStream> wrapper = () -> new ByteArrayInputStream(value);

        if (this.storage.putIfAbsent(key, wrapper) != null) {

            throw DuplicateKeyException.of(key);
        }
        log.debug("Store on key '{}'.", key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream retrieve(String key) throws MissingKeyException {

        Supplier<InputStream> wrapper = storage.get(key);

        if (wrapper != null) {

            log.debug("Retrieve on key '{}'.", key);
            return wrapper.get();
        } else {

            throw MissingKeyException.of(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) throws MissingKeyException {

        Supplier<InputStream> wrapper = storage.remove(key);

        if (wrapper == null) {

            throw MissingKeyException.of(key);
        }
        log.debug("Delete on key '{}'.", key);
    }
}

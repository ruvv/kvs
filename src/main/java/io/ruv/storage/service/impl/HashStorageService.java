package io.ruv.storage.service.impl;

import io.ruv.storage.persistence.PersistenceException;
import io.ruv.storage.persistence.PersistenceStrategy;
import io.ruv.storage.service.DuplicateKeyException;
import io.ruv.storage.service.MissingKeyException;
import io.ruv.storage.service.StorageService;
import io.ruv.storage.util.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Implementation of {@link StorageService} with {@link ConcurrentHashMap} as a backing storage
 */
@Slf4j
@RequiredArgsConstructor
public class HashStorageService implements StorageService {

    private final PersistenceStrategy persistenceStrategy;

    private final ConcurrentHashMap<String, Supplier<InputStream>> storage = new ConcurrentHashMap<>();

    private final Runnable noop = () -> {
    };

    /**
     * Action to execute before internal storage access.
     * Currently used only to circuit break requests while saving/loading.
     */
    private final AtomicReference<Runnable> preAccessAction = new AtomicReference<>(noop);

    /**
     * {@inheritDoc}
     */
    @Override
    public void store(String key, byte[] value) throws DuplicateKeyException {

        preAccessAction.get().run();

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

        preAccessAction.get().run();

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

        preAccessAction.get().run();

        Supplier<InputStream> wrapper = storage.remove(key);

        if (wrapper == null) {

            throw MissingKeyException.of(key);
        }
        log.debug("Delete on key '{}'.", key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() throws PersistenceException, ServiceUnavailableException {

        preAccessAction.set(this::unavailableWhileSaving);
        persistenceStrategy.persist(storage.entrySet().stream());
        preAccessAction.set(noop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() throws PersistenceException, ServiceUnavailableException {

        preAccessAction.set(this::unavailableWhileLoading);
        persistenceStrategy.load(storage::put);
        preAccessAction.set(noop);
    }

    private void unavailableWhileSaving() {

        throw new ServiceUnavailableException("Save operation in progress.");
    }

    private void unavailableWhileLoading() {

        throw new ServiceUnavailableException("Load operation in progress.");
    }
}

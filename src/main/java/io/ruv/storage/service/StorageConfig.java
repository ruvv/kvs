package io.ruv.storage.service;

import io.ruv.storage.persistence.PersistenceStrategy;
import io.ruv.storage.service.impl.HashStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    public StorageService hashStorageService(PersistenceStrategy persistenceStrategy) {

        return new HashStorageService(persistenceStrategy);
    }
}

package io.ruv.storage.persistence;

import io.ruv.storage.persistence.nio.impl.NioPersistenceStrategy;
import io.ruv.storage.util.properties.NioPersistenceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NioPersistenceProperties.class)
public class PersistenceConfig {

    @Bean
    public PersistenceStrategy persistenceStrategy(NioPersistenceProperties properties) {

        return new NioPersistenceStrategy(properties);
    }
}

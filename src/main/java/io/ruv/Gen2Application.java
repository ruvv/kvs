package io.ruv;

import io.ruv.service.StorageService;
import io.ruv.service.impl.HashStorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Gen2Application {

    public static void main(String[] args) {

        SpringApplication.run(Gen2Application.class, args);
    }

    @Bean
    public StorageService hashStorageService() {

        return new HashStorageService();
    }
}

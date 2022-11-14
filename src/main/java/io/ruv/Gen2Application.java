package io.ruv;

import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@SpringBootApplication
public class Gen2Application {

    public static void main(String[] args) {

        SpringApplication.run(Gen2Application.class, args);
    }

    @Bean
    public MessageSource messageSource() {

        val messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.addBasenames("classpath:storage-messages");
        messageSource.setConcurrentRefresh(true);
        messageSource.setCacheSeconds(5);
        return messageSource;
    }
}

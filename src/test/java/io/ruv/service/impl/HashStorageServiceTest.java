package io.ruv.service.impl;

import io.ruv.service.DuplicateKeyException;
import io.ruv.service.MissingKeyException;
import io.ruv.util.ErrorCode;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
public class HashStorageServiceTest {

    private final HashStorageService hashStorageService = new HashStorageService();

    public final ConcurrentHashMap<String, Supplier<InputStream>> internalStorage =
            (ConcurrentHashMap) ReflectionTestUtils.getField(hashStorageService, "storage");

    private final String key = "key";
    private final String otherKey = "otherKey";
    private final byte[] value = "value".getBytes(StandardCharsets.UTF_8);
    private final byte[] otherValue = "otherValue".getBytes(StandardCharsets.UTF_8);

    @AfterEach
    public void cleanup() {

        internalStorage.clear();
    }

    @Test
    public void storeStoresValue() throws IOException {

        hashStorageService.store(key, value);
        hashStorageService.store(otherKey, otherValue);

        try (val stream = internalStorage.get(key).get()) {

            byte[] readBytes = stream.readAllBytes();

            Assertions.assertThat(readBytes).isEqualTo(value);
        }

        try (val stream = internalStorage.get(otherKey).get()) {

            byte[] readBytes = stream.readAllBytes();

            Assertions.assertThat(readBytes).isEqualTo(otherValue);
        }
    }

    @Test
    public void storeDuplicateKeyThrowsException() {

        hashStorageService.store(key, value);

        Assertions.assertThatThrownBy(() -> hashStorageService.store(key, value))
                .isInstanceOf(DuplicateKeyException.class)
                .hasFieldOrPropertyWithValue("key", key)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_KEY);
    }

    @Test
    public void retrieveRetrievesValue() throws IOException {

        internalStorage.put(key, () -> new ByteArrayInputStream(value));
        internalStorage.put(otherKey, () -> new ByteArrayInputStream(otherValue));

        try (val stream = hashStorageService.retrieve(key)) {

            Assertions.assertThat(stream.readAllBytes()).isEqualTo(value);
        }

        try (val stream = hashStorageService.retrieve(otherKey)) {

            Assertions.assertThat(stream.readAllBytes()).isEqualTo(otherValue);
        }
    }

    @Test
    public void retrieveMissingThrowsException() {

        Assertions.assertThatThrownBy(() -> hashStorageService.retrieve(key))
                .isInstanceOf(MissingKeyException.class)
                .hasFieldOrPropertyWithValue("key", key)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MISSING_KEY);
    }

    @Test
    public void deleteDeletesRecord() {

        internalStorage.put(key, () -> new ByteArrayInputStream(value));
        internalStorage.put(otherKey, () -> new ByteArrayInputStream(otherValue));

        hashStorageService.delete(key);
        hashStorageService.delete(otherKey);

        Assertions.assertThat(internalStorage.containsKey(key)).isFalse();
        Assertions.assertThat(internalStorage.containsKey(otherKey)).isFalse();
    }

    @Test
    public void deleteMissingThrowsException() {

        Assertions.assertThatThrownBy(() -> hashStorageService.delete(key))
                .isInstanceOf(MissingKeyException.class)
                .hasFieldOrPropertyWithValue("key", key)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MISSING_KEY);
    }
}

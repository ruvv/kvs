package io.ruv.storage.web.conroller;

import io.ruv.storage.persistence.PersistenceException;
import io.ruv.storage.service.DuplicateKeyException;
import io.ruv.storage.service.MissingKeyException;
import io.ruv.storage.service.StorageService;
import io.ruv.storage.util.exception.ErrorCode;
import io.ruv.storage.web.controller.StorageController;
import lombok.val;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@WebMvcTest(StorageController.class)
public class StorageControllerTest {

    @MockBean
    private StorageService storageService;

    @Autowired
    private MockMvc mockMvc;

    private final String key = "key";
    private final byte[] value = "value".getBytes(StandardCharsets.UTF_8);
    private final String save = "/api/storage/save";
    private final String load = "/api/storage/load";
    private final String resource = "/api/storage/" + key;

    @Test
    public void storeReturnsOkNoBody() throws Exception {

        Mockito.doNothing()
                .when(storageService).store(key, value);


        mockMvc.perform(MockMvcRequestBuilders.put(resource).content(value))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Mockito.verify(storageService).store(key, value);
    }

    @Test
    public void storeDuplicateReturnsBadRequestError() throws Exception {

        Mockito.doThrow(DuplicateKeyException.of(key))
                .when(storageService).store(key, value);

        mockMvc.perform(MockMvcRequestBuilders.put(resource).content(value))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value(ErrorCode.DUPLICATE_KEY.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(StringContains.containsString(key)));

        Mockito.verify(storageService).store(key, value);
    }

    @Test
    public void getReturnsOkValue() throws Exception {

        try (val stream = new ByteArrayInputStream(value)) {

            //noinspection resource
            Mockito.doReturn(stream)
                    .when(storageService).retrieve(key);

            mockMvc.perform(MockMvcRequestBuilders.get(resource))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("value"));

            //noinspection resource
            Mockito.verify(storageService).retrieve(key);
        }
    }

    @Test
    public void getMissingReturnsBadRequest() throws Exception {

        //noinspection resource
        Mockito.doThrow(MissingKeyException.of(key))
                .when(storageService).retrieve(key);

        mockMvc.perform(MockMvcRequestBuilders.get(resource))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value(ErrorCode.MISSING_KEY.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(StringContains.containsString(key)));

        //noinspection resource
        Mockito.verify(storageService).retrieve(key);
    }


    @Test
    public void deleteReturnsNoContentNoBody() throws Exception {

        Mockito.doNothing()
                .when(storageService).delete(key);

        mockMvc.perform(MockMvcRequestBuilders.delete(resource))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Mockito.verify(storageService).delete(key);
    }

    @Test
    public void deleteMissingReturnsBadRequest() throws Exception {

        Mockito.doThrow(MissingKeyException.of(key))
                .when(storageService).delete(key);

        mockMvc.perform(MockMvcRequestBuilders.delete(resource))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value(ErrorCode.MISSING_KEY.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(StringContains.containsString(key)));

        Mockito.verify(storageService).delete(key);
    }

    @Test
    public void saveReturnsOkNoBody() throws Exception {

        Mockito.doNothing()
                .when(storageService).save();

        mockMvc.perform(MockMvcRequestBuilders.post(save))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Mockito.verify(storageService).save();
    }

    @Test
    public void saveProblemReturnsInternalServerError() throws Exception {

        Mockito.doThrow(PersistenceException.cleaningStorage(new Exception()))
                .when(storageService).save();

        mockMvc.perform(MockMvcRequestBuilders.post(save))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value(ErrorCode.PERSISTENCE_CLEAN_STORAGE.name()));


        Mockito.verify(storageService).save();
    }

    @Test
    public void loadReturnsOkNoBody() throws Exception {

        Mockito.doNothing()
                .when(storageService).load();

        mockMvc.perform(MockMvcRequestBuilders.post(load))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Mockito.verify(storageService).load();
    }

    @Test
    public void loadProblemReturnsInternalServerError() throws Exception {

        Mockito.doThrow(PersistenceException.readingStorage(new Exception()))
                .when(storageService).load();

        mockMvc.perform(MockMvcRequestBuilders.post(load))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value(ErrorCode.PERSISTENCE_READ_STORAGE.name()));

        Mockito.verify(storageService).load();
    }
}

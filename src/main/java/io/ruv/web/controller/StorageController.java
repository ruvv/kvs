package io.ruv.web.controller;

import io.ruv.service.StorageService;
import io.ruv.util.FrontalExceptionSupport;
import io.ruv.web.dto.ErrorWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/storage")
public class StorageController {

    private final StorageService storageService;
    private final MessageSource messageSource;

    @GetMapping("/{key}")
    public ResponseEntity<InputStreamResource> retrieve(@PathVariable String key) {

        return ResponseEntity.ok()
                .body(new InputStreamResource(storageService.retrieve(key)));
    }


    @PutMapping("/{key}")
    public ResponseEntity<?> store(@PathVariable String key, @RequestBody byte[] content) {

        storageService.store(key, content);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> delete(@PathVariable String key) {

        storageService.delete(key);
        return ResponseEntity.noContent().build();
    }

    //todo move to advice handler
    @ExceptionHandler(FrontalExceptionSupport.class)
    public ResponseEntity<ErrorWrapperDto> frontalError(FrontalExceptionSupport ex) {

        val errorWrapper = ex.makeErrorWrapper(messageSource);

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorWrapper);
    }
}

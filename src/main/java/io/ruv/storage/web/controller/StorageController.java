package io.ruv.storage.web.controller;

import io.ruv.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/storage")
public class StorageController {

    private final StorageService storageService;

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
}

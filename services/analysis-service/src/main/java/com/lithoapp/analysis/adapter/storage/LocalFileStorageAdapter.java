package com.lithoapp.analysis.adapter.storage;

import com.lithoapp.analysis.exception.StorageException;
import com.lithoapp.analysis.port.FileStoragePort;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * FileStoragePort adapter that stores files on the local filesystem.
 *
 * Storage key format: "{uuid}{extension}"  (e.g. "a3f8c2d1-….pdf")
 * Files are stored flat under {@code storage.local.base-path}.
 *
 * Swap this adapter for an S3 implementation by:
 *   1. Creating a class that implements FileStoragePort.
 *   2. Replacing this @Component with a @ConditionalOnProperty guard,
 *      or wiring the desired bean explicitly in a @Configuration class.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageAdapter implements FileStoragePort {

    @Value("${storage.local.base-path:./uploads}")
    private String basePath;

    private Path storageRoot;

    @PostConstruct
    public void init() {
        storageRoot = Paths.get(basePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageRoot);
            log.info("File storage initialised at: {}", storageRoot);
        } catch (IOException e) {
            throw new StorageException("Could not create storage directory: " + storageRoot, e);
        }
    }

    @Override
    public String store(byte[] content, String suggestedFilename) {
        String extension = extractExtension(suggestedFilename);
        String key = UUID.randomUUID() + extension;
        Path target = storageRoot.resolve(key);
        try {
            Files.write(target, content);
            log.debug("Stored file: {}", target);
            return key;
        } catch (IOException e) {
            throw new StorageException("Failed to store file: " + key, e);
        }
    }

    @Override
    public byte[] retrieve(String storageKey) {
        Path file = storageRoot.resolve(storageKey).normalize();
        guardPath(file);
        if (!Files.exists(file)) {
            throw new StorageException("File not found for storage key: " + storageKey);
        }
        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new StorageException("Failed to read file: " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) {
        if (storageKey == null) return;
        Path file = storageRoot.resolve(storageKey).normalize();
        guardPath(file);
        try {
            Files.deleteIfExists(file);
            log.debug("Deleted file: {}", file);
        } catch (IOException e) {
            // Log but do not rethrow — a failed delete of an old file
            // must not roll back the replacement operation.
            log.warn("Could not delete file {}: {}", file, e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Prevent path-traversal attacks by ensuring the resolved path stays inside storageRoot. */
    private void guardPath(Path resolved) {
        if (!resolved.startsWith(storageRoot)) {
            throw new StorageException("Illegal storage key (path traversal attempt).");
        }
    }

    private static String extractExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot) : "";
    }
}

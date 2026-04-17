package com.lithoapp.analysis.adapter.storage;

import com.lithoapp.analysis.exception.StorageException;
import com.lithoapp.analysis.port.FileStoragePort;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * FileStoragePort adapter backed by MinIO Community (S3-compatible object storage).
 *
 * Storage key format: "{uuid}{extension}"  (e.g. "a3f8c2d1-….pdf")
 * Objects are stored in the bucket configured via {@code storage.minio.bucket}.
 *
 * Active when: storage.type=minio in application.properties.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "minio")
@RequiredArgsConstructor
public class MinioFileStorageAdapter implements FileStoragePort {

    private final MinioClient minioClient;

    @Value("${storage.minio.bucket}")
    private String bucket;

    @Override
    public String store(byte[] content, String suggestedFilename) {
        String key = UUID.randomUUID() + extractExtension(suggestedFilename);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(new ByteArrayInputStream(content), content.length, -1)
                    .contentType("application/pdf")
                    .build());
            log.debug("Stored object in MinIO: bucket={} key={}", bucket, key);
            return key;
        } catch (Exception e) {
            throw new StorageException("Failed to store object in MinIO: " + key, e);
        }
    }

    @Override
    public byte[] retrieve(String storageKey) {
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(storageKey)
                .build())) {
            return stream.readAllBytes();
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                throw new StorageException("File not found in MinIO: " + storageKey);
            }
            throw new StorageException("Failed to retrieve object from MinIO: " + storageKey, e);
        } catch (Exception e) {
            throw new StorageException("Failed to retrieve object from MinIO: " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) {
        if (storageKey == null) return;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(storageKey)
                    .build());
            log.debug("Deleted object from MinIO: bucket={} key={}", bucket, storageKey);
        } catch (Exception e) {
            // Log but do not rethrow — a failed delete of an old file
            // must not roll back the replacement operation.
            log.warn("Could not delete object {} from MinIO: {}", storageKey, e.getMessage());
        }
    }

    private static String extractExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot) : "";
    }
}

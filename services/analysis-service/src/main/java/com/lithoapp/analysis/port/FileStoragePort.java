package com.lithoapp.analysis.port;

/**
 * Hexagonal port for file storage.
 *
 * The active adapter is wired via Spring configuration.
 * Current adapter: {@code LocalFileStorageAdapter} (filesystem).
 * Future adapters: S3, MinIO, Azure Blob — implement this interface and swap
 * the @Bean in StorageConfig without touching any service code.
 */
public interface FileStoragePort {

    /**
     * Persist the given bytes and return a storage key that can be used
     * to retrieve or delete the file later.
     *
     * @param content           raw file bytes
     * @param suggestedFilename original filename (used for extension detection)
     * @return an opaque storage key (e.g. a relative path or UUID-based key)
     */
    String store(byte[] content, String suggestedFilename);

    /**
     * Retrieve the bytes associated with the given storage key.
     *
     * @throws com.lithoapp.analysis.exception.StorageException if the key does not exist
     */
    byte[] retrieve(String storageKey);

    /**
     * Delete the file associated with the given storage key.
     * No-op (and no exception) if the key is not found.
     */
    void delete(String storageKey);
}

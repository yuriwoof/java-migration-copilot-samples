package com.microsoft.migration.assets.service;

import com.microsoft.migration.assets.constants.StorageConstants;
import com.microsoft.migration.assets.model.ImageProcessingMessage;
import com.microsoft.migration.assets.model.BlobStorageItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Interface for storage operations that can be implemented by different storage providers
 * (Azure Blob Storage, local file system, etc.)
 */
public interface StorageService {
    
    /**
     * List all objects in storage
     */
    List<BlobStorageItem> listObjects();
    
    /**
     * Upload file to storage
     */
    void uploadObject(MultipartFile file) throws IOException;
    
    /**
     * Get object from storage by key
     */
    InputStream getObject(String key) throws IOException;

    /**
     * Delete object from storage by key
     */
    void deleteObject(String key) throws IOException;

    /**
     * Get the storage type (blob or local)
     */
    String getStorageType();

    /**
     * Get the thumbnail key for a given key
     */
    default String getThumbnailKey(String key) {
        int dotIndex = key.lastIndexOf('.');
        if (dotIndex > 0) {
            return key.substring(0, dotIndex) + "_thumbnail" + key.substring(dotIndex);
        }
        return key + "_thumbnail";
    }

    /**
     * Generate a URL for viewing the object
     */
    default String generateUrl(String key) {
        return "/" + StorageConstants.STORAGE_PATH + "/view/" + key;
    }
}
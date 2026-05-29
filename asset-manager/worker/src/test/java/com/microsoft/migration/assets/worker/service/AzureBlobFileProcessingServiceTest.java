package com.microsoft.migration.assets.worker.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobProperties;
import com.microsoft.migration.assets.worker.repository.ImageMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AzureBlobFileProcessingServiceTest {

    @Mock
    private BlobServiceClient blobServiceClient;

    @Mock
    private BlobContainerClient blobContainerClient;

    @Mock
    private BlobClient blobClient;

    @Mock
    private ImageMetadataRepository imageMetadataRepository;

    @InjectMocks
    private AzureBlobFileProcessingService azureBlobFileProcessingService;

    private final String containerName = "test-container";
    private final String testKey = "test-image.jpg";
    private final String thumbnailKey = "test-image_thumbnail.jpg";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(azureBlobFileProcessingService, "containerName", containerName);
    }

    @Test
    void getStorageTypeReturnsBlob() {
        // Act
        String result = azureBlobFileProcessingService.getStorageType();

        // Assert
        assertEquals("blob", result);
    }

    @Test
    void downloadOriginalDownloadsFileFromBlob() throws Exception {
        // Arrange
        Path tempFile = Files.createTempFile("download-", ".tmp");
        when(blobServiceClient.getBlobContainerClient(containerName)).thenReturn(blobContainerClient);
        when(blobContainerClient.getBlobClient(testKey)).thenReturn(blobClient);
        when(blobClient.downloadToFile(anyString(), anyBoolean())).thenReturn(mock(BlobProperties.class));

        // Act
        azureBlobFileProcessingService.downloadOriginal(testKey, tempFile);

        // Assert
        verify(blobClient).downloadToFile(tempFile.toString(), true);

        // Clean up
        Files.deleteIfExists(tempFile);
    }

    @Test
    void uploadThumbnailUploadsFileToBlob() throws Exception {
        // Arrange
        Path tempFile = Files.createTempFile("thumbnail-", ".tmp");
        when(blobServiceClient.getBlobContainerClient(containerName)).thenReturn(blobContainerClient);
        when(blobContainerClient.getBlobClient(thumbnailKey)).thenReturn(blobClient);
        doNothing().when(blobClient).uploadFromFile(anyString(), isNull(), any(), isNull(), isNull(), isNull(), isNull());
        when(imageMetadataRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        azureBlobFileProcessingService.uploadThumbnail(tempFile, thumbnailKey, "image/jpeg");

        // Assert
        verify(blobClient).uploadFromFile(eq(tempFile.toString()), isNull(), any(), isNull(), isNull(), isNull(), isNull());

        // Clean up
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testExtractOriginalKey() throws Exception {
        // Use reflection to test private method
        String result = (String) ReflectionTestUtils.invokeMethod(
                azureBlobFileProcessingService,
                "extractOriginalKey",
                "image_thumbnail.jpg");

        // Assert
        assertEquals("image.jpg", result);
    }
}

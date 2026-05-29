package com.microsoft.migration.assets.worker.config;

import com.azure.core.credential.TokenCredential;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobConfig {

    @Value("${blob.storage.endpoint}")
    private String endpoint;

    private final TokenCredential tokenCredential;

    public AzureBlobConfig(TokenCredential tokenCredential) {
        this.tokenCredential = tokenCredential;
    }

    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder()
                .endpoint(endpoint)
                .credential(tokenCredential)
                .buildClient();
    }
}

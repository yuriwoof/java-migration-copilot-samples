package com.microsoft.migration.assets.worker.config;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

@Configuration
public class AzureCredentialConfig {

    @Bean
    @Primary
    public TokenCredential applicationTokenCredential(
            @Value("${spring.cloud.azure.credential.client-id:}") String managedIdentityClientId) {
        DefaultAzureCredentialBuilder builder = new DefaultAzureCredentialBuilder();
        if (StringUtils.hasText(managedIdentityClientId)) {
            builder.managedIdentityClientId(managedIdentityClientId);
        }
        return builder.build();
    }
}
package com.knuissant.dailyq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class NcpConfig {

    @Value("${ncp.storage.endpoint}")
    private String storageEndpoint;

    @Value("${ncp.storage.region}")
    private String storageRegion;

    @Value("${ncp.storage.access-key}")
    private String storageAccessKey;

    @Value("${ncp.storage.secret-key}")
    private String storageSecretKey;

    // Object Storage
    @Bean
    public AmazonS3 ncpObjectStorageClient() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(storageEndpoint, storageRegion))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(storageAccessKey, storageSecretKey)))
                .build();
    }
}

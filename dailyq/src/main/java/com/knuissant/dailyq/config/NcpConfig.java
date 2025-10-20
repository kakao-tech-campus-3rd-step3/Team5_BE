package com.knuissant.dailyq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import lombok.Getter;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Getter
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
    @Value("${ncp.storage.bucket-name}")
    private String bucketName;

    @Value("${ncp.clova.stt.invoke-url}")
    private String clovaInvokeUrl;
    @Value("${ncp.clova.stt.secret-key}")
    private String clovaSecretKey;
    @Value("${ncp.clova.stt.callback-server}")
    private String clovaCallbackServer;

    // Object Storage
    @Bean
    public AmazonS3 ncpObjectStorageClient() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(storageEndpoint, storageRegion))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(storageAccessKey, storageSecretKey)))
                .build();
    }

    // CLOVA speech
    @Bean
    public RestClient ncpClovaRestClient() {
        return RestClient.builder()
                .baseUrl(clovaInvokeUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-CLOVASPEECH-API-KEY", clovaSecretKey)
                .build();
    }
}

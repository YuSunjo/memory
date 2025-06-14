package com.memory.config.storage;

import com.memory.component.storage.S3Component;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class S3Config {

    private final S3Component s3Component;

    public S3Config(S3Component s3Component) {
        this.s3Component = s3Component;
    }

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                s3Component.getCredentials().getAccessKey(),
                s3Component.getCredentials().getSecretKey()
        );

        return S3Client.builder()
                .endpointOverride(URI.create(s3Component.getS3().getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}

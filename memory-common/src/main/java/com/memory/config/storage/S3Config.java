package com.memory.config.storage;

import com.memory.component.storage.S3Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@ConditionalOnProperty(prefix = "cloud.aws", name = "enabled", havingValue = "true")
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
                .region(Region.of(s3Component.getRegion().getStatic()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}

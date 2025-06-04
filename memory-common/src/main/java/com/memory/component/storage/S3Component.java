package com.memory.component.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cloud.aws")
@Getter
@Setter
public class S3Component {

    private Credentials credentials;
    private Region region;
    private S3 s3;
    private Stack stack;

    @Getter
    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class Region {
        private String static_;

        // Getter method to match the property name in application.yml
        public String getStatic() {
            return static_;
        }

        // Setter method to match the property name in application.yml
        public void setStatic(String staticValue) {
            this.static_ = staticValue;
        }
    }

    @Getter
    @Setter
    public static class S3 {
        private String bucket;
        private String endpoint;
    }

    @Getter
    @Setter
    public static class Stack {
        private boolean auto;
    }
}

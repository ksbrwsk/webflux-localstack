package de.ksbrwsk.localstack;

import io.awspring.cloud.core.region.StaticRegionProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Configuration class for AWS S3.
 * This class sets up the necessary AWS S3 credentials and configurations.
 */
@Configuration
@Data
@Slf4j
public class AwsS3Config {

    /**
     * AWS region.
     */
    @Value("${cloud.aws.region}")
    private String region;

    /**
     * AWS S3 endpoint URL.
     */
    @Value("${cloud.aws.s3.url}")
    private String s3EndpointUrl;

    /**
     * AWS S3 bucket name.
     */
    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    /**
     * AWS S3 access key.
     */
    @Value("${cloud.aws.s3.access-key}")
    private String accessKey;

    /**
     * AWS S3 secret key.
     */
    @Value("${cloud.aws.s3.secret-key}")
    private String secretKey;

    @Bean
    public S3Client s3Client(AwsCredentialsProvider customAwsCredentialsProvider) {
        return S3Client.builder()
                .credentialsProvider(customAwsCredentialsProvider)
                .region(Region.EU_CENTRAL_1)
                .endpointOverride(URI.create(this.s3EndpointUrl))
                .build();
    }

    @Bean
    public StaticRegionProvider regionProvider() {
        return new StaticRegionProvider(this.region);
    }

    /**
     * Bean for custom AWS credentials provider.
     *
     * @return AwsCredentialsProvider instance with custom AWS credentials.
     */
    @Bean
    public AwsCredentialsProvider customAwsCredentialsProvider() {
        return new CustomAWSCredentialsProvider(this.accessKey, this.secretKey);
    }

    /**
     * Custom AWS credentials provider class.
     * This class provides AWS credentials using the provided access key and secret key.
     */
    protected static class CustomAWSCredentialsProvider implements AwsCredentialsProvider {

        private final String accessKey;
        private final String secretKey;

        /**
         * Constructor for CustomAWSCredentialsProvider.
         *
         * @param accessKey AWS access key.
         * @param secretKey AWS secret key.
         */
        public CustomAWSCredentialsProvider(String accessKey, String secretKey) {
            this.accessKey = accessKey;
            this.secretKey = secretKey;
        }

        /**
         * Resolves and returns AWS credentials.
         *
         * @return AwsBasicCredentials instance with the provided access key and secret key.
         */
        @Override
        public AwsBasicCredentials resolveCredentials() {
            boolean isAwsCredentialsSet = this.accessKey != null
                    && this.secretKey != null;
            log.info("AWS Credentials set? {}", isAwsCredentialsSet);
            return AwsBasicCredentials.create(this.accessKey, this.secretKey);
        }
    }
}
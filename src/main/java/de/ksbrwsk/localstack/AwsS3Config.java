package de.ksbrwsk.localstack;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for AWS S3.
 * This class is responsible for creating and configuring the AmazonS3 bean.
 */
@Configuration
@Data
public class AwsS3Config {

    // AWS region
    @Value("${cloud.aws.region}")
    private String region;

    // AWS S3 endpoint URL
    @Value("${cloud.aws.s3.url}")
    private String s3EndpointUrl;

    // AWS S3 bucket name
    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    // AWS S3 access key
    @Value("${cloud.aws.s3.access-key}")
    private String accessKey;

    // AWS S3 secret key
    @Value("${cloud.aws.s3.secret-key}")
    private String secretKey;

    /**
     * Creates and configures the AmazonS3 bean.
     * @return AmazonS3 instance
     */
    @Bean(name = "amazonS3")
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(getCredentialsProvider())
                .withEndpointConfiguration(getEndpointConfiguration(s3EndpointUrl))
                .build();
    }

    /**
     * Creates an EndpointConfiguration with the provided URL and the region.
     * @param url The URL for the endpoint
     * @return EndpointConfiguration instance
     */
    private EndpointConfiguration getEndpointConfiguration(String url) {
        return new EndpointConfiguration(url, region);
    }

    /**
     * Creates an AWSStaticCredentialsProvider with the access key and secret key.
     * @return AWSStaticCredentialsProvider instance
     */
    private AWSStaticCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(getBasicAWSCredentials());
    }

    /**
     * Creates a BasicAWSCredentials with the access key and secret key.
     * @return BasicAWSCredentials instance
     */
    private BasicAWSCredentials getBasicAWSCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }
}
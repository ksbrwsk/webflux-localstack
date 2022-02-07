package de.ksbrwsk.localstack;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
class AwsS3ServiceTest {

    private static final String EXAMPLE_TXT = "example_txt.txt";
    private static final String EXAMPLE_CSV = "example_csv.csv";

    private static AmazonS3 amazonS3;
    private static AwsS3Service awsS3Service;

    @Container
    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14.0"))
                    .withServices(S3)
                    .withReuse(true);

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localStack::getSecretKey);
    }

    @BeforeAll
    public static void setup() {
        amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(localStack.getEndpointConfiguration(S3))
                .build();
        amazonS3.createBucket("sabo-s3-bucket");
        awsS3Service = new AwsS3Service(amazonS3);
    }

    @Test
    void uploadObjectToS3() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new ClassPathResource(EXAMPLE_TXT).getFile());
        String etag = awsS3Service.uploadObjectToS3(EXAMPLE_TXT, bytes);
        assertNotNull(etag);
        System.out.println("ETag:" + etag);
    }

    @Test
    void listObjects() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new ClassPathResource(EXAMPLE_TXT).getFile());
        awsS3Service.uploadObjectToS3(EXAMPLE_TXT, bytes);
        List<S3ObjectSummary> s3ObjectSummaries = awsS3Service.listObjects();
        System.out.println(s3ObjectSummaries);
        assertFalse(s3ObjectSummaries.isEmpty());
    }

    @Test
    void downloadFileFromS3Bucket() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new ClassPathResource(EXAMPLE_CSV).getFile());
        awsS3Service.uploadObjectToS3(EXAMPLE_CSV, bytes);
        S3ObjectInputStream s3ObjectInputStream = awsS3Service.downloadFileFromS3Bucket(EXAMPLE_CSV);
        byte[] read = s3ObjectInputStream.readAllBytes();
        String content = new String(read, StandardCharsets.UTF_8);
        assertNotNull(content);
        System.out.println(content);
    }
}
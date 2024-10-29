package de.ksbrwsk.localstack;

import io.awspring.cloud.s3.S3Resource;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AwsS3ServiceTest {

    private static final String EXAMPLE_TXT = "example_txt.txt";
    private static final String EXAMPLE_CSV = "example_csv.csv";

    @Autowired
    AwsS3Service awsS3Service;

    @Container
    static final LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.8.1"))
                    .withServices(S3);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.region.static", localstack::getRegion);
        registry.add("cloud.aws.credentials.access-key", localstack::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localstack::getSecretKey);
        registry.add("cloud.aws.region", localstack::getRegion);
        registry.add("cloud.aws.s3.url", () -> localstack.getEndpointOverride(S3).toString());
    }

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        localstack.execInContainer("awslocal", "s3api", "create-bucket", "--bucket", "sabo-s3-bucket");
    }

    @Test
    void uploadObjectToS3() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new ClassPathResource(EXAMPLE_TXT).getFile());
        String filename = awsS3Service.uploadObjectToS3(EXAMPLE_TXT, bytes);
        assertNotNull(filename);
        System.out.println("Filename:" + filename);
    }

    @Test
    void listObjects() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new ClassPathResource(EXAMPLE_TXT).getFile());
        awsS3Service.uploadObjectToS3(EXAMPLE_TXT, bytes);
        List<String> s3ObjectSummaries = awsS3Service.listObjects();
        System.out.println(s3ObjectSummaries);
        assertFalse(s3ObjectSummaries.isEmpty());
    }

    @Test
    void downloadFileFromS3Bucket() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new ClassPathResource(EXAMPLE_CSV).getFile());
        awsS3Service.uploadObjectToS3(EXAMPLE_CSV, bytes);
        S3Resource s3Resource = awsS3Service.downloadFileFromS3Bucket(EXAMPLE_CSV);
        byte[] read = s3Resource.getInputStream().readAllBytes();
        String content = new String(read, StandardCharsets.UTF_8);
        assertNotNull(content);
        System.out.println(content);
    }
}
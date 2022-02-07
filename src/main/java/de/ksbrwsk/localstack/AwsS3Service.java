package de.ksbrwsk.localstack;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsS3Service {
    public static final String BUCKET="sabo-s3-bucket";

    private final AmazonS3 amazonS3;

    public String uploadObjectToS3(String fileName, byte[] fileData) {
        log.info("Uploading file '{}' to bucket: '{}' ", fileName, BUCKET);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileData);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileData.length);
        PutObjectResult putObjectResult =
                amazonS3.putObject(BUCKET, fileName, byteArrayInputStream, objectMetadata);
        return putObjectResult.getETag();
    }

    public S3ObjectInputStream downloadFileFromS3Bucket(final String fileName) {
        log.info("Downloading file '{}' from bucket: '{}' ", fileName, BUCKET);
        final S3Object s3Object = amazonS3.getObject(BUCKET, fileName);
        return s3Object.getObjectContent();
    }

    public List<S3ObjectSummary> listObjects() {
        log.info("Retrieving object summaries for bucket '{}'", BUCKET);
        ObjectListing objectListing = amazonS3.listObjects(BUCKET);
        return objectListing.getObjectSummaries();
    }
}
package de.ksbrwsk.localstack;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Service class for AWS S3 operations.
 * This class is responsible for uploading, downloading, and listing objects in an AWS S3 bucket.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AwsS3Service {
    public static final String BUCKET="sabo-s3-bucket";

    // AmazonS3 instance
    private final AmazonS3 amazonS3;

    /**
     * Uploads a file to the AWS S3 bucket.
     * @param fileName The name of the file to be uploaded
     * @param fileData The data of the file to be uploaded
     * @return The ETag of the uploaded object
     */
    public String uploadObjectToS3(String fileName, byte[] fileData) {
        log.info("Uploading file '{}' to bucket: '{}' ", fileName, BUCKET);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileData);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileData.length);
        PutObjectResult putObjectResult =
                amazonS3.putObject(BUCKET, fileName, byteArrayInputStream, objectMetadata);
        return putObjectResult.getETag();
    }

    /**
     * Downloads a file from the AWS S3 bucket.
     * @param fileName The name of the file to be downloaded
     * @return The input stream of the downloaded object
     */
    public S3ObjectInputStream downloadFileFromS3Bucket(final String fileName) {
        log.info("Downloading file '{}' from bucket: '{}' ", fileName, BUCKET);
        final S3Object s3Object = amazonS3.getObject(BUCKET, fileName);
        return s3Object.getObjectContent();
    }

    /**
     * Lists the objects in the AWS S3 bucket.
     * @return A list of object summaries in the bucket
     */
    public List<S3ObjectSummary> listObjects() {
        log.info("Retrieving object summaries for bucket '{}'", BUCKET);
        ObjectListing objectListing = amazonS3.listObjects(BUCKET);
        return objectListing.getObjectSummaries();
    }
}
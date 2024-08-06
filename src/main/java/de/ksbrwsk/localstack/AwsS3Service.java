package de.ksbrwsk.localstack;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Service class for AWS S3 operations.
 * This class provides methods to upload, download, list, and manage S3 buckets and objects.
 */
@Service
@Slf4j
public class AwsS3Service {
    private final S3Template s3Template;
    private final String bucketName;

    /**
     * Constructor for AwsS3Service.
     *
     * @param s3Template the S3Template instance for S3 operations
     * @param bucketName the name of the S3 bucket
     */
    public AwsS3Service(S3Template s3Template, @Value("sabo-s3-bucket") String bucketName) {
        this.s3Template = s3Template;
        this.bucketName = bucketName;
    }

    /**
     * Uploads an object to the specified S3 bucket.
     *
     * @param fileName the name of the file to upload
     * @param fileData the byte array of the file data
     * @return the name of the uploaded file
     */
    public String uploadObjectToS3(String fileName, byte[] fileData) {
        log.info("Uploading file '{}' to bucket: '{}' ", fileName, this.bucketName);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileData);
        S3Resource upload = this.s3Template.upload(this.bucketName, fileName, byteArrayInputStream);
        return upload.getFilename();
    }

    /**
     * Downloads a file from the specified S3 bucket.
     *
     * @param fileName the name of the file to download
     * @return the S3Resource representing the downloaded file
     */
    public S3Resource downloadFileFromS3Bucket(final String fileName) {
        log.info("Downloading file '{}' from bucket: '{}' ", fileName, this.bucketName);
        return this.s3Template.download(this.bucketName, fileName);
    }

    /**
     * Lists all objects in the specified S3 bucket.
     *
     * @return a list of filenames of the objects in the bucket
     */
    public List<String> listObjects() {
        log.info("Retrieving object summaries for bucket '{}'", this.bucketName);
        List<S3Resource> s3Resources = this.s3Template.listObjects(this.bucketName, "");
        List<String> resources = s3Resources.stream()
                .map(S3Resource::getFilename)
                .toList();
        return resources;
    }

    /**
     * Checks if the specified S3 bucket exists.
     *
     * @return true if the bucket exists, false otherwise
     */
    public boolean bucketExists() {
        return this.s3Template.bucketExists(this.bucketName);
    }

    /**
     * Creates the specified S3 bucket if it does not already exist.
     *
     * @return the name of the created or existing bucket
     */
    public String createBucket() {
        if (bucketExists()) {
            log.info("Bucket '{}' already exists", this.bucketName);
            return this.bucketName;
        }
        log.info("Creating bucket '{}'", this.bucketName);
        this.s3Template.createBucket(this.bucketName);
        return this.bucketName;
    }
}
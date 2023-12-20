package de.ksbrwsk.localstack;

import java.util.Date;

/**
 * A record class representing an AWS S3 file.
 * This class holds information about a file stored in an AWS S3 bucket.
 */
@SuppressWarnings("ALL")
public record AwsS3File(
    /**
     * The name of the S3 bucket where the file is stored.
     */
    String bucketName,

    /**
     * The name of the file.
     */
    String file,

    /**
     * The date when the file was last modified.
     */
    Date lastModified) {
}
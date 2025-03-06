package de.ksbrwsk.localstack;

import java.time.LocalDateTime;

/**
 * Record representing an AWS S3 file.
 * This record holds information about an S3 file including the bucket name, file name, and last modified date.
 *
 * @param bucketName   the name of the S3 bucket
 * @param file         the name of the file in the S3 bucket
 * @param lastModified the date when the file was last modified
 */
public record AwsS3File(
        String bucketName,
        String file,
        LocalDateTime lastModified) {
}
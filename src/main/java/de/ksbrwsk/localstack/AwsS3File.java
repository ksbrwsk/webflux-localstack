package de.ksbrwsk.localstack;

import java.util.Date;

public record AwsS3File(String bucketName, String file, Date lastModified) {
}

package com.abhi.aws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;
import java.nio.file.Path;

@Service
public class AwsS3Service {

    private final S3Client s3Client;

    @Autowired
    public AwsS3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // Creates a new bucket in S3
    public String createBucket(String bucketName) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();
        s3Client.createBucket(createBucketRequest);
        return "Bucket created: " + bucketName;
    }

    // Uploads a file to the specified S3 bucket
    public String uploadFile(String bucketName, String key, Path filePath) throws Exception {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                RequestBody.fromFile(filePath));
        return "File uploaded: " + key;
    }

    // Generates a URL for accessing the file
    public String getFileUrl(String bucketName, String key) {
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key)).toString();
    }

    // Deletes the specified file from the bucket
    public String deleteFile(String bucketName, String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
        return "File deleted: " + key;
    }

    // Downloads the file stream from S3
    public ResponseInputStream<GetObjectResponse> downloadFile(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }
}

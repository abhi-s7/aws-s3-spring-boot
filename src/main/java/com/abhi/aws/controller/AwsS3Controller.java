package com.abhi.aws.controller;

import com.abhi.aws.service.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/s3")
public class AwsS3Controller {

    @Autowired
    private AwsS3Service awsS3Service;

    // Uploads a file to the S3 bucket
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("bucketName") String bucketName) {
        try {
            Path tempFile = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir")), file.getOriginalFilename(), null);
            file.transferTo(tempFile.toFile());
            
            String result = awsS3Service.uploadFile(bucketName, file.getOriginalFilename(), tempFile);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    // Downloads a file from the S3 bucket
    @GetMapping("/download/{bucketName}/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String bucketName, @PathVariable String fileName) {
        try {
            ResponseInputStream<GetObjectResponse> s3Object = awsS3Service.downloadFile(bucketName, fileName);
            byte[] fileBytes = s3Object.readAllBytes();
            s3Object.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(("Failed to retrieve file: " + e.getMessage().getBytes()).getBytes());
        }
    }

    // Deletes a file from the S3 bucket
    @DeleteMapping("/delete/{bucketName}/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String bucketName, @PathVariable String fileName) {
        try {
            String result = awsS3Service.deleteFile(bucketName, fileName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File deletion failed: " + e.getMessage());
        }
    }

    // Creates a new S3 bucket
    @PostMapping("/create-bucket/{bucketName}")
    public ResponseEntity<String> createBucket(@PathVariable String bucketName) {
        try {
            String result = awsS3Service.createBucket(bucketName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Bucket creation failed: " + e.getMessage());
        }
    }
}

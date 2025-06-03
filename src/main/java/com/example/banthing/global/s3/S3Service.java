package com.example.banthing.global.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    public static Logger logger2 = LoggerFactory.getLogger("S3 관련 로그");

    @Value("${spring.s3.endpoint}")
    private String endPoint;

    @Value("${spring.s3.region}")
    private String region;

    @Value("${spring.s3.accessKey}")
    private String accessKey;

    @Value("${spring.s3.secretKey}")
    private String secretKey;

    @Value("${spring.s3.bucket}")
    private String bucketName;

    private AmazonS3 s3;

    @PostConstruct
    public void init() {
        this.s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    public String uploadImage(String folderPath, MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = System.currentTimeMillis() + fileExtension;
        String savePath = folderPath + "/" + newFileName;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        try {
            s3.putObject(new PutObjectRequest(bucketName, savePath, image.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            logger.info("Object {} uploaded to NCP S3.", newFileName);
        } catch (SdkClientException e) {
            logger.error("S3 Upload Error: {}", e.getMessage());
            throw new IOException("Error uploading file to S3: " + e.getMessage());
        }
        return newFileName;
    }

    public String uploadImageFromBytes(String folderPath, String originalFileName, byte[] bytes) throws IOException {
        

        Tika tika = new Tika();
        String contentType = tika.detect(bytes);
        
        String extension = "";
        if ("image/png".equals(contentType)) {
            extension = "png";
        } else if ("image/jpeg".equals(contentType)) {
            extension = "jpg";
        } else {
            extension = "bin"; // fallback
        }
        
        String key = folderPath + "/" + UUID.randomUUID() + "_" + originalFileName + "." + extension;
        
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType(contentType);
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        logger2.info(inputStream.toString());
        
        try {
            s3.putObject(new PutObjectRequest(bucketName, key, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            logger.info("Object {} uploaded to NCP S3.", originalFileName);
        } catch (SdkClientException e) {
            logger.error("S3 Upload Error: {}", e.getMessage());
            throw new IOException("Error uploading file to S3: " + e.getMessage());
        }
        return s3.getUrl(bucketName, key).toString();
    }

    public void deleteImage(String imagePath) {
        try {
            s3.deleteObject(bucketName, imagePath);
            logger.info("Deleted image from NCP S3: {}", imagePath);
        } catch (SdkClientException e) {
            logger.error("S3 Image Deletion Error: {}", e.getMessage());
            throw new RuntimeException("Error deleting image from S3: " + e.getMessage());
        }
    }

    public void deleteFolder(String folderPath) {
        try {
            ObjectListing objectListing = s3.listObjects(bucketName, folderPath);
            List<String> objectKeys = objectListing.getObjectSummaries()
                    .stream()
                    .map(S3ObjectSummary::getKey)
                    .collect(Collectors.toList());

            for (String key : objectKeys) {
                s3.deleteObject(bucketName, key);
                logger.info("Object {} deleted from NCP S3.", key);
            }
        } catch (SdkClientException e) {
            logger.error("S3 Deletion Error: {}", e.getMessage());
            throw new RuntimeException("Error deleting files from S3: " + e.getMessage());
        }
    }

    public String encodeImageToBase64(String imgUrl, String folderPath) throws IOException {
        String fullUrl = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + folderPath + "/" + imgUrl;
        URL url = new URL(fullUrl);

        try (InputStream inputStream = url.openStream()) {
            byte[] imageBytes = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }
}
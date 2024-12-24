package com.example.banthing.domain.item.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.cloudformation.model.Visibility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.example.banthing.domain.item.entity.ItemImg;
import com.example.banthing.domain.item.repository.ItemImgRepository;
import com.example.banthing.domain.item.repository.ItemRepository;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemImgService {

    public static Logger logger = LoggerFactory.getLogger("Item 이미지 관련 로그");
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final ItemImgRepository itemImgsRepository;
    private final ItemRepository itemRepository;

    @Value("${file.upload-dir}") //이미지 저장 경로
    private String folderName;

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
        // NCP S3 클라이언트 초기화
        this.s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    public void save(List<MultipartFile> images, Long itemId) throws IOException {
        List<ItemImg> itemImgs = new ArrayList<>();
        
        for (MultipartFile image : images) {
            String imgUrl = saveImage(image, itemId);
            ItemImg itemImg = ItemImg.builder()
                    .imgUrl(imgUrl)
                    .item(itemRepository.findById(itemId).orElseThrow())
                    .build();
            itemImgs.add(itemImg);
        }
        
        itemImgsRepository.saveAll(itemImgs);
    }

    public String saveImage(MultipartFile image, Long itemId) throws IOException {

        String originalFilename = image.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String newFileName = System.currentTimeMillis() + fileExtension;
        String savePath = folderName + "/" +itemId +  "/" + newFileName;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        // objectMapper.setVisibility(PropertyAccessor.FIELD, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY);
        // logger.info("Image Response in Service: {}", objectMapper.writeValueAsString(image));

        try {
            s3.putObject(new PutObjectRequest(bucketName, savePath, image.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            System.out.format("Object %s has been uploaded to NCP S3 bucket.\n", newFileName);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
            throw new IOException("Error uploading file to S3: " + e.getMessage());
        } catch (SdkClientException e) {
            e.printStackTrace();
            throw new IOException("Error in S3 SDK client: " + e.getMessage());
        }

        return newFileName;
    }


    public void delete(Long itemId) {
        List<ItemImg> itemImgs = itemImgsRepository.findByItemId(itemId);
        deleteImage(itemId);
        itemImgsRepository.deleteAll(itemImgs);
    }

    private void deleteImage(Long itemId) {

        String itemFolderPath = folderName + "/" + itemId + "/";

        try {
            ObjectListing objectListing = s3.listObjects(bucketName, itemFolderPath);
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                String objectKey = objectSummary.getKey();
                s3.deleteObject(bucketName, objectKey);
                System.out.format("Object %s has been deleted from NCP S3 bucket.\n", objectKey);
            }
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting files from S3: " + e.getMessage());
        } catch (SdkClientException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in S3 SDK client: " + e.getMessage());
        }
    }

    public void update(List<MultipartFile> newImages, Long itemId) throws IOException {
        delete(itemId);
        save(newImages, itemId);
    }

    public List<ItemImg> findItemImgs(Long itemId) {
        return itemImgsRepository.findByItemId(itemId);
    }
    public List<String> getBase64EncodedImages(Long itemId) {
        List<ItemImg> itemImgs = findItemImgs(itemId);

        return itemImgs.stream()
                .map(itemImg -> {
                    try {
                        return encodeImageToBase64(itemImg.getImgUrl(), itemId);
                    } catch (IOException e) {
                        logger.error("Failed to encode image to Base64: {}", itemImg.getImgUrl(), e);
                        return null;
                    }
                })
                .filter(base64 -> base64 != null)
                .collect(Collectors.toList());
    }


    private String encodeImageToBase64(String imgUrl, Long itemId) throws IOException {
        String fullUrl = "https://kr.object.ncloudstorage.com/banthing-images/itemImage/" + itemId + "/" + imgUrl;
        URL url = new URL(fullUrl);

        try (InputStream inputStream = url.openStream()) {
            byte[] imageBytes = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

}
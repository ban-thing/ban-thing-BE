package com.example.banthing.domain.item.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NcpConfig {
    private final String endPoint = "https://kr.object.ncloudstorage.com";
    private final String regionName = "kr-standard";

    @Value("${spring.s3.accessKey}")
    private String accessKey;

    @Value("${spring.s3.secretKey}")
    private String secretKey;

    @Value("${spring.s3.bucket}")
    private String bucketName;

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        
        // S3 client
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
            .build();


        List<CORSRule.AllowedMethods> methodRule = new ArrayList<>();
        methodRule.add(CORSRule.AllowedMethods.PUT);
        methodRule.add(CORSRule.AllowedMethods.GET);
        methodRule.add(CORSRule.AllowedMethods.POST);
        CORSRule rule = new CORSRule().withId("CORSRule")
                .withAllowedMethods(methodRule)
                .withAllowedHeaders(Arrays.asList(new String[] { "*" }))
                .withAllowedOrigins(Arrays.asList(new String[] { "https://banthing.net" }))
                .withMaxAgeSeconds(3000);

        List<CORSRule> rules = new ArrayList<>();
        rules.add(rule);

        // Add rules to new CORS configuration.
        BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();
        configuration.setRules(rules);

        // Set the rules to CORS configuration.
        s3.setBucketCrossOriginConfiguration(bucketName, configuration);

        // Get the rules for CORS configuration.
        configuration = s3.getBucketCrossOriginConfiguration(bucketName);

        // check
        if (configuration == null) {
            System.out.println("Configuration is null.");
        } else {
            System.out.println("Configuration has " + configuration.getRules().size() + " rules\n");

            for (CORSRule getRule : configuration.getRules()) {
                System.out.println("Rule ID: " + getRule.getId());
                System.out.println("MaxAgeSeconds: " + getRule.getMaxAgeSeconds());
                System.out.println("AllowedMethod: " + getRule.getAllowedMethods());
                System.out.println("AllowedOrigins: " + getRule.getAllowedOrigins());
                System.out.println("AllowedHeaders: " + getRule.getAllowedHeaders());
                System.out.println("ExposeHeader: " + getRule.getExposedHeaders());
                System.out.println();
            }
        }
        
        return (AmazonS3Client) s3;
    }

    

}

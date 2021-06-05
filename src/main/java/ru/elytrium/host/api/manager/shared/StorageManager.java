package ru.elytrium.host.api.manager.shared;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import ru.elytrium.host.api.ElytraHostAPI;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public class StorageManager {
    private final AmazonS3 s3client;

    public StorageManager(String endpoint, String region, String accessKey, String secretKey) {
        final AWSCredentials credentials = new BasicAWSCredentials(
                accessKey,
                secretKey
        );

        this.s3client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        ElytraHostAPI.getLogger().info("StorageManager: connected to " + endpoint);
    }

    public URL getDownloadLink(String bucketName, String path, Date expiration) {
        return s3client.generatePresignedUrl(bucketName, path, expiration, HttpMethod.GET);
    }

    public URL getUploadLink(String bucketName, String path, Date expiration) {
        return s3client.generatePresignedUrl(bucketName, path, expiration, HttpMethod.PUT);
    }

    public InputStream getFile(String bucketName, String path) {
        return s3client.getObject(bucketName, path).getObjectContent();
    }

    public void saveFile(String bucketName, String path, File file) {
        s3client.putObject(bucketName, path, file);
    }
}

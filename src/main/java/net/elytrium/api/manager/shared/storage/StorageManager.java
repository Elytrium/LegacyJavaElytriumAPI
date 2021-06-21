package net.elytrium.api.manager.shared.storage;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public class StorageManager {
    private static final Logger logger = LoggerFactory.getLogger(StorageManager.class);

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

        logger.info("Connected to " + endpoint);
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

    public void deleteDir(String bucketName, String path) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(path);

        ObjectListing objectListing = s3client.listObjects(listObjectsRequest);

        while (true) {
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                s3client.deleteObject(bucketName, objectSummary.getKey());
            }
            if (objectListing.isTruncated()) {
                objectListing = s3client.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }
    }

    public void saveFile(String bucketName, String path, File file) {
        s3client.putObject(bucketName, path, file);
    }
}

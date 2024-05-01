package school.sptech.filesexample.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.filesexample.service.StorageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Service class that implements StorageService for storing files on Amazon S3.
 * This service is activated under the 's3' profile.
 * Author: Diego Brito
 */
@Slf4j
@Service
@Profile("s3")
public class S3StorageImpl implements StorageService {
    private final AmazonS3 s3Client;
    private final String bucketName;

    /**
     * Constructor for dependency injection of the S3 client and the bucket name.
     *
     * @param s3Client   the Amazon S3 client
     * @param bucketName the name of the S3 bucket where files will be stored
     */
    public S3StorageImpl(AmazonS3 s3Client, @Value("${app.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    /**
     * Saves a file to the S3 bucket.
     *
     * @param fileName the name of the file to be saved
     * @param content  the content of the file as a byte array
     * @throws ResponseStatusException if the file cannot be saved due to client or service issues
     * @author: Diego Brito
     */
    @Override
    public void save(String fileName, byte[] content) {
        try {
            log.info("[AWS] Saving file to S3 bucket: {} with filename: {}", bucketName, fileName);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(content.length);
            s3Client.putObject(bucketName, fileName, new ByteArrayInputStream(content), metadata);
        } catch (AmazonServiceException e) {
            log.error("[AWS-SERVICE-ERROR] Failed to save file to S3", e);
            throw new ResponseStatusException(500, "Service error: Unable to save file to S3.", e);
        } catch (AmazonClientException e) {
            log.error("[AWS-CLIENT-ERROR] Client error in connecting to S3", e);
            throw new ResponseStatusException(503, "Client error: Unable to connect to S3.", e);
        }
    }

    /**
     * Loads a file from the S3 bucket.
     *
     * @param fileName the name of the file to be loaded
     * @return the content of the file as a byte array
     * @throws ResponseStatusException if the file cannot be loaded due to client or service issues
     * @author: Diego Brito
     */
    @Override
    public byte[] load(String fileName) {
        try {
            log.info("[AWS] Loading file {} from S3 bucket: {}", fileName, bucketName);
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            return s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            log.error("[AWS-IO-ERROR] Failed to load file from S3", e);
            throw new ResponseStatusException(500, "IO error: Unable to read file from S3.", e);
        } catch (AmazonServiceException e) {
            log.error("[AWS-SERVICE-ERROR] Service error on S3 operation", e);
            throw new ResponseStatusException(500, "Service error: Unable to load file from S3.", e);
        } catch (AmazonClientException e) {
            log.error("[AWS-CLIENT-ERROR] Client error in connecting to S3", e);
            throw new ResponseStatusException(503, "Client error: Unable to connect to S3.", e);
        }
    }

    /**
     * Deletes a file from the S3 bucket.
     *
     * @param fileName the name of the file to be deleted
     * @throws ResponseStatusException if the file cannot be deleted due to client or service issues
     * @author: Diego Brito
     */
    @Override
    public void delete(String fileName) {
        try {
            log.info("[AWS] Deleting file {} from S3 bucket: {}", fileName, bucketName);
            s3Client.deleteObject(bucketName, fileName);
        } catch (AmazonServiceException e) {
            log.error("[AWS-SERVICE-ERROR] Failed to delete file from S3", e);
            throw new ResponseStatusException(500, "Service error: Unable to delete file from S3.", e);
        } catch (AmazonClientException e) {
            log.error("[AWS-CLIENT-ERROR] Client error in connecting to S3", e);
            throw new ResponseStatusException(503, "Client error: Unable to connect to S3.", e);
        }
    }
}

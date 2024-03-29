package DpFeeder.ProcessingFeeder.file;


import DpFeeder.ProcessingFeeder.messaging.SqsMessageProducer;
import DpFeeder.ProcessingFeeder.model.objectModel;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Feeder will run on ${objects.pickup.scanInterval} and look for files in the $${objects.pickup.bucket}.
 * They will be moved to the processing Directory and a message will be sent to the SQS Queue
 */
@Component
@Slf4j
public class S3FileScanner {

    private final SqsMessageProducer producer;
    @Value("${objects.pickup.inputBucket}")
    private String sourceBucket;
    @Value("${objects.pickup.processingBucket}")
    private String processingBucket;
    @Value("${objects.pickup.maxObjectsPerScan}")
    private int maxFetch;
    @Value("${cloud.aws.region}")
    private String awsRegion;
    @Value("${cloud.aws.accessKey}")
    private String awsAccessKey;
    @Value("${cloud.aws.secretKey}")
    private String secretKey;

    public S3FileScanner(SqsMessageProducer producer) {
        this.producer = producer;
    }

    //TODO: Move AWS Credentials and s3 client to config class
    public AWSCredentials credentials() {
        return new BasicAWSCredentials(awsAccessKey, secretKey);
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials()))
                .withRegion(awsRegion)
                .build();
    }

    @Scheduled(fixedRateString = "${objects.pickup.scanInterval}")
    public void scanS3() {
        log.info("Scanning files in {}", sourceBucket);
        try {
            // Get list of objects in Input Directory
            List<S3ObjectSummary> s3Objects = listObjects(sourceBucket, maxFetch);

            if (s3Objects.isEmpty()) {
                log.info("No objects found in input bucket");
            }

            // For each object move to processing directory
            for (S3ObjectSummary objectSummary : s3Objects) {
                log.info("Moving {} object to {} bucket", objectSummary.getKey(), processingBucket);
                UUID objectUUID = UUID.randomUUID();
                String newKey = moveObject(sourceBucket, processingBucket, objectSummary.getKey(), objectUUID);

                // Create message for the object
                objectModel objectPayload = new objectModel();
                objectPayload.setUuid(UUID.randomUUID());
                objectPayload.setFeedingDate(new Date());
                objectPayload.setOriginalFilename(objectSummary.getKey());
                objectPayload.setObjectURL(newKey);
                objectPayload.setObjectBucket(processingBucket);
                Map<String, Object> headers = new HashMap<>();
                headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                producer.send(objectPayload, headers);
            }
        } catch (Exception e) {
            log.error("Failed to scan files and create message for the queue", e);
        }
    }

    public List<S3ObjectSummary> listObjects(String bucketName, int max) {
        ListObjectsRequest request = new ListObjectsRequest();
        request.setBucketName(bucketName);
        request.setMaxKeys(max);
        ObjectListing objectListing = amazonS3().listObjects(request);
        if (objectListing.isTruncated()) {
            log.warn("S3 Object List is Truncated");
        }
        return objectListing.getObjectSummaries();
    }

    public String moveObject(String sourceBucket, String targetBucket, String objectKey, UUID objectUUID) {
        // Create object key name with timestamp directory prefix
        // year/month/day/hour UTC time
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH");
        // Change key to UUID for easier management
        String newKey = now.format(formatter) + objectUUID.toString();

        // Move to new bucket
        amazonS3().copyObject(sourceBucket, objectKey, targetBucket, newKey);
        // Delete from old bucket
        // TODO: Only move if queue message is successful created
        amazonS3().deleteObject(sourceBucket, objectKey);
        return newKey;
    }

}

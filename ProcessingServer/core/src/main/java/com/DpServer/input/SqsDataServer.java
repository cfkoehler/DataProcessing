package com.DpServer.input;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.textract.model.InvalidS3ObjectException;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import emissary.core.Pausable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SqsDataServer extends Pausable {

    protected static final Logger logger = LoggerFactory.getLogger(SqsDataServer.class);

    // SQS Queue to listen on
    protected String queue;

    // How often to check for new messages in seconds
    protected int pollingInterval = 10;

    protected String loadedBucket;

    protected SqsS3PickupPlace myParent;

    // Thread safe termination
    protected boolean timeToShutdown = false;

    AmazonSQSAsync sqsClient;

    AmazonS3 s3;


    public SqsDataServer(String queue, SqsS3PickupPlace parent, int pollingInterval, AWSCredentials awsCredentials, String awsRegion, String loadedBucket) {
        super("SQS-" + queue);
        myParent = parent;
        this.pollingInterval = pollingInterval;
        this.queue = queue;
        this.loadedBucket = loadedBucket;
        this.sqsClient = amazonSQSAsync(awsCredentials, awsRegion);
        this.s3 = amazonS3(awsCredentials, awsRegion);
        this.setPriority(Thread.NORM_PRIORITY - 2);
        this.setDaemon(true);
    }

    @Override
    public void run() {

        while (!timeToShutdown) {

            if (checkPaused()) {
                continue;
            }

            // Check the queue for a new message
            List<Message> newMessages  = receiveMessages(sqsClient, queue, 2);

            int processedCount = 0;

            for (Message message : newMessages) {
                logger.info("Received Message: {}", message);

                ObjectMapper jsonMapper = new ObjectMapper();
                SqsMessage sqsMessage;
                try {
                    sqsMessage = jsonMapper.readValue(message.getBody(), SqsMessage.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                logger.info("objectURL: {}", sqsMessage.getObjectURL());

                byte[] objectPayload = getS3Bytes(sqsMessage.getObjectBucket(), sqsMessage.getObjectURL());

                if (objectPayload == null) {
                    logger.warn("Empty payload object");
                    // TODO: Do something when empty payload
                }

                Boolean processed = myParent.processDataPayload(objectPayload, sqsMessage);
                if (processed) {
                    // Move to loaded S3 Bucket
                    moveObject(sqsMessage.getObjectBucket(), loadedBucket, sqsMessage.getObjectURL());
                    // Delete message from queue
                    deleteMessage(sqsClient, queue, message);
                } else {
                    // Move message to error queue??
                }
                processedCount++;
            }


            // Delay for the polling interval if there was
            // nothing to do for this last round
            if (processedCount == 0) {
                try {
                    Thread.sleep(pollingInterval*1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // TODO: Close SQS Queue Connection


    }


    private byte[] getS3Bytes(String bucket, String key) {
        try {
            S3Object object = s3.getObject(bucket, key);
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (InvalidS3ObjectException | IOException e) {
            logger.error("Error Fetching S3 Object", e);
        }
        return null;
    }

    private void moveObject(String sourceBucket, String targetBucket, String objectKey) {
        // Move to new bucket
        s3.copyObject(sourceBucket, objectKey, targetBucket, objectKey);
        // Delete from old bucket
        s3.deleteObject(sourceBucket, objectKey);
    }

    private static List<Message> receiveMessages(AmazonSQSAsync sqsClient, String queueUrl, int maxMessageCount) {
        logger.info("Checking for SQS Messages");
        try {
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
            receiveMessageRequest.setQueueUrl(queueUrl);
            receiveMessageRequest.setMaxNumberOfMessages(maxMessageCount);
            ReceiveMessageResult receiveMessageResult = sqsClient.receiveMessage(receiveMessageRequest);
            return receiveMessageResult.getMessages();
        } catch (AmazonSQSException e) {
            logger.error("Error fetching messages from SQS queue {}",queueUrl,e);
        }
        return Collections.EMPTY_LIST;
    }

    private static void deleteMessage(AmazonSQSAsync sqsClient, String queueUrl, Message message) {
        logger.info("Deleting message: {}", message.getMessageId());
        try {
            sqsClient.deleteMessage(queueUrl, message.getReceiptHandle());
        } catch (AmazonSQSException e) {
            logger.error("Error deleting message from SQS Queue", e);
        }
    }

    private AmazonSQSAsync amazonSQSAsync(AWSCredentials credentials, String region) {
        return AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

    private AmazonS3 amazonS3(AWSCredentials credentials, String region) {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }


    /**
     * Shutdown the thread
     */
    public void shutdown() {
        timeToShutdown = true;
    }

}

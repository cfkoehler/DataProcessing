package com.DpServer.output;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import emissary.core.IBaseDataObject;
import emissary.place.EmptyFormPlace;
import emissary.place.ServiceProviderPlace;
import emissary.util.DataUtil;
import emissary.util.DisposeHelper;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DynamoDBDropOffPlace extends ServiceProviderPlace implements EmptyFormPlace {

    private String dynamodbTable;
    private String awsAccessKey;
    private String awsSecretKey;
    private String awsRegion;
    private AWSCredentials awsCredentials;

    private static AmazonDynamoDB dynamoDBClient;

    public DynamoDBDropOffPlace() throws IOException {
        super();
        configurePlace();
    }

    public DynamoDBDropOffPlace(String configInfo, String dir, String placeLoc) throws IOException {
        super(configInfo, dir, placeLoc);
        configurePlace();
    }

    protected void configurePlace() {
        dynamodbTable = configG.findStringEntry("DYNAMODB_TABLE_NAME");
        awsAccessKey = configG.findStringEntry("AWS_ACCESS_KEY");
        awsSecretKey = configG.findStringEntry("AWS_SECRET_KEY");
        awsRegion = configG.findStringEntry("AWS_REGION");
        awsCredentials=credentials();
        dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public AWSCredentials credentials() {
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }

    @Override
    public void shutDown() {
        // TODO: Close DynamoDB Connection
    }

    @Override
    public void process(final IBaseDataObject ibdo) {
        if (DataUtil.isEmpty(ibdo)) {
            logger.warn("null/empty data object");
            return;
        }

        logger.info("Starting DropOff of {} to DynamoDB. Current Forms: {}", ibdo.shortName(), ibdo.getAllCurrentForms());

        // Translate ibdo Parameters into map for dynamoDB Insert
        Map<String, AttributeValue> insertParams = new HashMap<>();
        for (Map.Entry<String, Collection<Object>> param : ibdo.getParameters().entrySet() ) {
            String value = param.getValue().toString();
            value = value.replaceAll("\\[|\\]", "");
            insertParams.put(param.getKey(), new AttributeValue(value));
        }

        //Insert all metadata into DynamoDB
        if (!insertParams.isEmpty()) {
            PutItemRequest request = new PutItemRequest();
            request.setTableName(dynamodbTable);
            request.setItem(insertParams);

            try {
                PutItemResult result = dynamoDBClient.putItem(request);
                logger.info("DynamoDB Put Result for object: {} --- {}", ibdo.shortName(), result.getSdkHttpMetadata().getHttpStatusCode());
            } catch (AmazonServiceException e) {
                logger.error("Error putting object to DynamoDB", e);
            }
        } else {
            logger.warn("DynamoDB Insert Map is empty");
        }

        // Clean Up
        this.nukeMyProxies(ibdo);
        DisposeHelper.execute(ibdo);
    }


}

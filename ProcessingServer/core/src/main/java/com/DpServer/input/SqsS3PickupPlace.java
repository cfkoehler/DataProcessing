package com.DpServer.input;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import emissary.core.DataObjectFactory;
import emissary.core.EmissaryException;
import emissary.core.IBaseDataObject;
import emissary.pickup.IPickUp;
import emissary.pickup.PickUpPlace;

import java.io.IOException;
import java.util.Date;

public class SqsS3PickupPlace extends PickUpPlace implements IPickUp {

    protected enum QueServerState {
        PAUSED, UNPAUSED
    }

    protected QueServerState queServerState = QueServerState.PAUSED;


    private String sqsUrl;
    private String awsAccessKey;
    private String awsSecretKey;
    private String awsRegion;
    private AWSCredentials awsCredentials;
    private String loadedBucket;

    public SqsS3PickupPlace() throws IOException {
        super();
        configurePlace();
    }

    public SqsS3PickupPlace(String configInfo, String dir, String placeLoc) throws IOException {
        super(configInfo, dir, placeLoc);
        configurePlace();
    }

    protected void configurePlace() {
        sqsUrl = configG.findStringEntry("SQS_INPUT_QUEUE");
        awsAccessKey = configG.findStringEntry("AWS_ACCESS_KEY");
        awsSecretKey = configG.findStringEntry("AWS_SECRET_KEY");
        awsRegion = configG.findStringEntry("AWS_REGION");
        loadedBucket = configG.findStringEntry("s3_LOADED_BUCKET");
        awsCredentials=credentials();
        startDataServer();
    }

    public boolean processDataPayload(byte[] payload , SqsMessage sqsMessage) {
        IBaseDataObject dataObject = DataObjectFactory.getInstance(payload, sqsMessage.getUuid());
        dataObject.setParameter("FEEDING_DATE", sqsMessage.getFeedingDate());
        dataObject.setParameter("ORIGINAL_FILENAME", sqsMessage.getOriginalFilename());
        dataObject.putParameter("SERVER_INPUT_DATE", emissary.util.TimeUtil.getDateAsISO8601(new Date().getTime()));
        String s3URL = "https://" + sqsMessage.getObjectBucket() + "s3.amazonaws.com/" + sqsMessage.getObjectURL();
        dataObject.setParameter("OBJECT_URI", s3URL);

        // TODO: make this configurable
        dataObject.setCurrentForm("UNKNOWN");

        try {
            logger.info("**Deploying an agent for id: {}, object key: {}, forms: {}", dataObject.getInternalId(), sqsMessage.getObjectURL(), dataObject.getAllCurrentForms());
            assignToPooledAgent(dataObject, -1L);
        } catch (EmissaryException e) {
            logger.error("Error deploying agent", e);
            return false;
        }
        return true;
    }

    public AWSCredentials credentials() {
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }


    public void startDataServer() {
        SqsDataServer sqsDataServer = new SqsDataServer(sqsUrl, this, 30, awsCredentials, awsRegion, loadedBucket);
        sqsDataServer.start();
    }



}

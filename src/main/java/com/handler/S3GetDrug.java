package com.handler;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.entity.Drug;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class S3GetDrug implements RequestHandler<S3Event, List<Drug>> {
    private final ObjectMapper mapper = new ObjectMapper().configure
            (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
    private final String topicArn= System.getenv("DRUG_TOPIC");
    private final Gson gson = new Gson();

    public List<Drug> handleRequest(S3Event s3event, Context context) {
        System.out.println("event: " + s3event.toString());
        List<Drug> drugList = null;
        try {
            System.out.println("Received s3 event");
            int count = 1;
            for (S3EventNotification.S3EventNotificationRecord record : s3event.getRecords()) {
                System.out.println("Event No: " + count++);
                drugList = listEventRecordDetails(record);
            }
         } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
        return drugList;
    }
    private List<Drug> getObjectAsDrugArray(String bkt, String key){
        // Read file as objects
        Drug[] drugs = null;

        System.out.println("bucketname in getObjectAsDrugArray: " + bkt);
        System.out.println("key in getObjectAsDrugArray: " + key);
        S3ObjectInputStream sStream = s3Client.getObject(bkt, key).getObjectContent();
        try {
            System.out.println("sStream: " + sStream.available());
            drugs = mapper.readValue(sStream, Drug[].class);
        }catch (Exception e){
            System.err.println("Exception: " + e);
        }
        System.out.println("Body as List: " + Arrays.asList(drugs));
        System.out.println("List length: " + drugs.length);
        return Arrays.asList(drugs);
    }

    private String getObjectAsString(String bkt, String key){
        // Read the source file as text
        String body = s3Client.getObjectAsString(bkt, key);
        System.out.println("Body as String: " + body);
        return body;
    }

    private List<Drug> listEventRecordDetails(S3EventNotification.S3EventNotificationRecord record) throws Exception{
        String bkt = record.getS3().getBucket().getName();
        System.out.println("bucketname: " + bkt);
        String key = record.getS3().getObject().getKey().replace('+', ' ');
        key = URLDecoder.decode(key, "UTF-8");
        System.out.println("key: " + key);
      //  System.out.println("Calling getObjectAsString");
       //getObjectAsString(bkt, key);
        System.out.println("Calling getObjectAsDrugArray");
        List<Drug> drugList = getObjectAsDrugArray(bkt, key);
        System.out.println("Publishing to topic: " + topicArn);
        PublishRequest publishRequest = null;
        PublishResult publishResponse = null;

        drugList.forEach(this::publishToTopic);
        return drugList;
    }

    private void publishToTopic(Drug drug) {
        // Publish a message to an Amazon SNS topic.
        PublishRequest publishRequest = new PublishRequest(topicArn, gson.toJson(drug));
        PublishResult publishResponse = snsClient.publish(publishRequest);
         // Print the MessageId of the message.
        System.out.println("MessageId: " + publishResponse.getMessageId());
    }
}

package com.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.entity.Drug;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetDrugSubscriber implements RequestHandler<SNSEvent, Drug> {
    private final ObjectMapper mapper = new ObjectMapper().configure
            (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    private AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
    private final String queue= System.getenv("DRUG_QUEUE");

    @Override
    public Drug handleRequest(SNSEvent snsEvent, Context context) {
        System.out.println("Recieved SNS Event");
        Drug drug = null;
        String subscriptionMessage = snsEvent.getRecords().get(0).getSNS().getMessage();
        try {
            context.getLogger().log("subscriptionMessage" + subscriptionMessage);
            System.out.println("syso subscriptionMessage" + subscriptionMessage);
            drug = mapper.readValue(subscriptionMessage, Drug.class);

            // Send a message
            System.out.println("Sending a message to Queue: " + queue);
            sqs.sendMessage(new SendMessageRequest(queue, subscriptionMessage));
        }catch (Exception e){
            System.err.println("Exception: " + e);
        }
        return drug;
    }
}

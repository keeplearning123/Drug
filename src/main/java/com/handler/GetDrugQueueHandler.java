package com.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetDrugQueueHandler implements RequestHandler<SQSEvent, Void> {
    private final ObjectMapper mapper = new ObjectMapper().configure
            (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        System.out.println("Recieved SQS Event");
        try {
            for(SQSEvent.SQSMessage msg : sqsEvent.getRecords()){
                System.out.println(msg.getBody());
            }
        }catch (Exception e){
            System.err.println("Exception: " + e);
        }
        return null;
    }
}

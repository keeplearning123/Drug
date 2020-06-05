package com.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.test.DrugRequest;
import com.test.DrugResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Drug {
    public String getCost(String s) {
        if (s.startsWith("A"))
            return "$100.99";
        else if (s.startsWith("B"))
            return "$200.99";
        else
            return "$999.99";
    }

    public void getCostAsync(String s, Context c) {
        System.out.println("Cost of requested drug: "+getCost(s));
        System.out.println("Log stream: "+c.getLogStreamName());
    }

    public List<String> getCostList(List<String> drugList) {
        List<String> newList = new ArrayList<>();
        drugList.forEach(name -> {
            newList.add(getCost(name));
             });
        return newList;
    }


    public Map<String, String> getDetails(Map<String, String> input)
    {
        Map<String, String>  newMap = new HashMap<>();
        input.forEach((k,v)-> {
            String newKey =
            newMap.put("New"+k, v);
        });
        return newMap;
    }


    public Map<String,Map<String, String>> getPatientDtsColection(List<Map<String, String>> input)
    {

        System.out.println(input);
        Map<String,Map<String, String>>  newMap = new HashMap<>();
        IntStream.range(0, input.size()).forEach(i->newMap.put("Nested at Position"+i, input.get(i)));

        return newMap;
    }

    public DrugResponse identifyDrug(DrugRequest request)
    {
        DrugResponse response = null;

        if(request.getNdc().equals("100"))
            response = new DrugResponse("Brand");
        else
            response = new DrugResponse("Generic");
        return response;
    }


    public void timeoutHandler(Object request, Context c)
    {
        while(true)
        {
            System.out.println("Remaining time "+c.getRemainingTimeInMillis());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getClientDetails(Object request, Context c){
        System.out.println("c.getClientContext()" + c.getClientContext().toString());
        System.out.println("c.getAwsRequestId()" + c.getAwsRequestId());
        System.out.println("c.getInvokedFunctionArn()" + c.getInvokedFunctionArn());
        System.out.println("c.getLogStreamName()" + c.getLogStreamName());
        System.out.println("c.getFunctionName()" + c.getFunctionName());
    }
}
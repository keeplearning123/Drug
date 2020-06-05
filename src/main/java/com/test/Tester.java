package com.test;

import com.entity.Drug;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tester {
    public static void main(String[] args) throws Exception{
        Gson g = new Gson();
        Drug[] drugs = {new Drug("D1", "Tylenol"), new Drug("D2", "Motrin")};
        String jsonString = g.toJson(drugs);
        System.out.println(jsonString);

        List drugsList = new ArrayList();
        drugsList.add(new Drug("D1", "Tylenol"));
        drugsList.add(new Drug("D2", "Motrin"));
        String jsonStringList = g.toJson(drugs);
        System.out.println(jsonStringList);

        String jsonInString="[{\"id\":\"D1\",\"name\":\"Tylenol\"},{\"id\":\"D2\",\"name\":\"Motrin\"}]";
        ObjectMapper mapper = new ObjectMapper();
        Drug[] obj = mapper.readValue(jsonInString, Drug[].class);
        System.out.println(Arrays.asList(obj));
    }
}

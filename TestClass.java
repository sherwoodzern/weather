package com.oracle.examples.weather;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestClass {

    public static void main(String args[]) {
        HashMap<String, String> aMap = new HashMap<String,String>();
        aMap.put("zip","34772");
        aMap.put("city","Saint Cloud");
        Iterator<String> iter = aMap.keySet().iterator();
        StringBuilder queryString = new StringBuilder("?");
        while (iter.hasNext()) {
            String key = iter.next();
            queryString.append(key);
            queryString.append("=");
            queryString.append(aMap.get(key));
            queryString.append("&");
        }
        queryString.append("apiKey=");
        queryString.append("11111111111111");
        System.out.println("The queryString: " + queryString);
    }

}


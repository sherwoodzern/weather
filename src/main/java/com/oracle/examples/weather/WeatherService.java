/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oracle.examples.weather;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Iterator;

import io.helidon.common.http.MediaType;
import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import java.util.HashMap;

/**
 * 
 */

public class WeatherService implements Service {

    /**
     * This gets config from application.yaml on classpath
     * and uses "app" section.
     */
    private static final Config CONFIG = Config.create().get("app");

    /**
     * The config value for the key {@code hostURL}.
     */
    private static String hostURL = CONFIG.get("hostURL").asString("https://api.openweathermap.org/data/2.5/weather");
    private static String apiKey  = CONFIG.get("apiKey").asString("apiKey");

    /**
     * A service registers itself by updating the routing rules.
     * @param rules the routing rules.
     */
    @Override
    public void update(Routing.Rules rules) {
        System.out.println("Creating the weather service rules");
        rules
            .get("/current/city/{city}", this::getByLocation)
            .get("/current/id/{id}", this::getById)
            .get("/current/lat/{lat}/lon/{lon}", this::getByLatLon)
            .get("/current/zip/{zip}", this::getByZip);
    }

    private void getByZip(ServerRequest request, ServerResponse response) {
        System.out.println("Invoking getByZip");
        String zip = request.path().param("zip");
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("zip",zip);
        try {
            JsonObject jsonObject = getWeather(params);
            System.out.println("JsonObject Created: " + jsonObject.toString());
            response.headers().contentType(MediaType.APPLICATION_JSON);
            response.send(jsonObject.toString());
        }
        catch (Exception e) {}

    }

    private void getByLocation(ServerRequest request, ServerResponse response) {

    }

    private void getById(ServerRequest request, ServerResponse response) {
        

    }

    private void getByLatLon(ServerRequest request, ServerResponse response) {

    }

    /*private void countAccess(ServerRequest request, ServerResponse response) {

    }*/

    private JsonObject getWeather(HashMap<String,String> params) throws Exception {
        System.out.println("getWeather");
        Iterator<String> iter = params.keySet().iterator();
        StringBuilder queryString = new StringBuilder("?");
        while (iter.hasNext()) {
            String key = iter.next();
            queryString.append(key);
            queryString.append("=");
            queryString.append(params.get(key));
            queryString.append("&");
        }
        queryString.append("apiKey=");
        queryString.append(apiKey);
        HttpURLConnection connection = getURLConnection("GET",queryString.toString());
        System.out.println("The Connection details: " + connection.toString());
        JsonReader jsonReader = Json.createReader(connection.getInputStream());
        JsonObject jsonObject = jsonReader.readObject();
        System.out.println("Returning the JsonObject");
        return jsonObject;
        //System.out.println("The queryString: " + queryString);
    }

    private HttpURLConnection getURLConnection(String method, String path) throws Exception {
        System.out.println("Create the URL Connection");
        URL url = new URL(hostURL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Accept", "application/json");
        System.out.println("Connection is created");
        return conn;
    }
    
}

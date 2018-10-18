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

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.logging.LogManager;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerConfiguration;
import io.helidon.webserver.WebServer;
//import io.helidon.webserver.json.JsonSupport;
import io.helidon.webserver.NotFoundException;
import io.helidon.common.http.Http;
//import io.helidon.metrics.MetricsSupport;
import io.helidon.common.http.MediaType;
//import io.helidon.metrics.MetricRegistry;
//import io.helidon.metrics.RegistryFactory;


/**
 * Simple Hello World rest application.
 */
public final class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() { }

    /**
     * Creates new {@link Routing}.
     *
     * @return the new instance
     */
    private static Routing createRouting() throws FileNotFoundException {
        //MetricsSupport metricsSupport = MetricsSupport.create();
 
        /*MetricRegistry registry = RegistryFactory
                .getRegistryFactory()
                .get()
                .getRegistry(MetricRegistry.Type.APPLICATION);*/
        return Routing.builder()
                .register("/weather", new WeatherService())
                .error( NotFoundException.class, (req,res,ex) -> {
                    ex.printStackTrace(System.out);
                    res.status(Http.Status.BAD_REQUEST_400).send();
                })
                .error( Throwable.class, (req, res, ex) -> {
                    ex.printStackTrace(System.out);
                    res.status(Http.Status.INTERNAL_SERVER_ERROR_500).send();
                     
                })
                .build();
                //.register(metricsSupport)  Needs to be added back after the first .register
    }

    /**
     * Application main entry point.
     * @param args command line arguments.
     * @throws IOException if there are problems reading logging properties
     */
    public static void main(final String[] args) throws IOException, FileNotFoundException {
        startServer();
    }

    /**
     * Start the server.
     * @return the created {@link WebServer} instance
     * @throws IOException if there are problems reading logging properties
     */
    protected static WebServer startServer() throws IOException {

        // load logging configuration
        LogManager.getLogManager().readConfiguration(
                Main.class.getResourceAsStream("/logging.properties"));

        // By default this will pick up application.yaml from the classpath
        Config config = Config.create();

        // Get webserver config from the "server" section of application.yaml
        ServerConfiguration serverConfig =
                ServerConfiguration.fromConfig(config.get("server"));

        WebServer server = WebServer.create(serverConfig, createRouting());

        // Start the server and print some info.
        server.start().thenAccept(ws -> {
            System.out.println(
                    "WEB server is up! http://localhost:" + ws.port());
        });

        // Server threads are not demon. NO need to block. Just react.
        server.whenShutdown().thenRun(()
                -> System.out.println("WEB server is DOWN. Good bye!"));

        return server;
    }
}

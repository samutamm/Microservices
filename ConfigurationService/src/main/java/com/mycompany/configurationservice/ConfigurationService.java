
package com.mycompany.configurationservice;

import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import spark.Spark;
import static spark.Spark.get;
import static spark.Spark.post;


public class ConfigurationService implements Runnable {
    
    public ConfigurationService() {        
    }
    
    @Override
    public void run() {
        configureServer();
        createRoutes();
    }

    private void configureServer() {
        String port = System.getenv("PORT");
        if (port != null) {
            Spark.port(Integer.parseInt(port));
        }
    }

    private void createRoutes() {
        get("/ping", (request, response) -> {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String dir = System.getProperty("user.dir");

            return "{ \"name\": \"" + name + "\", \"dir\": \"" + dir + "\" }";
        });
        
        JsonParser parser = new JsonParser();
        get("/configurations", (request, response) -> {
            return parser.parse("{\n" +
                                "  endpoint: {\n" +
                                "    persons: \"http://localhost:4567/persons\",\n" +
                                "    products: \"http://localhost:4568/products\",\n" +
                                "    session: \"http://localhost:4567/session\",\n" +
                                "    token: \"http://localhost:4567/tokens\"\n" +
                                "  },\n" +
                                "  mongoUrl: \"mongodb://ohtu:ohtu@ds055842.mongolab.com:55842/kanta1\",\n" +
                                "  mongoDb: \"kanta1\",\n" +
                                "  productsPort: 4568,\n" +
                                "  personsPort: 4567\n" +
                                "}").getAsJsonObject();
   
        });
        
        get("/test-configurations", (request, response) -> {
            return parser.parse("{\n" +
                                "  endpoint: {\n" +
                                "    persons: \"http://localhost:4567/persons\",\n" +
                                "    products: \"http://localhost:4568/products\",\n" +
                                "    session: \"http://localhost:4567/session\",\n" +
                                "    token: \"http://localhost:4567/tokens\"\n" +
                                "  },\n" +
                                "  mongoUrl: \"mongodb://ohtu:ohtu@ds055842.mongolab.com:55842/testikanta1\",\n" +
                                "  mongoDb: \"testikanta1\",\n" +
                                "  productsPort: 4568,\n" +
                                "  personsPort: 4567\n" +
                                "}").getAsJsonObject();
        });
        
        /*TODO Here should be checked from the environment variables
        * that the current instance is test instance.
        * Needs some playing with bash to pass variable from tests
        */ 
        post("/close",(request, response) -> {
            Spark.stop();
            return "TRUE";
        });
    }
}

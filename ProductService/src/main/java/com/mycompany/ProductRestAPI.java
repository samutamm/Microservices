package com.mycompany;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mycompany.domain.*;
import com.mycompany.domain.Error;
import com.mycompany.rest.ApiResponse;
import com.mycompany.rest.Http;
import java.io.IOException;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.lang.management.ManagementFactory;
import spark.Filter;
import spark.Response;
import spark.Route;
import spark.Spark;

import static spark.Spark.*;

public class ProductRestAPI {

    private Http http;
    private Gson gson;
    private Morphia morphia;
    private Datastore datastore;

    public ProductRestAPI(String confApi, String encryptionKey) {
        this.http = new Http(confApi);
        this.gson = new Gson();
        JsonObject configurations = http.getConfigurations();
        setUpDB(configurations);
        configure(configurations);
        createRoutes(encryptionKey);
    }

    private void configure(JsonObject configurations) {
        Spark.port(configurations.get("productsPort").getAsInt());
    }

    private void setUpDB(JsonObject conf) {
        this.morphia = new Morphia();
        MongoClient mongo = new MongoClient();
        MongoDatabase persons = mongo.getDatabase(conf.get("mongoDb").getAsString());
        morphia.mapPackage("com.mycompany.domain");
        this.datastore = morphia.createDatastore(mongo, persons.getName());
    }

    private void createRoutes(String encryptKey) {
        get("/ping", ping());
        before("/products", checkAuthorizationHeader(encryptKey));
        get("/products", getAllProducts(), new JsonTransformer());
        post("/products", registerProduct());
        
        /*TODO Here should be checked from the environment variables
        * that the current instance is test instance.
        * Needs some playing with bash to pass variable from tests
        */ 
        post("/close",(request, response) -> {
            Spark.stop();
            return "TRUE";
        });
    }

    private Route registerProduct() {
        return (request, response) -> {
            Product product = gson.fromJson(request.body(), Product.class);
            
            if ( product == null || !product.valid()) {
                halt(400, gson.toJson(com.mycompany.domain.Error.withCause("all fields must have a value")));
            }

            if ( datastore.createQuery(Product.class).field("name").equal(product.name()).get() != null ){
                halt(400, gson.toJson(Error.withCause("name must be unique")));
            }

            datastore.save(product);
            return product;
        };
    }

    private Route getAllProducts() {
        return (request, response) -> {
            return datastore.find(Product.class).asList();
        };
    }

    private Filter checkAuthorizationHeader(String encryptKey) {
        return (spark.Request request, Response response) -> {
            String authorization = request.headers("Authorization");
            if ( request.requestMethod().equals("GET")) {
                if (!Token.isValid(encryptKey, authorization)) {
                    halt(401, gson.toJson(Error.withCause("missing or invalid token. "
                            + "Please log in again.")));
                }
            }
        };
    }

    private void checkValidityFromServer(String authorization) throws JsonSyntaxException, IOException {
        String authURL = http.endpointForTokens() + "/" + authorization;
        ApiResponse apiResponse = http.get(authURL);
        JsonObject json = new JsonParser()
                .parse(apiResponse.getJson())
                .getAsJsonObject();
        if (json.get("valid").toString().equals("false")) {
            halt(401, gson.toJson(Error.withCause("missing or invalid token")));
        }
    }

    private static Route ping() {
        return (request, response) -> {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String dir = System.getProperty("user.dir");
            
            return "{ \"name\": \"" + name + "\", \"dir\": \"" + dir + "\" }";
        };
    }
}

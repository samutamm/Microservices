package com.mycompany.rest;

import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import com.mycompany.domain.Token;
import com.mycompany.domain.Person;
import java.io.IOException;

public class Http {

    private JsonObject configurations;
    private JsonObject apiEndpoints;
    private Token token = new Token("invalid");
    private CustomJsonParser parser;

    public Http(String confApi) {
        this.parser = new CustomJsonParser();
        this.configurations = fetch(confApi);
        this.apiEndpoints = configurations.get("endpoint").getAsJsonObject();
    }

    public JsonObject getConfigurations() {
        return configurations;
    }
    
    public String endpointForLogin() {
        return apiEndpoints.get("session").getAsString();
    }
    
    public String endpointForPersons() {
        return apiEndpoints.get("persons").getAsString();
    }

    public String endpointForProducts() {
        return apiEndpoints.get("products").getAsString();
    }

    public String endpointForTokens() {
        return apiEndpoints.get("token").getAsString();
    }
    
    public Token getToken() {
        return token;
    }

    public ApiResponse get(String url) throws IOException {
        System.out.println("URL: " + url);
        HttpResponse response = Request.Get(url)
                .addHeader("Authorization", token.toString())
                .execute().returnResponse();
        return new ApiResponse(parser, response);
    }
    
    public ApiResponse post(String url, Object data) throws IOException {
        HttpResponse httpResponse = postJson(url, parser.toJson(data));
        return new ApiResponse(parser, httpResponse);
    }
    
    public ApiResponse post(String url) throws IOException {
        HttpResponse httpResponse = Request.Post(url)
                .execute().returnResponse();
        return new ApiResponse(parser, httpResponse);
    }

    public ApiResponse login(String url, String username, String password) throws IOException {
        Person person = new Person(username, null, password, null);
        String asJson = parser.toJson(person);
        HttpResponse httpResponse = postJson(url, asJson);
        ApiResponse response = new ApiResponse(parser, httpResponse);
        if (response.ok()) {
            token = parser.tokenFromJson(response.getJson());
        }
        return response;
    }

    private HttpResponse postJson(String url, String asJson) throws IOException {
        HttpResponse httpResponse = Request.Post(url)
                .bodyString(asJson, ContentType.APPLICATION_JSON)
                .execute().returnResponse();
        return httpResponse;
    }

    private JsonObject fetch(String confApi) {
        try {
            ApiResponse response = get(confApi);
            if (response.ok()) {
                return parser.toJsonObject(response.getJson());
            }
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

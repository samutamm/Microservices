
package com.mycompany.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mycompany.domain.MongoSavable;
import com.mycompany.domain.Token;
import com.mycompany.domain.Error;


public class CustomJsonParser {
    private Gson gson;

    public CustomJsonParser() {
        this.gson = new Gson();
    }
    
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public MongoSavable[] fromJson(String responseBodyAsJson, Class<? extends MongoSavable[]> aClass) {
        return gson.fromJson(responseBodyAsJson, aClass);
    }

    public Token tokenFromJson(String json) {
        return gson.fromJson(json, Token.class);
    }

    public Error errorFromJson(String json) {
        return gson.fromJson(json, Error.class);
    }

    public JsonObject toJsonObject(String json) {
        return (JsonObject) new JsonParser().parse(json);
    }
    
}

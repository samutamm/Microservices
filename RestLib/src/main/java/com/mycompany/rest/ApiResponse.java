package com.mycompany.rest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import com.mycompany.domain.MongoSavable;
import com.mycompany.domain.Error;

import java.io.IOException;
import java.nio.charset.Charset;

public class ApiResponse {

    private HttpResponse response;
    private CustomJsonParser parser;
    
    public ApiResponse(CustomJsonParser parser, HttpResponse response) {
        this.parser = parser;
        this.response = response;
    }
    
    public int statusCode() {
        return response.getStatusLine().getStatusCode();
    }

    public boolean ok() {
        return response.getStatusLine().getStatusCode() == 200;
    }

    public MongoSavable[] body(MongoSavable[] domainClass) throws IOException {
        if (this.ok()) {
            String responseBodyAsJson = getJson();
            return parser.fromJson(responseBodyAsJson, domainClass.getClass());
        } else {
            return null;
        }
    }

    public String getJson() throws IOException {
        return IOUtils.toString(response.getEntity()
                        .getContent(), Charset.forName("UTF-8"));
    }
    
    public Error error() throws IOException {
        if (!this.ok()) {
            return parser.errorFromJson(getJson());
        }
        return null;
    }
}

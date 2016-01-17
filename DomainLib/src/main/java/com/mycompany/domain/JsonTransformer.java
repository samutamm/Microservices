package com.mycompany.domain;

import com.google.gson.Gson;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
    private Gson gson = new Gson();

    @Override
    public String render(Object model) {
        if (model instanceof List) {
            List rawObjects = (List) model;
            if (rawObjects.size() > 0 && rawObjects.get(0) instanceof MongoSavable) {

                List<MongoSavable> objects = (List<MongoSavable>) model;
                for (MongoSavable mongoSavable : objects) {
                    mongoSavable.identifier = mongoSavable.id.toHexString();
                    mongoSavable.id = null;
                }
            }
        } else if (model instanceof MongoSavable) {
            MongoSavable object = (MongoSavable) model;
            object.identifier = object.id.toHexString();
            object.id = null;
        }

        return gson.toJson(model);
    }
}

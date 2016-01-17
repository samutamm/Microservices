package com.mycompany.domain;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

@Entity
public class MongoSavable {
    @Id
    public ObjectId id;
    @Transient
    public String identifier;    
}

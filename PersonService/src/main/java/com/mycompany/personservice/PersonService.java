package com.mycompany.personservice;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoDatabase;
import com.mycompany.domain.Token;
import com.mycompany.domain.Person;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mycompany.domain.JsonTransformer;
import com.mycompany.domain.Error;
import com.mycompany.rest.Http;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import spark.Filter;
import spark.Route;
import spark.Spark;
import static spark.Spark.*;

public class PersonService implements Runnable {
    @Override
    public void run() {
        String confApi = System.getenv("CONF_API");
        String encryptKey = System.getenv("ENCRYPTION_KEY");
        if (confApi == null) {
            System.out.println("Missing conf_api environment variable.");
            return;
        }
        Http http = new Http(confApi);
        JsonObject configurations = http.getConfigurations();
        Gson gson = new Gson();

        Datastore datastore = connectDB(configurations);
        
        Spark.port(configurations.get("personsPort").getAsInt());
        
        Set<String> validTokens = new HashSet<String>();
        
        before(printStuff());
        get("/ping", ping());
        before("/persons", checkAuthorizationHeader(gson, encryptKey));
        get("/persons", getAllPersons(datastore), new JsonTransformer());
        post("/persons", registerNewPerson(gson, datastore), new JsonTransformer());
        post("/session", logIn(gson, datastore, encryptKey), new JsonTransformer());
        get("/tokens/:token_value", checkToken(validTokens));

        /*TODO Here should be checked from the environment variables
        * that the current instance is test instance.
        * Needs some playing with bash to pass variable from tests
        */ 
        post("/close",(request, response) -> {
            Spark.stop();
            return "TRUE";
        });
        
        after((request, response) -> {
            response.type("application/json");
        });
    }

    private static Route checkToken(Set<String> validTokens) {
        return (request, response) -> {
            String token = request.params(":token_value");
            JsonObject json = new JsonObject();
            json.addProperty("token", token);
            json.addProperty("valid", validTokens.contains(token));
            return json;
        };
    }

    private static Route logIn(Gson gson, 
            Datastore datastore, String encryptKey) {
        return (request, response) -> {
            Person dataInRequest = gson.fromJson(request.body(), Person.class);
            
            Person person = datastore.createQuery(Person.class).field("username").equal(dataInRequest.username()).get();
            
            if ( person==null || !person.password().equals(dataInRequest.password()) ) {
                halt(401, gson.toJson(Error.withCause( "invalid credentials")));
            }
            
            int secondsTokenValid = 10;//5 * 60;
            Token token = Token.generate(encryptKey, secondsTokenValid);
            return token;
        };
    }

    private static Route registerNewPerson(Gson gson, Datastore datastore) {
        return (request, response) -> {
            Person person = gson.fromJson(request.body(), Person.class);
            
            if ( person == null || !person.valid()) {
                halt(400, gson.toJson(Error.withCause("all fields must have a value")));
            }
            
            if ( datastore.createQuery(Person.class).field("username").equal(person.username()).get() != null ){
                halt(400, gson.toJson(Error.withCause("username must be unique")));
            }
            
            datastore.save(person);
            return person;
        };
    }

    private static Route getAllPersons(Datastore datastore) {
        return (request, response) -> {
            return datastore.find(Person.class).asList();
        };
    }

    private static Filter checkAuthorizationHeader(Gson gson, String encryptKey) {
        return (request, response) -> {
            System.out.println("AUTH: " + request.headers("Authorization"));
            if ( request.requestMethod().equals("GET") &&
                    !Token.isValid(encryptKey, request.headers("Authorization") ) ){
                halt(401, gson.toJson(Error.withCause("missing or invalid token")));
            }
        };
    }

    private static Route ping() {
        return (request, response) -> {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String dir = System.getProperty("user.dir");
            
            return "{ \"name\": \"" + name + "\", \"dir\": \"" + dir + "\" }";
        };
    }

    private static Filter printStuff() {
        return (request, response)->{
            System.out.println(request.requestMethod());
            request.headers().forEach((header) -> System.out.println(header + " = " + request.headers(header)));
            System.out.println(request.contentType());
            request.cookies().forEach((key, value) -> System.out.println(key + " = " + value));
            request.attributes().forEach(System.out::println);
            System.out.println(request.userAgent());
            System.out.println(request.body());
        };
    }

    private static Datastore connectDB(JsonObject configurations) {
        Morphia morphia = new Morphia();
        MongoClient mongo = new MongoClient();
        MongoDatabase persons = mongo.getDatabase(configurations.get("mongoDb").getAsString());
        morphia.mapPackage("com.mycompany.domain");
        Datastore datastore = morphia.createDatastore(mongo, persons.getName());
        return datastore;
    }
}

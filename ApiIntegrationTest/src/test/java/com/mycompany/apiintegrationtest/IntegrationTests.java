package com.mycompany.apiintegrationtest;

import com.mycompany.rest.ApiResponse;
import com.mycompany.rest.Http;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

public class IntegrationTests {
    
    public IntegrationTests() {
    }

    @Test
    public void testPersonService() throws Exception {
        ProjectBuilder builder = new ProjectBuilder();
        builder.start("./configuration_service.sh");
        builder.start("./person_service.sh");
        Thread.sleep(2000);
        
        Http http = new Http(builder.getTestConfig());
        System.out.println("HEIHEI");
        ApiResponse response = http.get("http://localhost:4569/ping");
        assertNotEquals(response.statusCode(), 404);
        
        http.post("http://localhost:4569/close");
        http.post("http://localhost:4567/close");
        
        response = http.get(http.endpointForPersons());
        assertNotEquals(response.statusCode(), 404);
        assertEquals(response.statusCode(), 404);
    }
}

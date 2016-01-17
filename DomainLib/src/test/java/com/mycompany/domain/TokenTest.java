package com.mycompany.domain;

import org.junit.Test;
import static org.junit.Assert.*;

public class TokenTest {
    
    private String encryptKey = "foobari";
    
    public TokenTest() {
    }

    @Test
    public void testGenerate() {
        String token = Token.generate(encryptKey, 1).toString();
        assertTrue(token.length() > 30);
    }

    @Test
    public void testIsValid() throws InterruptedException {
        Token token = Token.generate(encryptKey, 0);
        assertFalse(Token.isValid(encryptKey, token.toString()));
        
        token = Token.generate(encryptKey, 1);
        assertTrue(Token.isValid(encryptKey, token.toString()));
        
        token = Token.generate(encryptKey, 1);
        Thread.sleep(990);
        assertTrue(Token.isValid(encryptKey, token.toString()));
        
        token = Token.generate(encryptKey, 1);
        Thread.sleep(1001);
        assertFalse(Token.isValid(encryptKey, token.toString()));
    }
    
}

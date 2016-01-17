package com.mycompany.domain;

import java.util.Calendar;

public class Token {
    private String token;

    public Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }
    
    public static Token generate(String salt, int minutes){
        String encryptedTime = generateTimePart(minutes, salt);
        Token t = new Token(encryptedTime);
        return t;
    }

    private static String generateTimePart(int seconds, String encryptKey) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, seconds);
        Encrypter encrypter = new Encrypter(encryptKey);
        long timeInMillis = cal.getTimeInMillis();
        String encryptedTime = encrypter.encrypt(String.valueOf(timeInMillis));
        return encryptedTime;
    }
    
    public static boolean isValid(String encryptKey, String tokenValue) {
        if (tokenValue.length() < 30) {
            return false;
        }
        System.out.println("TOKEN VALUE TO DECRYPT: " + tokenValue);
        Encrypter encrypter = new Encrypter(encryptKey);
        String decrypted = encrypter.decrypt(tokenValue);
        return Long.parseLong(decrypted) >= System.currentTimeMillis();
    }
}

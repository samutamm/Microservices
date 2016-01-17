package com.mycompany;

public class Main {

    public static void main(String[] args) {
        String confApi = System.getenv("CONF_API");
        String encryptionKey = System.getenv("ENCRYPTION_KEY");
        if (confApi == null || encryptionKey == null) {
            System.out.println("Missing environment variables.");
            return;
        }
        new ProductRestAPI(confApi, encryptionKey);
    }
}

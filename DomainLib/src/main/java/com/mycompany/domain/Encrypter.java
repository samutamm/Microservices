
package com.mycompany.domain;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class Encrypter {
    
    private StandardPBEStringEncryptor encrypter;
    
    public Encrypter(String encryptKey) {
        this.encrypter = new StandardPBEStringEncryptor();
        this.encrypter.setPassword(encryptKey);
        this.encrypter.initialize();
    }
    
    public String decrypt(String encryptedMessage) {
        return this.encrypter.decrypt(encryptedMessage);
    }
    
    public String encrypt(String message) {
        return this.encrypter.encrypt(message);
    }
}

package com.mycompany.domain;

public class Person extends MongoSavable {

    private String name;
    private String username;
    private String address;
    private String password;

    public Person() {
    }

    public Person(String username, String name, String pw, String address) {
        this.name = name;
        password = pw;
        this.username = username;
        this.address = address;
    }    
    
    public Person(String name, String username, String address) {
        this.name = name;
        this.username = username;
        this.address = address;
    }

    public String name() {
        return name;
    }

    public String username() {
        return username;
    }
 
    public String password() {
        return password;
    }
    
    public boolean valid() {
        return username!=null && !username.isEmpty() && 
               name!=null && !name.isEmpty() && 
               address!=null && !address.isEmpty() && 
               password!=null && !password.isEmpty();
    }

    @Override
    public String toString() {
        return username+ " " +name+ " "+ address;
    }
}

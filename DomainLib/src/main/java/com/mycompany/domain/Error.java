package com.mycompany.domain;

public class Error {
    String error;

    @Override
    public String toString() {
        return "Error: "+error;
    }

    public static Error withCause(String cause){
        Error e = new Error();
        e.error = cause;
        return e;
    }
}



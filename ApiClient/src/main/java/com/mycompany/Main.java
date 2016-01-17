package com.mycompany;

import java.util.Scanner;
import com.mycompany.domain.Person;
import com.mycompany.domain.Product;
import com.mycompany.rest.Http;
import com.mycompany.rest.ApiResponse;

public class Main {

    public static void main(String[] args) throws Exception {
        String confApi = System.getenv("CONF_API");
        if (confApi == null) {
            System.out.println("Missing conf_api environment variable.");
            return;
        }
        Http http = new Http(confApi);

        Scanner scanner = new Scanner(System.in);
        while (true) {            
            System.out.print("> ");
            String komentorivi = scanner.nextLine();
            String[] komento = komentorivi.split(" ");
            
            if (komento[0].equals("login") ) {
                ApiResponse response = http.login(
                        http.endpointForLogin(), komento[1], komento[2]
                );
                if (response.ok()) {
                    System.out.println("success! got token " + http.getToken().toString());
                } else {
                    System.out.println(response.error());
                }
            } if (komento[0].equals("register")){
                if (komento.length != 5) {
                    System.out.println("Please give new person in format: \n register username name password address");
                } else {
                    Person person = new Person(komento[1], komento[2], komento[3], komento[4]);
                    if(person.valid()) {
                        ApiResponse response = http.post(http.endpointForPersons(), person);
                        if (response.ok()) {
                            System.out.println("success! person " + person.name() + " registred." );
                        } else {
                            System.out.println(response.error());
                        }
                    }
                }
                // implement registration here
            } if (komento[0].equals("persons")){
                ApiResponse response = http.get(http.endpointForPersons());
                
                if (response.ok()) {
                    Person[] persons = (Person[]) response.body(new Person[0]);
                    for (Person person : persons) {
                        System.out.println(person);
                    }
                } else {
                    System.out.println(response.error());
                }                
            } else if(komento[0].equals("add-product")) {
                if(komento.length != 5) {
                    System.out.println("command should be like: \n add-product name producer price inStock");
                } else {
                    Product product = new Product(
                            komento[1], komento[2],
                            Integer.parseInt(komento[3]),
                            Integer.parseInt(komento[4])
                    );
                    if (product.valid()) {
                        ApiResponse response = http.post(http.endpointForProducts(), product);
                        if (response.ok()) {
                            System.out.println("success! product " + product.name() + " registred." );
                        } else {
                            System.out.println(response.error());
                        }
                    }
                }
            } else if(komento[0].equals("products")) {
                ApiResponse response = http.get(http.endpointForProducts());
                if (response.ok()) {
                    Product[] products = (Product[]) response.body(new Product[0]);
                    for (Product product : products) {
                        System.out.println(product);
                    }
                } else {
                    System.out.println(response.error());
                }
            }
        }
    }
}

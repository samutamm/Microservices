package com.mycompany.domain;

public class Product extends MongoSavable {

    private String name;
    private String producer;
    private int price;
    private int inStock;

    public Product() {
    }

    public Product(String name, String producer, int price, int inStock) {
        this.name = name;
        this.producer = producer;
        this.price = price;
        this.inStock = inStock;
    }

    public String name() {
        return name;
    }

    public String producer() {
        return producer;
    }

    public int price() {
        return price;
    }

    public int inStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean valid() {
        return producer!=null && !producer.isEmpty() &&
                name!=null && !name.isEmpty();
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", producer='" + producer + '\'' +
                ", price=" + price +
                ", inStock=" + inStock +
                '}';
    }
}

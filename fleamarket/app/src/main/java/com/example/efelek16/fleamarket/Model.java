package com.example.efelek16.fleamarket;

/**
 * Created by efelek16 on 19.03.2019.
 */

public class Model {
    private String id;
    private String name;
    private String price;
    private String username;
    private String email;
    private String phone;

    public Model(String id,
                 String name, String price, String username,
                  String email, String phone) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return name+" Preis: "+price;
    }
}
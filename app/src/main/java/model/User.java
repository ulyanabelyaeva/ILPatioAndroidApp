package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String id;
    public String name;
    public String phone;
    public String email;
    public List <Integer> cart;

    public String city;
    public User(){}
    public User(String id, String name, String phone, String email, String city) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        cart = new ArrayList<>();
        this.city = city;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Integer> getCart() { return cart; }
    public void setCart(List<Integer> cart) { this.cart = cart; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}

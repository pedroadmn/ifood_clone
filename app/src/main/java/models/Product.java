package models;

import com.google.firebase.database.DatabaseReference;

import helpers.FirebaseConfig;

public class Product {
    private String productId;
    private String userId;
    private String name;
    private String description;
    private double price;

    public Product() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference productsRef = firebaseRef.child("products");

        setProductId(productsRef.push().getKey());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void save() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference productsRef = firebaseRef.child("products").child(getUserId()).child(getProductId());
        productsRef.setValue(this);
    }

    public void remove() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference productsRef = firebaseRef.child("products").child(getUserId()).child(getProductId());
        productsRef.removeValue();
    }
}

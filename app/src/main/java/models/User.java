package models;

import com.google.firebase.database.DatabaseReference;

import helpers.FirebaseConfig;

public class User {

    private String userId;
    private String name;
    private String address;

    public User() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void save() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference usersRef = firebaseRef.child("users").child(getUserId());
        usersRef.setValue(this);
    }
}

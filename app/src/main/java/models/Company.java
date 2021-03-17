package models;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import helpers.FirebaseConfig;

public class Company implements Serializable {
    private String idUser;
    private String urlImage;
    private String name;
    private String time;
    private String category;
    private String tax;

    public Company() {
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public void save() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference companiesRef = firebaseRef.child("companies").child(getIdUser());
        companiesRef.setValue(this);
    }
}

package com.example.xchange;

import com.example.xchange.helper.ConfigurationFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {

    private String idItem;
    private String state;
    private String category;
    private String title;
    private String phone;
    private String email;
    private String description;
    private List<String> photo;


    public Item() {

        DatabaseReference itemRef = ConfigurationFirebase.getFirebase()
                .child("my items");
        setIdItem(itemRef.push().getKey());
    }


    public void save(){
        String idUser = ConfigurationFirebase.getIdUser();
        DatabaseReference itemRef = ConfigurationFirebase.getFirebase()
                .child("my items");

        itemRef.child(idUser)
                .child(getIdItem())
                .setValue(this);

        saveItemPublic();
    }

    public void remove(){
        String idUser = ConfigurationFirebase.getIdUser();
        DatabaseReference itemRef = ConfigurationFirebase.getFirebase()
                .child("my items")
                .child(idUser)
                .child(getIdItem());

        itemRef.removeValue();
        removeItemPublic();
    }

    public void removeItemPublic(){
        DatabaseReference itemRef = ConfigurationFirebase.getFirebase()
                .child("items")
                .child(getState())
                .child(getCategory())
                .child(getIdItem());

        itemRef.removeValue();
    }

    public void saveItemPublic(){
        DatabaseReference itemRef = ConfigurationFirebase.getFirebase()
                .child("items");

        itemRef.child(getState())
                .child(getCategory())
                .child(getIdItem())
                .setValue(this);
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhoto() {
        return photo;
    }

    public void setPhoto(List<String> photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail(){
        return email;
    }


}

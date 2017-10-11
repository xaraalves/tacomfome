package com.antoinecampbell.firebase.demo;

import java.util.ArrayList;


/**
 * Created by Gabriel on 10/10/2017.
 */

public class User {
    private String fullName;
    private String firebaseId;
    private ArrayList<String> ownedProducts;

    public User() {
        //this.ownedProducts = new ArrayList<>();
    }

    public User(User inUser) {
        this.fullName = inUser.getFullName();
        this.firebaseId = inUser.getFirebaseId();

        this.ownedProducts = new ArrayList<>(inUser.getOwnedProducts().size());
        for(String item : inUser.getOwnedProducts()) {
            this.ownedProducts.add(item);
        }
    }

    public void setFullName(String name) {  this.fullName = name;   }

    public String getFullName() {   return this.fullName;   }

    public void setFirebaseId(String uid) { this.firebaseId = uid;  }

    public String getFirebaseId() { return this.firebaseId; }

    public void addOwnedProduct(String productUid) { this.ownedProducts.add(productUid); }

    public ArrayList<String> getOwnedProducts() {   return this.ownedProducts;  }


}

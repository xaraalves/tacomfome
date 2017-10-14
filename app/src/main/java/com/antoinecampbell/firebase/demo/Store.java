package com.antoinecampbell.firebase.demo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Gabriel on 10/10/2017.
 */

public class Store implements Parcelable {
    private String title;
    //private ArrayList<String> categories;
    //private ArrayList<Product> products;
    private String ownerId;
    private String storeId;
    private String categoria;

    public void setTitle(String title) {    this.title = title; }

    public String getTitle() {  return this.title;  }

    public void setCategoria(String title) {    this.categoria = categoria; }

    public String getCategoria() {  return this.categoria;  }

    public void setOwnerId(String ownerId) {    this.ownerId = ownerId; }

    public String getOwnerId() {    return this.ownerId;    }

//    public void setCategories(ArrayList<String> categories) {
//        for(String category : categories) {
//            this.categories.add(category);
//        }
//    }
//
//    public ArrayList<String> getCategories() {  return this.categories; }
//
//    public void addProduct(Product product) {
//        this.products.add(product);
//    }
//
//    public ArrayList<Product> getProducts() {  return this.products; }
//
    public void setStoreId(String uid) {    this.storeId = uid; }

    public String getStoreId() {  return this.storeId;  }

    @Override
    public int describeContents() { return 0;   }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.storeId);
        dest.writeString(this.title);
        dest.writeString(this.ownerId);
    }

    public Store() {
    }

    protected Store(Parcel in) {
        this.storeId = in.readString();
        this.title = in.readString();
        this.ownerId = in.readString();
    }

    public static final Parcelable.Creator<Store> CREATOR = new Parcelable.Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel source) {
            return new Store(source);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Store store = (Store) o;

        if (storeId != null ? !storeId.equals(store.storeId) : store.storeId != null) return false;
        if (title != null ? !title.equals(store.title) : store.title != null) return false;
        return ownerId != null ? ownerId.equals(store.ownerId) : store.ownerId == null;

    }

    @Override
    public int hashCode() {
        int result = storeId != null ? storeId.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        return result;
    }
}

package com.antoinecampbell.firebase.demo;

/**
 * Created by Gabriel on 10/10/2017.
 */

public class Product {
    private String title;
    private double value;
    private String uid;

    public void setTitle(String title) {    this.title = title; }

    public String getTitle() {   return this.title;  }

    public void setValue(double value) {    this.value = value; }

    public double getValue() {  return this.value;  }

    public void setProductId(String uid) {  this.uid = uid; }

    public String getProductId() {  return this.uid;    }
}

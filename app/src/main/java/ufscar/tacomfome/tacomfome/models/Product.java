package ufscar.tacomfome.tacomfome.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gabriel on 14/10/2017.
 */

public class Product implements Parcelable {
    private String productId;
    private String productName;
    private String sellingPlace;
    private String price;
    private String sellerName;
    private String sellerId;
    private String sellingPeriod;
    private String categorie;
    private Integer numLikes;
//    private Uri imageURL;
    private String imageStoragePath;
    private long timestamp;
    private String description;


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSellingPlace() {
        return sellingPlace;
    }

    public void setSellingPlace(String sellingPlace) {
        this.sellingPlace = sellingPlace;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellingPeriod() {
        return sellingPeriod;
    }

    public void setSellingPeriod(String sellingPeriod) {
        this.sellingPeriod = sellingPeriod;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getNumLikes() { return this.numLikes; }
    public void incrementNumLikes() { this.numLikes = numLikes + 1; }
    public void decrementNumLikes() { this.numLikes = numLikes - 1; }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

//    public Uri getImageURL() { return this.imageURL; }
//
//    public void setImageURL(Uri url) { this.imageURL = url; }

    public String getImageStoragePath() {   return this.imageStoragePath;   }

    public void setImageStoragePath(String path) {  this.imageStoragePath = path;   }

    public String getDescription() {    return this.description;    }

    public void setDescription(String desc) {   this.description = desc;    }

    @Override
    public int describeContents() { return 0;   }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productId);
        dest.writeString(this.productName);
        dest.writeString(this.sellingPlace);
        dest.writeString(this.price);
        dest.writeString(this.sellerName);
        dest.writeString(this.sellingPeriod);
        dest.writeString(this.categorie);
        dest.writeString(this.numLikes.toString());
    }

    public Product() {
        this.numLikes = 0;
        this.imageStoragePath = "comida6.jpg";
    }

    public Product(Parcel in) {
        this.productId = in.readString();
        this.productName = in.readString();
        this.sellingPlace = in.readString();
        this.price = in.readString();
        this.sellerName = in.readString();
        this.sellingPeriod = in.readString();
        this.categorie = in.readString();
        this.numLikes = in.readInt();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (productId != null ? !productId.equals(product.productId) : product.productId != null) return false;
        if (productName != null ? !productName.equals(product.productName) : product.productName != null) return false;
        return sellerId != null ? sellerId.equals(product.sellerId) : product.sellerId == null;

    }

    @Override
    public int hashCode() {
        int result = productId != null ? productId.hashCode() : 0;
        result = 31 * result + (productName != null ? productName.hashCode() : 0);
        result = 31 * result + (sellerId != null ? sellerId.hashCode() : 0);
        return result;
    }
}

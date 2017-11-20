package ufscar.tacomfome.tacomfome.extras;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ufscar.tacomfome.tacomfome.models.Product;

/**
 * Created by Gabriel on 19/11/2017.
 */

public class ProductList {

    private static ProductList ourInstance = new ProductList();

    private List<Product> allProducts = new ArrayList<>();
    private List<Product> allProducts1 = new ArrayList<>();

    private boolean finished = true;

    public static ProductList getInstance() {
        return ourInstance;
    }

    public ProductList() {
        obtainAllProducts();
    }

    public List<Product> getAllProductsAlphabetically() {
        //obtainAllProducts();
        waitForSync();
        orderByName(allProducts);

        return allProducts;
    }

    public List<Product> getAllProductsByLikesDesc() {
        obtainAllProducts();
        orderByLikesDesc(allProducts);

        return allProducts;
    }

    public List<Product> getAllProductsByPriceAsc() {
        obtainAllProducts();
        orderByPriceAsc(allProducts);

        return allProducts;
    }

    public List<Product> getAllProductsByDateDesc() {
        obtainAllProducts();
        orderByDateDesc(allProducts);

        return allProducts;
    }


    private void obtainAllProducts() {
        final List<Product> myProducts = new ArrayList<>();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //myProducts.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    finished = false;
                    myProducts.add(data.getValue(Product.class));
                }
                allProducts.addAll(myProducts);
                finished = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        return myProducts;
    }

    private void obtainCafes() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allProducts.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    allProducts.add(data.getValue(Product.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /* Orders by the product name, alphabetically */
    private static void orderByName(List<Product> lista) {
        Collections.sort(lista, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getProductName().compareTo(o2.getProductName());
            }
        });
    }

    /* Orders by numLikes, descending, alphabetically */
    private static void orderByLikesDesc(List<Product> lista) {
        orderByName(lista);
        Collections.sort(lista, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o2.getNumLikes().compareTo(o1.getNumLikes());
            }
        });
    }

    /* Orders by cost, ascending, alphabetically */
    private static void orderByPriceAsc(List<Product> lista) {
        orderByName(lista);
        Collections.sort(lista, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getPrice().compareTo(o2.getPrice());
            }
        });
    }

    /* Orders by date, descending, alphabetically */
    private static void orderByDateDesc(List<Product> lista) {
        orderByName(lista);
        Collections.sort(lista, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o2.getTimestamp() < o1.getTimestamp() ? -1 :
                        o2.getTimestamp() > o1.getTimestamp() ? 1 : 0;
            }
        });
    }

    private void waitForSync() {
        while(finished == false) {

        }
    }
}

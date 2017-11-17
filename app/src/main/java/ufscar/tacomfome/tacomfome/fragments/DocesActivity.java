package ufscar.tacomfome.tacomfome.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ufscar.tacomfome.tacomfome.R;
import ufscar.tacomfome.tacomfome.adapters.ProductRecyclerViewAdapter;
import ufscar.tacomfome.tacomfome.models.Product;

/**
 * Created by Gabriel on 13/10/2017.
 */

public class DocesActivity extends Fragment {

    private ProgressDialog progressDialog;
    private RecyclerView mRecyclerView;
    private View mView;
    private List<Product> products = new ArrayList<>();
    //private List<String> mDatakey = new ArrayList<>();
    //private FormAdapter adapter;
    private ProductRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todos, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mView = view;
        init();
        /*progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Syncing...");
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        // Sort by name
        //loadDocesAlphabetically();

        // Sort by number of likes
        //loadDocesByLikesDesc();

        // Sort by cost
        //loadDocesByPriceAsc();

        // Sort by date
        loadDocesByDateDesc();
    }

    private void init() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.todos_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductRecyclerViewAdapter(products);
        mRecyclerView.setAdapter(adapter);
    }

    /*
    private void loadData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getValue(Product.class).getCategorie() != null
                            && data.getValue(Product.class).getCategorie().toLowerCase().contains("doce")) {
                        products.add(data.getValue(Product.class));
                       // mDatakey.add(data.getKey().toString());
                    }
                }
                adapter.notifyDataSetChanged();
//                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    */

    private void loadDocesAlphabetically() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getValue(Product.class).getCategorie() != null
                            && data.getValue(Product.class).getCategorie().toLowerCase().contains("doce")) {
                        products.add(data.getValue(Product.class));
                        // mDatakey.add(data.getKey().toString());
                    }
                }
                orderByName(products);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /* orders by likes: descending, alphabetically */
    private void loadDocesByLikesDesc() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getValue(Product.class).getCategorie() != null
                            && data.getValue(Product.class).getCategorie().toLowerCase().contains("doce")) {
                        products.add(data.getValue(Product.class));
                        // mDatakey.add(data.getKey().toString());
                    }
                }
                orderByLikesDesc(products);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /* orders by price: ascending, alphabetically */
    private void loadDocesByPriceAsc() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getValue(Product.class).getCategorie() != null
                            && data.getValue(Product.class).getCategorie().toLowerCase().contains("doce")) {
                        products.add(data.getValue(Product.class));
                        // mDatakey.add(data.getKey().toString());
                    }
                }
                orderByPriceAsc(products);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /* orders by date: descending, alphabetically */
    private void loadDocesByDateDesc() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getValue(Product.class).getCategorie() != null
                            && data.getValue(Product.class).getCategorie().toLowerCase().contains("doce")) {
                        products.add(data.getValue(Product.class));
                        // mDatakey.add(data.getKey().toString());
                    }
                }
                orderByDateDesc(products);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
}

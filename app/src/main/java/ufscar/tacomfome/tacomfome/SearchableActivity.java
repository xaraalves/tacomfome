package ufscar.tacomfome.tacomfome;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ufscar.tacomfome.tacomfome.adapters.ProductRecyclerViewAdapter;
import ufscar.tacomfome.tacomfome.extras.ProductList;
import ufscar.tacomfome.tacomfome.fragments.TodosActivity;
import ufscar.tacomfome.tacomfome.models.Product;
import ufscar.tacomfome.tacomfome.provider.SearchableProvider;

/**
 * Created by Gabriel on 18/11/2017.
 */


public class SearchableActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<Product> mList = new ArrayList<>();
    private List<Product> mListAux = new ArrayList<>();
    private ProductRecyclerViewAdapter adapter;
    private CoordinatorLayout clContainer;

    //private ProductList productList = ProductList.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clContainer = (CoordinatorLayout) findViewById(R.id.cl_container);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductRecyclerViewAdapter(mList);
        mRecyclerView.setAdapter(adapter);

        handleSearch( getIntent() );
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent( intent );
        handleSearch( intent );
    }

    public void handleSearch( Intent intent ){
        if( Intent.ACTION_SEARCH.equalsIgnoreCase( intent.getAction() ) ){
            String q = intent.getStringExtra( SearchManager.QUERY );

            mToolbar.setTitle(q);
            //filterCars( q );

            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);
            searchRecentSuggestions.saveRecentQuery( q, null );

            loadFilteredProductsByDateDesc(q);
        }
    }

    /*
    public void filterCars( String q ){
        mListAux.clear();

        for( int i = 0, tamI = mList.size(); i < tamI; i++ ){
            if( mList.get(i).getProductName().toLowerCase().contains( q.toLowerCase() ) ){
                mListAux.add( mList.get(i) );
            }
        }

        if(mList.isEmpty()) {
            new AlertDialog.Builder(SearchableActivity.this)
                    .setMessage("mList vazia")
                    .setCancelable(false)
                    .setNegativeButton("Cancelar", null)
                    .show();
        }

        mRecyclerView.setVisibility( mListAux.isEmpty() ? View.GONE : View.VISIBLE);
        if( mListAux.isEmpty() ){
            TextView tv = new TextView( this );
            tv.setText( "Nenhum produto encontrado." );
            tv.setTextColor( getResources().getColor( R.color.colorAccent) );
            tv.setId( 1 );
            tv.setLayoutParams( new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT )  );
            tv.setGravity(Gravity.CENTER);

            clContainer.addView( tv );
        }
        else if( clContainer.findViewById(1) != null ) {
            clContainer.removeView( clContainer.findViewById(1) );
        }

        adapter.notifyDataSetChanged();
    }*/


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mList", (ArrayList<Product>) mList);
        outState.putParcelableArrayList("mListAux", (ArrayList<Product>) mListAux);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchable_activity, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        MenuItem item = menu.findItem(R.id.action_searchable_activity);

        searchView = (SearchView) item.getActionView();

        searchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );
        searchView.setQueryHint( getResources().getString(R.string.search_hint) );

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        else if( id == R.id.action_delete ){

            new AlertDialog.Builder(SearchableActivity.this)
                    .setMessage("Apagar histórico de pesquisa?")
                    .setCancelable(false)
                    .setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(getApplicationContext(),
                                    SearchableProvider.AUTHORITY,
                                    SearchableProvider.MODE);

                            searchRecentSuggestions.clearHistory();

                            Toast.makeText(getApplicationContext(), "Histórico de pesquisa apagado.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();

        }

        return true;
    }

    private void loadFilteredProductsAlphabetically() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    mList.add(data.getValue(Product.class));
                }
                orderByName(mList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /* orders by likes: descending, alphabetically */
    private void loadFilteredProductsByLikesDesc() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    mList.add(data.getValue(Product.class));
                }
                orderByLikesDesc(mList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /* orders by price: ascending, alphabetically */
    private void loadFilteredProductsByPriceAsc() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    mList.add(data.getValue(Product.class));
                }
                orderByPriceAsc(mList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /* orders by date: descending, alphabetically */
    private void loadFilteredProductsByDateDesc(String query) {
        final String q = query;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getValue(Product.class).getProductName().toLowerCase().contains(q.toLowerCase())) {
                        mList.add(data.getValue(Product.class));
                    }
                }
                orderByDateDesc(mList);
                adapter.notifyDataSetChanged();
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

}

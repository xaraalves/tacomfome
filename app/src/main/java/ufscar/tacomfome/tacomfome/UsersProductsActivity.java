package ufscar.tacomfome.tacomfome;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.support.v7.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ufscar.tacomfome.tacomfome.adapters.ProductRecyclerViewAdapter;
import ufscar.tacomfome.tacomfome.models.Product;
import ufscar.tacomfome.tacomfome.provider.SearchableProvider;

/**
 * Created by Gabriel on 18/11/2017.
 */


public class UsersProductsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<Product> mList = new ArrayList<>();
    private ProductRecyclerViewAdapter adapter;
    private CoordinatorLayout clContainer;

    private DatabaseReference database;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    private Spinner spinner_ranking;
    String[] lista_rank;
    ArrayAdapter adapter_rank;

    boolean hasResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(getResources().getString(R.string.my_products));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clContainer = (CoordinatorLayout) findViewById(R.id.cl_container);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductRecyclerViewAdapter(mList);
        mRecyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        // Set up the spinner to choose the products order
        setOrderBySpinner();

        loadFilteredProducts();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent( intent );
        loadFilteredProducts();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mList", (ArrayList<Product>) mList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_searchable_activity, menu);
//
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView;
//        MenuItem item = menu.findItem(R.id.action_searchable_activity);
//
//        searchView = (SearchView) item.getActionView();
//
//        searchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );
//        searchView.setQueryHint( getResources().getString(R.string.search_hint) );

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
            goMainScreen();
        }
        else if( id == R.id.action_delete ){

            new AlertDialog.Builder(UsersProductsActivity.this)
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

    /* The first order when loading the page. After this user can select order from spinner */
    private void loadFilteredProducts() {
        // Sort by name
        //loadFilteredProductsAlphabetically();

        // Sort by number of likes
        //loadFilteredProductsByLikesDesc();

        // Sort by cost
        //loadFilteredProductsByPriceAsc();

        // Sort by date
        loadFilteredProductsByDateDesc();
    }

    private void loadFilteredProductsAlphabetically() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getValue(Product.class).getSellerId().equals(user.getUid())) {
                        mList.add(data.getValue(Product.class));
                        hasResult = true;
                        clContainer.removeView( clContainer.findViewById(1) );
                    }
                }
                orderByName(mList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mRecyclerView.setVisibility( hasResult == false ? View.GONE : View.VISIBLE);
                if( hasResult == false ){
                    TextView tv = new TextView( UsersProductsActivity.this );
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
            }
        }, 10);

    }

    /* orders by likes: descending, alphabetically */
    private void loadFilteredProductsByLikesDesc() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getValue(Product.class).getSellerId().equals(user.getUid())) {
                        mList.add(data.getValue(Product.class));
                        hasResult = true;
                        clContainer.removeView( clContainer.findViewById(1) );
                    }
                }
                orderByLikesDesc(mList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mRecyclerView.setVisibility( hasResult == false ? View.GONE : View.VISIBLE);
                if( hasResult == false ){
                    TextView tv = new TextView( UsersProductsActivity.this );
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
            }
        }, 10);

    }

    /* orders by price: ascending, alphabetically */
    private void loadFilteredProductsByPriceAsc() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getValue(Product.class).getSellerId().equals(user.getUid())) {
                        mList.add(data.getValue(Product.class));
                        hasResult = true;
                        clContainer.removeView( clContainer.findViewById(1) );
                    }
                }
                orderByPriceAsc(mList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mRecyclerView.setVisibility( hasResult == false ? View.GONE : View.VISIBLE);
                if( hasResult == false ){
                    TextView tv = new TextView( UsersProductsActivity.this );
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
            }
        }, 10);

    }

    /* orders by date: descending, alphabetically */
    private void loadFilteredProductsByDateDesc() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getValue(Product.class).getSellerId().equals(user.getUid())) {
                        mList.add(data.getValue(Product.class));
                        hasResult = true;
                        clContainer.removeView( clContainer.findViewById(1) );
                    }
                }
                orderByDateDesc(mList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mRecyclerView.setVisibility( hasResult == false ? View.GONE : View.VISIBLE);
                if( hasResult == false ){
                    TextView tv = new TextView( UsersProductsActivity.this );
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
            }
        }, 10);

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

    private void setOrderBySpinner() {
        spinner_ranking = (Spinner) findViewById(R.id.spinner_rank);
        lista_rank = new  String[]{"Mais recentes","Mais recomendações","Menor preço","Nome"};

        adapter_rank = new ArrayAdapter<String>(this,R.layout.spinner_item,lista_rank);
        spinner_ranking.setSelection(0);
        spinner_ranking.setAdapter(adapter_rank);

        spinner_ranking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i) {
                    case 0:
                        loadFilteredProductsByDateDesc();
                        break;
                    case 1:
                        loadFilteredProductsByLikesDesc();
                        break;
                    case 2:
                        loadFilteredProductsByPriceAsc();
                        break;
                    case 3:
                        loadFilteredProductsAlphabetically();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

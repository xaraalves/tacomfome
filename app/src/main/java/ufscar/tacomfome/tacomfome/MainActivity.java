package ufscar.tacomfome.tacomfome;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ufscar.tacomfome.tacomfome.fragments.CafesActivity;
import ufscar.tacomfome.tacomfome.fragments.DocesActivity;
import ufscar.tacomfome.tacomfome.fragments.SalgadosActivity;
import ufscar.tacomfome.tacomfome.fragments.TodosActivity;
import ufscar.tacomfome.tacomfome.fragments.VeganosActivity;
import ufscar.tacomfome.tacomfome.interfaces.Observer;
import ufscar.tacomfome.tacomfome.interfaces.Subject;
import ufscar.tacomfome.tacomfome.models.Product;
import ufscar.tacomfome.tacomfome.models.Product1;

public class MainActivity extends AppCompatActivity implements Subject {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private Drawer.Result navigationDrawerLeft;
    private AccountHeader.Result headerNavigationLeft;
    static AccountHeader accountHeader;

    private Spinner spinner_ranking;
    String[] lista_rank;
    ArrayAdapter adapter_rank;

    private List<Observer> observers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        observers = new ArrayList<>();

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

//        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//        database.child("lojas").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot data : dataSnapshot.getChildren()) {
//                    Product1 product1 = data.getValue(Product1.class);
//                    Product product = new Product();
//                    product.setProductName(product1.getProductName());
//                    product.setSellingPlace(product1.getSellingPlace());
//                    product.setPrice(product1.getPrice());
//                    product.setSellerName(product1.getSellerName());
//                    product.setSellerId(product1.getSellerId());
//                    product.setSellingPeriod(product1.getSellingPeriod());
//                    product.setCategorie(product1.getCategorie());
//                    product.setProductId(product1.getProductId());
//                    product.setTimestamp(product1.getTimestamp());
//                    product.setImageStoragePath("comida6.jpg");
//                    product.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
//                    FirebaseDatabase.getInstance().getReference().child("lojas").child(product.getProductId()).setValue(product);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        final Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the spinner to choose the products order
        setOrderBySpinner();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //tabLayout.getTabAt(0).setIcon(R.drawable.);
        tabLayout.getTabAt(1).setIcon(R.drawable.cafe_branco_48);
        tabLayout.getTabAt(2).setIcon(R.drawable.doce_branco_48);
        tabLayout.getTabAt(3).setIcon(R.drawable.hamburguer_branco_48);
        tabLayout.getTabAt(4).setIcon(R.drawable.folha_branco_48);

        // NAVIGATIOn DRAWER
        accountHeader = new AccountHeader()
                .withActivity(this)
                .withCompactStyle(true)
                .withSavedInstance(savedInstanceState)
                .withThreeSmallProfileImages(true)
                .withHeaderBackground(R.drawable.comida6);

        if(user != null) {
            accountHeader.addProfiles(
                    new ProfileDrawerItem().withName(user.getDisplayName()).withIcon(getResources().getDrawable(R.drawable.com_facebook_button_icon_blue)
                    ));
        }
        else {
            accountHeader.addProfiles(
                    new ProfileDrawerItem().withName("Convidado").withIcon(getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_portrait)
                    ));
        }
        headerNavigationLeft =  accountHeader.build();


        navigationDrawerLeft = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowToolbar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.LEFT)
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(2)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(headerNavigationLeft)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        mViewPager.setCurrentItem(position-3);
                        switch(position) {
                            case 0: //add produto
                                loadProductActivityView();
                                break;
                            case 1:
                                if(user != null) {
                                    loadUsersProducts();
                                }
                                else {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Você precisa entrar com o Facebook!")
                                            .setCancelable(false)
                                            .setPositiveButton("Entrar", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    loadLogInView();
                                                }
                                            })
                                            .setNegativeButton("Cancelar", null)
                                            .show();
                                }
                                break;
                        }
                    }
                })
                .build();

        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Adicionar produto").withIcon(getResources().getDrawable(R.drawable.ic_add_item)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Meus produtos"));
        navigationDrawerLeft.addItem(new SectionDrawerItem().withName("Categorias"));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Todos").withIcon(getResources().getDrawable(R.drawable.ic_local_dining_black_36dp)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Cafés").withIcon(getResources().getDrawable(R.drawable.ic_local_cafe_black_36dp)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Doces").withIcon(getResources().getDrawable(R.drawable.doce_36)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Salgados").withIcon(getResources().getDrawable(R.drawable.hamburguer_36)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Veganos").withIcon(getResources().getDrawable(R.drawable.folha_36)));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
        switch (item.getItemId()) {
            case R.id.action_logout:
//                if(user == null) {
//                    new AlertDialog.Builder(MainActivity.this)
//                            .setMessage("Entrar com a conta do Facebook?")
//                            .setCancelable(false)
//                            .setPositiveButton("Entrar", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    loadLogInView();
//                                }
//                            })
//                            .setNegativeButton("Cancelar", null)
//                            .show();
//                }
//                else {
//                    new AlertDialog.Builder(MainActivity.this)
//                            .setMessage("Desassociar conta do Facebook?")
//                            .setCancelable(false)
//                            .setPositiveButton("Desassociar", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    loadLogInView();
//                                }
//                            })
//                            .setNegativeButton("Cancelar", null)
//                            .show();
//                }

                mFirebaseAuth.signOut();
                loadLogInView();
                return true;
            case R.id.action_addItem:
                if(user != null) {
                    loadProductActivityView();
                }
                else {
                    loadLogInView();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void loadProductActivityView() {
        Intent intent = new Intent(this, AddProductActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void loadUsersProducts() {
        Intent intent = new Intent(this, UsersProductsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer o : observers) {
            o.update(spinner_ranking.getSelectedItemPosition());
        }
    }

    @Override
    public int getSpinnerPosition() {
        return spinner_ranking.getSelectedItemPosition();
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

//            navigationDrawerLeft.setSelection(position+3);
            switch (position) {
                case 0:
                    TodosActivity todos = new TodosActivity();
                    observers.add(todos);
                    todos.addSubject(MainActivity.this);
                    return todos;
                case 1:
                    CafesActivity cafes = new CafesActivity();
                    observers.add(cafes);
                    cafes.addSubject(MainActivity.this);
                    return cafes;
                case 2:
                    DocesActivity doces = new DocesActivity();
                    observers.add(doces);
                    doces.addSubject(MainActivity.this);
                    return doces;
                case 3:
                    SalgadosActivity salgados = new SalgadosActivity();
                    observers.add(salgados);
                    salgados.addSubject(MainActivity.this);
                    return salgados;
                case 4:
                    VeganosActivity veganos = new VeganosActivity();
                    observers.add(veganos);
                    veganos.addSubject(MainActivity.this);
                    return veganos;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "TODOS";
//                case 1:
//                    return "CAFÉS";
//                case 2:
//                    return "DOCES";
//                case 3:
//                    return "SALGADOS";
//                case 4:
//                    return "VEGANOS";
                default:
                    return null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(navigationDrawerLeft.isDrawerOpen()) {
            navigationDrawerLeft.closeDrawer();
        }
        else {
            super.onBackPressed();
        }
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
                notifyObservers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

}

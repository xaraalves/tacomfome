package ufscar.tacomfome.tacomfome;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

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

import java.sql.Timestamp;

import ufscar.tacomfome.tacomfome.fragments.CafesActivity;
import ufscar.tacomfome.tacomfome.fragments.DocesActivity;
import ufscar.tacomfome.tacomfome.fragments.SalgadosActivity;
import ufscar.tacomfome.tacomfome.fragments.TodosActivity;
import ufscar.tacomfome.tacomfome.fragments.VeganosActivity;
import ufscar.tacomfome.tacomfome.models.Product;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private Drawer.Result navigationDrawerLeft;
    private AccountHeader.Result headerNavigationLeft;
    static AccountHeader accountHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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
                        mViewPager.setCurrentItem(position-2);
                        switch(position) {
                            case 0: //add produto
                                loadProductActivityView();
                                break;
                            case 8:
                                loadLogInView();
                                break;
                        }
                    }
                })
                .build();

        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Adicionar produto").withIcon(getResources().getDrawable(R.drawable.ic_add_item)));
        //navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Meus produtos"));
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
        Intent intent = new Intent(this, ProductActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
                    return todos;
                case 1:
                    CafesActivity cafes = new CafesActivity();
                    return cafes;
                case 2:
                    DocesActivity doces = new DocesActivity();
                    return doces;
                case 3:
                    SalgadosActivity salgados = new SalgadosActivity();
                    return salgados;
                case 4:
                    VeganosActivity veganos = new VeganosActivity();
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
}

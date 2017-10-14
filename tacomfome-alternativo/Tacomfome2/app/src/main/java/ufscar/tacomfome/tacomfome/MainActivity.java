package ufscar.tacomfome.tacomfome;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ufscar.tacomfome.tacomfome.fragments.CafesActivity;
import ufscar.tacomfome.tacomfome.fragments.DocesActivity;
import ufscar.tacomfome.tacomfome.fragments.SalgadosActivity;
import ufscar.tacomfome.tacomfome.fragments.TodosActivity;
import ufscar.tacomfome.tacomfome.fragments.VegetarianosActivity;
import ufscar.tacomfome.tacomfome.models.Product;

public class MainActivity extends AppCompatActivity {

    DatabaseReference database;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void loadStoreActivityView() {
        Intent intent = new Intent(this, StoreActivity.class);
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
                    VegetarianosActivity vegetarianos = new VegetarianosActivity();
                    return vegetarianos;
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
                case 1:
                    return "CAFÉS";
                case 2:
                    return "DOCES";
                case 3:
                    return "SALGADOS";
                case 4:
                    return "VEGETARIANOS";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        /*if(navigationDrawerLeft.isDrawerOpen()) {
            navigationDrawerLeft.closeDrawer();
        }
        else {*/
            super.onBackPressed();
//        }
    }
}

package com.antoinecampbell.firebase.demo;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeletionListener {

    DatabaseReference database;
    StoreRecyclerViewAdapter adapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

//        mToolbar = (Toolbar) findViewById(R.id.tb_main);
//        mToolbar.setTitle("Tá com fome?");
//        setSupportActionBar(mToolbar);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null) {
                    startActivity(StoreActivity.newInstance(view.getContext(), null));
                }
                else {
                    loadLogInView();
                }

            }
        });

        database = FirebaseDatabase.getInstance().getReference();
        adapter = new StoreRecyclerViewAdapter(Collections.<Store>emptyList());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        final int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = offset;
                }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new StoreItemTouchHelperCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, MainActivity.this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        database.child("lojas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Store> stores = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    stores.add(child.getValue(Store.class));
                }

                adapter.updateList(stores);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void itemRemoved(int position) {
        Store store = adapter.getItem(position);
        adapter.removeItem(position);
        database.child("lojas").child(store.getStoreId()).removeValue();
    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.option_menu, menu); //your file name
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                mFirebaseAuth.signOut();
                loadLogInView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

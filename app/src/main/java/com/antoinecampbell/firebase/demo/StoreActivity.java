package com.antoinecampbell.firebase.demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class StoreActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "NOTE";

    private DatabaseReference database;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private Store store;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private String mUserId;

    public static Intent newInstance(Context context, Store store) {
        Intent intent = new Intent(context, StoreActivity.class);
        if (store != null) {
            intent.putExtra(EXTRA_NOTE, store);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        database = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        mUserId = user.getUid();

        titleTextView = (TextView) findViewById(R.id.note_title);
        descriptionTextView = (TextView) findViewById(R.id.note_description);

        store = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (store != null) {
            titleTextView.setText(store.getTitle());
            descriptionTextView.setText(store.getOwnerId());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (store == null) {
                    store = new Store();
                    store.setStoreId(database.child("lojas").push().getKey());
                    store.setTitle(titleTextView.getText().toString());
                    //store.setOwnerId(descriptionTextView.getText().toString());
                    store.setOwnerId(user.getUid());
                    database.child("lojas").child(store.getStoreId()).setValue(store);
                    final String storeId = store.getStoreId();
                    database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                database.child("users").child(user.getUid()).child("ownedProducts").child(storeId).setValue(true);
                                database.child("users").child(user.getUid()).child("fullName").setValue(user.getDisplayName());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {  }
                    });
                }
                finish();
            }
        });

        FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (store != null) {
                    database.child("users").child(user.getUid()).child("ownedProducts").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                if(data.child(store.getOwnerId()).getKey().equals(user.getUid())) {
                                    new AlertDialog.Builder(StoreActivity.this)
                                            .setMessage("Tem certeza de que deseja excluir este item?")
                                            .setCancelable(false)
                                            .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    database.child("lojas").child(store.getStoreId()).removeValue();
                                                    database.child("users").child(user.getUid()).child("ownedProducts").child(store.getStoreId()).removeValue();
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton("Cancelar", null)
                                            .show();
                                }
                                else {
                                    new AlertDialog.Builder(StoreActivity.this)
                                            .setMessage("Você não tem permissão para excluir este item!")
                                            .setCancelable(false)
                                            .setNegativeButton("Cancelar", null)
                                            .show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {  }
                    });

                }
            }
        });

    }

}

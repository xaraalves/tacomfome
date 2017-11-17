package ufscar.tacomfome.tacomfome;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import ufscar.tacomfome.tacomfome.models.Store;

public class StoreActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "NOTE";

    private DatabaseReference database;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private Store store;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    private Spinner sp;
    String[] lista_categorias;
    ArrayAdapter<String> adapter;
    String categoria;

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
        setContentView(R.layout.add_store);

        database = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        titleTextView = (TextView) findViewById(R.id.note_title);
        //descriptionTextView = (TextView) findViewById(R.id.note_description);
        sp = (Spinner) findViewById(R.id.spinner_categoria);
        lista_categorias = new String[]{"Café","Doces","Salgados","Vegano"};
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,lista_categorias);

        sp.setAdapter(adapter);

        store = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (store != null) {
            titleTextView.setText(store.getTitle());
            //descriptionTextView.setText(store.getOwnerId());
//            descriptionTextView.setText(store.getOwnerId());
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
                    //store.setOwnerId(user.getUid());
                    categoria = sp.getSelectedItem().toString();
                    store.setOwnerId(categoria);
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

        /*Teste Botão Whatsapp*/

//        FloatingActionButton whatsappBtn = (FloatingActionButton) findViewById(R.id.btnWhatsapp);
//        whatsappBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//                whatsappIntent.setType("text/plain");
//                whatsappIntent.setPackage("com.whatsapp");
//                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Testando compartilhamento no Whatsapp");
//                startActivity(whatsappIntent);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        finish();
        goMainScreen();
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

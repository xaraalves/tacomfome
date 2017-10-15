package ufscar.tacomfome.tacomfome;

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

import ufscar.tacomfome.tacomfome.models.Product;

public class ProductActivity extends AppCompatActivity {

    private static final String EXTRA_PRODUCT = "PRODUCT";

    private DatabaseReference database;
    TextView sellerNameTextView;
    TextView productNameTextView;
    TextView sellingPlaceTextView;
    TextView priceTextView;
    private Spinner spinner_categories;
    private Spinner spinner_selling_period;
    private Product product;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    String[] lista_categorias;
    String[] lista_periodos;
    ArrayAdapter<String> adapter_categories, adapter_selling_period;
    String categoria;
    String periodo;

    public static Intent newInstance(Context context, Product product) {
        Intent intent = new Intent(context, ProductActivity.class);
        if (product != null) {
            intent.putExtra(EXTRA_PRODUCT, product);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        database = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        sellerNameTextView = (TextView) findViewById(R.id.product_seller_name);
        productNameTextView = (TextView) findViewById(R.id.product_title);
        sellingPlaceTextView = (TextView) findViewById(R.id.product_selling_place);
        priceTextView = (TextView) findViewById(R.id.product_price);
        spinner_selling_period = (Spinner) findViewById(R.id.product_spinner_selling_period);
        lista_periodos = new String[]{"Manhã","Tarde","Integral","Noturno", "Integral e Noturno"};
        spinner_categories = (Spinner) findViewById(R.id.product_spinner_categories);
        lista_categorias = new String[]{"Café","Doces","Salgados","Vegano"};

        adapter_categories = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,lista_categorias);
        spinner_categories.setAdapter(adapter_categories);

        adapter_selling_period = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,lista_periodos);
        spinner_selling_period.setAdapter(adapter_selling_period);


        product = getIntent().getParcelableExtra(EXTRA_PRODUCT);
        if (product != null) {
            sellerNameTextView.setText(product.getSellerName());
            productNameTextView.setText(product.getProductName());
            sellingPlaceTextView.setText(product.getSellingPlace());
            priceTextView.setText(product.getPrice());
            spinner_categories.setSelection(adapter_categories.getPosition(product.getCategorie()));
            spinner_selling_period.setSelection(adapter_selling_period.getPosition(product.getSellingPeriod()));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product == null) {
                    product = new Product();
                    product.setProductId(database.child("lojas").push().getKey());
                    product.setProductName(productNameTextView.getText().toString());
                    product.setSellingPlace(sellingPlaceTextView.getText().toString());
                    product.setPrice(priceTextView.getText().toString());
                    product.setSellerName(user.getDisplayName());
                    product.setSellerId(user.getUid());
                    periodo = spinner_selling_period.getSelectedItem().toString();
                    product.setSellingPeriod(periodo);
                    categoria = spinner_categories.getSelectedItem().toString();
                    product.setCategorie(categoria);
                    database.child("lojas").child(product.getProductId()).setValue(product);
//                    final String productId = product.getProductId();
//                    if(database.child("users").child(user.getUid()) != null) {
//                        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                for(DataSnapshot data: dataSnapshot.getChildren()){
//                                    database.child("users").child(user.getUid()).child("ownedProducts").child(productId).setValue(true);
//                                    database.child("users").child(user.getUid()).child("fullName").setValue(user.getDisplayName());
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError firebaseError) {  }
//                        });
//                    }
//                    else {
//                        database.child("users").child(user.getUid()).child("ownedProducts").child(productId).setValue(true);
//                        database.child("users").child(user.getUid()).child("fullName").setValue(user.getDisplayName());
//                    }
                }
                finish();
                goMainScreen();
            }
        });

        FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.ic_delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product != null) {
                    /*database.child("users").child(user.getUid()).child("ownedProducts").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                if(data.child(product.getProductId()).getKey().equals(user.getUid())) {
                                    new AlertDialog.Builder(ProductActivity.this)
                                            .setMessage("Tem certeza de que deseja excluir este item?")
                                            .setCancelable(false)
                                            .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    database.child("lojas").child(product.getProductId()).removeValue();
                                                    database.child("users").child(user.getUid()).child("ownedProducts").child(product.getProductId()).removeValue();
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton("Cancelar", null)
                                            .show();
                                }
                                else {
                                    new AlertDialog.Builder(ProductActivity.this)
                                            .setMessage("Você não tem permissão para excluir este item!")
                                            .setCancelable(false)
                                            .setNegativeButton("Cancelar", null)
                                            .show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {  }
                    });*/

                    if(user.getUid() == product.getSellerId()) {
                        new AlertDialog.Builder(ProductActivity.this)
                                .setMessage("Tem certeza de que deseja excluir este item?")
                                .setCancelable(false)
                                .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        database.child("lojas").child(product.getProductId()).removeValue();
                                        finish();
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                    else {
                        new AlertDialog.Builder(ProductActivity.this)
                                .setMessage("Você não tem permissão para excluir este item!")
                                .setCancelable(false)
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }

                }
            }
        });

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

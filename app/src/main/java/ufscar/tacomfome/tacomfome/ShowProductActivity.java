package ufscar.tacomfome.tacomfome;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.NumberFormat;

import ufscar.tacomfome.tacomfome.models.Product;

public class ShowProductActivity extends AppCompatActivity {

    private static final String EXTRA_PRODUCT = "PRODUCT";
    private DatabaseReference database;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    private TextView productNameTextView;
    private TextView sellingPlaceTextView;
    private TextView priceTextView;
    private TextView categorieTextView;
    private TextView periodTextView;
    private TextView sellerNameTextView;
    private TextView descriptionTextView;
    private ImageView imgView;

    private Product product;

    // To add images to the product
    private FirebaseStorage storage;
    private StorageReference storageRef;


    public static Intent newInstance(Context context, Product product) {
        Intent intent = new Intent(context, ShowProductActivity.class);
        if (product != null) {
            intent.putExtra(EXTRA_PRODUCT, product);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        database = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        productNameTextView = (TextView) findViewById(R.id.product_show_title);
        sellerNameTextView = (TextView) findViewById(R.id.product_show_seller_name);
        periodTextView = (TextView) findViewById(R.id.product_show_selling_period);
        sellingPlaceTextView = (TextView) findViewById(R.id.product_show_selling_place);
        categorieTextView = (TextView) findViewById(R.id.product_show_selling_categorie);
        priceTextView = (TextView) findViewById(R.id.product_show_price);
        descriptionTextView = (TextView) findViewById(R.id.product_show_description);
        imgView = (ImageView) findViewById(R.id.product_show_image);

        product = getIntent().getParcelableExtra(EXTRA_PRODUCT);

        if(product != null) {
            productNameTextView.setText(product.getProductName());
            sellerNameTextView.setText(product.getSellerName());
            periodTextView.setText(product.getSellingPeriod());
            sellingPlaceTextView.setText(product.getSellingPlace());
            categorieTextView.setText(product.getCategorie());
            priceTextView.setText(product.getPrice());
            descriptionTextView.setText(product.getProductName());

            if(product.getImageStoragePath() != null && product.getImageStoragePath().equals("null") == false) {
                storageRef.child(product.getImageStoragePath()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imgView.setImageBitmap(bitmap);
                    }
                });
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        finish();
//        goMainScreen();
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

package ufscar.tacomfome.tacomfome;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

public class AddProductActivity extends AppCompatActivity {

    private static final String EXTRA_PRODUCT = "PRODUCT";
    private DatabaseReference database;
    TextView productNameTextView;
    TextView sellingPlaceTextView;
    TextView priceTextView;
    private Spinner spinner_categories;
    private Spinner spinner_selling_period;
    private Product product;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    String[] lista_categorias;
    String[] lista_periodos;
    ArrayAdapter<String> adapter_categories, adapter_selling_period;
    String categoria;
    String periodo;
    EditText price;

    // To add images to the product
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ImageView imgView;
    private String imageStoragePath;


    public static Intent newInstance(Context context, Product product) {
        Intent intent = new Intent(context, AddProductActivity.class);
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

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        imageStoragePath = "null";

        productNameTextView = (TextView) findViewById(R.id.product_title);
        sellingPlaceTextView = (TextView) findViewById(R.id.product_selling_place);
        priceTextView = (TextView) findViewById(R.id.product_price);
        spinner_selling_period = (Spinner) findViewById(R.id.product_spinner_selling_period);
        lista_periodos = new String[]{"Manhã","Tarde","Integral","Noturno", "Integral e Noturno"};
        spinner_categories = (Spinner) findViewById(R.id.product_spinner_categories);
        lista_categorias = new String[]{"Café","Doce","Salgado","Vegano"};

        adapter_categories = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,lista_categorias);
        spinner_categories.setAdapter(adapter_categories);

        adapter_selling_period = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,lista_periodos);
        spinner_selling_period.setAdapter(adapter_selling_period);

        price = (EditText) findViewById(R.id.product_price);
        price.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    price.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    price.setText(formatted);
                    price.setSelection(formatted.length());
                    price.addTextChangedListener(this);
                }
            }
        });


        product = getIntent().getParcelableExtra(EXTRA_PRODUCT);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product == null) {
                    product = new Product();
                    product.setProductId(database.child("lojas").push().getKey());
                }
                product.setProductName(productNameTextView.getText().toString());
                String sellingPlace = sellingPlaceTextView.getText().toString().substring(0,1).toUpperCase() + sellingPlaceTextView.getText().toString().substring(1).toLowerCase();
                product.setSellingPlace(sellingPlace);
                product.setPrice(priceTextView.getText().toString());
                product.setSellerName(user.getDisplayName());
                product.setSellerId(user.getUid());
                periodo = spinner_selling_period.getSelectedItem().toString();
                product.setSellingPeriod(periodo);
                categoria = spinner_categories.getSelectedItem().toString();
                product.setCategorie(categoria);
                product.setTimestamp(timestamp.getTime());
                if(filePath != null) {

//                    InputStream inputStream = null;//You can get an inputStream using any IO API
//                    try {
//                        inputStream = new FileInputStream(filePath.getPath());
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    byte[] bytes;
//                    byte[] buffer = new byte[8192];
//                    int bytesRead;
//                    ByteArrayOutputStream output = new ByteArrayOutputStream();
//                    try {
//                        while ((bytesRead = inputStream.read(buffer)) != -1) {
//                            output.write(buffer, 0, bytesRead);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    bytes = output.toByteArray();
//                    String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
//                    Log.i("LOG", encodedString);

                    //  Directory where the image will be uploaded
                    imageStoragePath = "image-" + product.getProductId();

                    StorageReference childRef = storageRef.child(imageStoragePath);

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddProductActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            //product.setImageStoragePath(imageStoragePath);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProductActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                            product.setImageStoragePath("null");
                            database.child("lojas").child(product.getProductId()).setValue(product);
                        }
                    });
                }
                else {
                    Toast.makeText(AddProductActivity.this, "Selecione uma imagem", Toast.LENGTH_SHORT).show();
                }
                product.setImageStoragePath(imageStoragePath);
                database.child("lojas").child(product.getProductId()).setValue(product);

                finish();
                goMainScreen();
            }
        });

        FloatingActionButton cancel = (FloatingActionButton) findViewById(R.id.ic_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AddProductActivity.this)
                        .setMessage("Deseja sair desta página e não adicionar este item?")
                        .setCancelable(false)
                        .setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onBackPressed();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        Button addImage = (Button) findViewById(R.id.btn_add_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting image to ImageView
                imgView = (ImageView) findViewById(R.id.product_show_image);
                imgView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    // Thanks to 'cesards' from the StackOverflow Forum: https://stackoverflow.com/questions/2789276/android-get-real-path-by-uri-getpath
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        Log.i("LOG", result);
        return result;
    }

}

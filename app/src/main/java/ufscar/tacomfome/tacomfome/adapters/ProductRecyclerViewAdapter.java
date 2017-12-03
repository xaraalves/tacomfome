package ufscar.tacomfome.tacomfome.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.List;

import ufscar.tacomfome.tacomfome.AddProductActivity;
import ufscar.tacomfome.tacomfome.R;
import ufscar.tacomfome.tacomfome.ShowProductActivity;
import ufscar.tacomfome.tacomfome.extras.FirebaseImageLoader;
import ufscar.tacomfome.tacomfome.interfaces.RecyclerViewOnClickListenerHack;
import ufscar.tacomfome.tacomfome.models.Product;

import static android.support.v4.content.ContextCompat.startActivity;
import static com.facebook.FacebookSdk.getApplicationContext;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Gabriel on 10/10/2017.
 */

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

    private static RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private List<Product> products;
//    public RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;


    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        this.mRecyclerViewOnClickListenerHack = r;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public static Product product1;
        TextView sellerNameTextView;
        TextView productNameTextView;
        TextView sellingPlaceTextView;
        TextView priceTextView;
        TextView sellingPeriodTextView;
        TextView categoriesTextView;
        public Product product;
        private DatabaseReference database;
        private FirebaseUser user;
        ImageButton likeButton;
        Button shareBtn;
        private boolean mProcessLike = false;
        TextView numLikesTextView;

        private FirebaseStorage storage;
        private StorageReference storageRef;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            sellerNameTextView = (TextView) itemView.findViewById(R.id.product_seller_name);
            productNameTextView = (TextView) itemView.findViewById(R.id.product_title);
            sellingPlaceTextView = (TextView) itemView.findViewById(R.id.product_selling_place);
            priceTextView = (TextView) itemView.findViewById(R.id.product_price);
            sellingPeriodTextView = (TextView) itemView.findViewById(R.id.product_selling_period);
            categoriesTextView = (TextView) itemView.findViewById(R.id.product_categorie);
            numLikesTextView = (TextView) itemView.findViewById(R.id.numLikesTextView);

            database = FirebaseDatabase.getInstance().getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();

            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();

            likeButton = (ImageButton) itemView.findViewById(R.id.like_btn);
            shareBtn = (Button) itemView.findViewById(R.id.btnShare);
        }

        public void bind(Product product) {
            this.product = product;
            final Product product1 = product;

            sellerNameTextView.setText(product.getSellerName());
            productNameTextView.setText(product.getProductName());
            sellingPlaceTextView.setText(product.getSellingPlace());
            priceTextView.setText(product.getPrice());
            sellingPeriodTextView.setText(product.getSellingPeriod());
            categoriesTextView.setText(product.getCategorie());

            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(user != null && dataSnapshot.child("Likes").child(product1.getProductId()).hasChild(user.getUid())) {
                        likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    }
                    else {
                        likeButton.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mProcessLike = true;

                    database.child("Likes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(mProcessLike) {
                                if(user == null){
                                    Toast toast = Toast.makeText(getApplicationContext(),"loga no face carai",Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.show();
                                }else {
                                    if (dataSnapshot.child(product1.getProductId()).hasChild(user.getUid())) {
                                        decrementNumLikes();
                                        likeButton.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                                        database.child("Likes").child(product1.getProductId()).child(user.getUid()).removeValue();
                                        mProcessLike = false;
                                    } else {
                                        database.child("Likes").child(product1.getProductId()).child(user.getUid()).setValue(user.getDisplayName());
                                        mProcessLike = false;
                                        incrementNumLikes();
                                        likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            });

            numLikesTextView.setText(product.getNumLikes().toString());

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, product1.getSellerName() + " " + product1.getProductName()+ " " + product1.getSellingPlace()+ " " + product1.getPrice()+ " " + product1.getSellingPeriod()+ " " + product1.getCategorie());
                    sendIntent.setType("text/plain");
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(sendIntent);
                }
            });
        }

        public void incrementNumLikes() {
            this.product.incrementNumLikes();
            database.child("lojas").child(product.getProductId()).child("numLikes").setValue(product.getNumLikes());
        }

        public void decrementNumLikes() {
            this.product.decrementNumLikes();
            database.child("lojas").child(product.getProductId()).child("numLikes").setValue(product.getNumLikes());
        }

        @Override
        public void onClick(View view) {
            if(mRecyclerViewOnClickListenerHack != null) {
                mRecyclerViewOnClickListenerHack.OnClickListener(view, getAdapterPosition());
            }

            Context context = view.getContext();
            context.startActivity(ShowProductActivity.newInstance(context, product));
        }
    }

    public ProductRecyclerViewAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);

        return new ProductRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateList(List<Product> stores) {
        // Allow recyclerview animations to complete normally if we already know about data changes
        if (stores.size() != this.products.size() || !this.products.containsAll(stores)) {
            this.products = stores;
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        products.remove(position);
        notifyItemRemoved(position);
    }

    public Product getItem(int position) {
        return products.get(position);
    }

    public void addListItem(Product c, int position){
        products.add(c);
        notifyItemInserted(position);
    }
}

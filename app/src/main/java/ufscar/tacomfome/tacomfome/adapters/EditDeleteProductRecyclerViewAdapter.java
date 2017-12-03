package ufscar.tacomfome.tacomfome.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ufscar.tacomfome.tacomfome.AddProductActivity;
import ufscar.tacomfome.tacomfome.EditProductActivity;
import ufscar.tacomfome.tacomfome.R;
import ufscar.tacomfome.tacomfome.SearchableActivity;
import ufscar.tacomfome.tacomfome.UsersProductsActivity;
import ufscar.tacomfome.tacomfome.interfaces.RecyclerViewOnClickListenerHack;
import ufscar.tacomfome.tacomfome.models.Product;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Gabriel on 10/10/2017.
 */

public class EditDeleteProductRecyclerViewAdapter extends RecyclerView.Adapter<EditDeleteProductRecyclerViewAdapter.ViewHolder> {

    private static RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private List<Product> products;

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        this.mRecyclerViewOnClickListenerHack = r;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView sellerNameTextView;
        TextView productNameTextView;
        TextView sellingPlaceTextView;
        TextView priceTextView;
        TextView sellingPeriodTextView;
        TextView categoriesTextView;
        private Product product;
        private DatabaseReference database;
        private FirebaseUser user;
        ImageButton likeButton;
        ImageButton editButton;
        ImageButton deleteButton;
        private boolean mProcessLike = false;
        TextView numLikesTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            sellerNameTextView = (TextView) itemView.findViewById(R.id.product_seller_name);
            productNameTextView = (TextView) itemView.findViewById(R.id.product_title);
            sellingPlaceTextView = (TextView) itemView.findViewById(R.id.product_selling_place);
            priceTextView = (TextView) itemView.findViewById(R.id.product_price);
            sellingPeriodTextView = (TextView) itemView.findViewById(R.id.product_selling_period);
            categoriesTextView = (TextView) itemView.findViewById(R.id.product_categorie);
            numLikesTextView = (TextView) itemView.findViewById(R.id.numLikesTextView);

            database = FirebaseDatabase.getInstance().getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            likeButton = (ImageButton) itemView.findViewById(R.id.like_btn);
            editButton = (ImageButton) itemView.findViewById(R.id.btnEditItem);
            deleteButton = (ImageButton) itemView.findViewById(R.id.btnDeleteItem);
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

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(user != null) {

                    }
                    Context context = view.getContext();
                    context.startActivity(AddProductActivity.newInstance(context, product1));
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (product1 != null) {
                        if(user.getUid().equals(product1.getSellerId())) {
                            new AlertDialog.Builder(view.getContext())
                                    .setMessage("Tem certeza de que deseja excluir este item?")
                                    .setCancelable(false)
                                    .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            database.child("lojas").child(product1.getProductId()).removeValue();
                                        }
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        }
                        else {
                            new AlertDialog.Builder(getApplicationContext())
                                    .setMessage("Você não tem permissão para excluir este item!")
                                    .setCancelable(false)
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        }

                    }
                }
            });

            numLikesTextView.setText(product.getNumLikes().toString());
        }

        public void incrementNumLikes() {
            this.product.incrementNumLikes();
            database.child("lojas").child(product.getProductId()).child("numLikes").setValue(product.getNumLikes());
        }

        public void decrementNumLikes() {
            this.product.decrementNumLikes();
            database.child("lojas").child(product.getProductId()).child("numLikes").setValue(product.getNumLikes());
        }
    }

    public EditDeleteProductRecyclerViewAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_edit, parent, false);

        return new EditDeleteProductRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void  updateList(List<Product> stores) {
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

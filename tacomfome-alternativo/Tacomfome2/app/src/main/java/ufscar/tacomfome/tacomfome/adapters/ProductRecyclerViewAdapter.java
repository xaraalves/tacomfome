package ufscar.tacomfome.tacomfome.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ufscar.tacomfome.tacomfome.ProductActivity;
import ufscar.tacomfome.tacomfome.R;
import ufscar.tacomfome.tacomfome.StoreActivity;
import ufscar.tacomfome.tacomfome.models.Product;
import ufscar.tacomfome.tacomfome.models.Store;

/**
 * Created by Gabriel on 10/10/2017.
 */

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

    private List<Product> products;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView sellerNameTextView;
        TextView productNameTextView;
        TextView sellingPlaceTextView;
        TextView priceTextView;
        TextView sellingPeriodTextView;
        TextView categorieTextView;
        private Product product;

        public ViewHolder(View itemView) {
            super(itemView);
            sellerNameTextView = (TextView) itemView.findViewById(R.id.product_seller_name);
            productNameTextView= (TextView) itemView.findViewById(R.id.product_title);
            sellingPlaceTextView = (TextView) itemView.findViewById(R.id.product_selling_place);
            priceTextView = (TextView) itemView.findViewById(R.id.product_price);
            sellingPeriodTextView = (TextView) itemView.findViewById(R.id.product_selling_period);
            categorieTextView = (TextView) itemView.findViewById(R.id.product_categorie);
            itemView.setOnClickListener(this);
        }

        public void bind(Product product) {
            this.product = product;
            sellerNameTextView.setText(product.getSellerName());
            productNameTextView.setText(product.getProductName());
            sellingPlaceTextView.setText(product.getSellingPlace());
            priceTextView.setText(product.getPrice());
            sellingPeriodTextView.setText(product.getSellingPeriod());
            categorieTextView.setText(product.getCategorie());

        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            context.startActivity(ProductActivity.newInstance(context, product));
        }
    }

    public ProductRecyclerViewAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);

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
}

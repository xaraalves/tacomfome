package ufscar.tacomfome.tacomfome.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ufscar.tacomfome.tacomfome.R;
import ufscar.tacomfome.tacomfome.models.Store;
import ufscar.tacomfome.tacomfome.StoreActivity;

/**
 * Created by Gabriel on 10/10/2017.
 */

public class StoreRecyclerViewAdapter extends RecyclerView.Adapter<StoreRecyclerViewAdapter.ViewHolder> {

    private List<Store> stores;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleTextView;
        TextView descriptionTextView;
        private Store store;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.note_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.note_description);
            itemView.setOnClickListener(this);
        }

        public void bind(Store store) {
            this.store = store;
            titleTextView.setText(store.getTitle());
            descriptionTextView.setText(store.getOwnerId());
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            context.startActivity(StoreActivity.newInstance(context, store));
        }
    }

    public StoreRecyclerViewAdapter(List<Store> stores) {
        this.stores = stores;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);

        return new StoreRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(stores.get(position));
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public void updateList(List<Store> stores) {
        // Allow recyclerview animations to complete normally if we already know about data changes
        if (stores.size() != this.stores.size() || !this.stores.containsAll(stores)) {
            this.stores = stores;
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        stores.remove(position);
        notifyItemRemoved(position);
    }

    public Store getItem(int position) {
        return stores.get(position);
    }
}

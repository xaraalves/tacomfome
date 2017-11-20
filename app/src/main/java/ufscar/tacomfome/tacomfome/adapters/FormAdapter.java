package ufscar.tacomfome.tacomfome.adapters;

 import android.content.Context;
 import android.support.v4.app.FragmentActivity;
 import android.support.v7.widget.RecyclerView;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.TextView;
 import java.util.List;

 import ufscar.tacomfome.tacomfome.R;
 import ufscar.tacomfome.tacomfome.models.Store;

public class FormAdapter extends RecyclerView.Adapter <FormAdapter.ViewHolder> {
    private List<Store> stores;

    private String formtype;
    String type;
    Context context;

    public FormAdapter(List<Store> dataset, FragmentActivity activity) {
        this.stores =dataset;
        this.context=activity;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new FormAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(FormAdapter.ViewHolder holder, int position) {
        holder.tvTitle.setText(stores.get(position).getTitle());
        holder.tvDescription.setText(stores.get(position).getOwnerId());

    }

    @Override
    public int getItemCount() {
        return stores.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvDescription;

        public ViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.product_title);
            //tvDescription = (TextView) view.findViewById(R.id.product_description);

            //view.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent(context,addforms.class);
//            intent.putExtra("type",formtype);
//            intent.putExtra("input",stores.get(getLayoutPosition()));
//            intent.putExtra("key",stores.get(getLayoutPosition()).getStoreId());
//            context.startActivity(intent);
//        }
    }
}
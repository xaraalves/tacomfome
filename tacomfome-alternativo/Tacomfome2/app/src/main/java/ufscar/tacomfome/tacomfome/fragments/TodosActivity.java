package ufscar.tacomfome.tacomfome.fragments;

import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ufscar.tacomfome.tacomfome.R;
import ufscar.tacomfome.tacomfome.adapters.StoreRecyclerViewAdapter;
import ufscar.tacomfome.tacomfome.models.Store;
import ufscar.tacomfome.tacomfome.adapters.FormAdapter;

/**
 * Created by Gabriel on 13/10/2017.
 */

public class TodosActivity extends Fragment {

    private ProgressDialog progressDialog;
    private RecyclerView mRecyclerView;
    private View mView;
    private List<Store> stores = new ArrayList<>();
    private List<String> mDatakey = new ArrayList<>();
    //private FormAdapter adapter;
    private StoreRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todos, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mView = view;
        init();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Syncing...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        loadData();
    }

    private void init() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.todos_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StoreRecyclerViewAdapter(stores);
        adapter = new StoreRecyclerViewAdapter(stores);
        mRecyclerView.setAdapter(adapter);
    }

    private void loadData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("lojas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stores.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    stores.add(data.getValue(Store.class));
                    mDatakey.add(data.getKey().toString());
                }
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

package ufscar.tacomfome.tacomfome.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Prof-Mohamed Atef on 11/1/2016.
 */
public class CustomSpinnerAdapter extends BaseAdapter {

    private final Context mContext;
    private ArrayList<String> SpinnerFeedItemList;

    public CustomSpinnerAdapter(Context mContext, ArrayList<String> spinnerFeedItemList) {
        this.mContext= mContext;
        SpinnerFeedItemList = spinnerFeedItemList;
    }

    @Override
    public int getCount() {
        return SpinnerFeedItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return SpinnerFeedItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(mContext);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(SpinnerFeedItemList.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }
    TextView txt;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        txt = new TextView(mContext);
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(10, 10, 10, 10);
        txt.setTextSize(16);
        txt.setText(SpinnerFeedItemList.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
//        txt.setBackgroundResource(R.color.yellow);
        return  txt;
    }
}
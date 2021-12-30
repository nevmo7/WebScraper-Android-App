package com.example.nevermore.webscraper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Never More on 12/31/2018.
 */

public class ClanListAdapter extends ArrayAdapter<Clan> {

    private static final String TAG = "ClanListAdapter";

    private Context mContext;
    private ArrayList<Clan> list;

    int mResource;

    public ClanListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Clan> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        list = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get clan information
        String name = getItem(position).getName();
        String cm = getItem(position).getCm();
        String no = getItem(position).getNo();
        String reps = getItem(position).getReps();
        String gap = getItem(position).getGap();

        //create the clan object with the information
        Clan clan = new Clan(name, cm, no, reps, gap);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.textView1);
        TextView tvCm = (TextView) convertView.findViewById(R.id.textView2);
        TextView tvNo = (TextView) convertView.findViewById(R.id.textView3);
        TextView tvReps = (TextView) convertView.findViewById(R.id.textView4);
        TextView tvGap = (TextView) convertView.findViewById(R.id.textView5);
        TextView tvRank = (TextView) convertView.findViewById(R.id.rank);

        if (position == 0){
            convertView.setBackgroundResource(R.drawable.listview_bottom_border);
            tvName.setText(name);
            tvCm.setText(cm);
            tvNo.setText(no);
            tvReps.setText(reps);
            tvGap.setText(gap);
            tvRank.setText("");
            tvName.setTextColor(Color.WHITE);
            tvCm.setTextColor(Color.WHITE);
            tvNo.setTextColor(Color.WHITE);
            tvReps.setTextColor(Color.WHITE);
            tvGap.setTextColor(Color.WHITE);
        }
        if (position > 0){
            tvName.setText(name);
            tvName.setTypeface(null, Typeface.BOLD);
            tvCm.setText(cm);
            tvNo.setText(no);
            tvReps.setText(reps);
            tvGap.setText(gap);
            String rank = Integer.toString(position);
            tvRank.setText(rank);
        }

        return convertView;

    }

    public void refill(List<Clan> newClanList) {
        list.clear();
        list.addAll(newClanList);
        notifyDataSetChanged();
    }
}

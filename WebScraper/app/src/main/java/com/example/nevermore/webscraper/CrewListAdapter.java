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

public class CrewListAdapter extends ArrayAdapter<Crew> {

    private static final String TAG = "ClanListAdapter";

    private Context mContext;
    private ArrayList<Crew> list;

    int mResource;

    public CrewListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Crew> objects) {
        super(context,resource,objects);
        mContext = context;
        mResource = resource;
        list = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get crew information
        String name = getItem(position).getName();
        String cm = getItem(position).getCm();
        String no = getItem(position).getNo();
        String damage = getItem(position).getDamage();
        String gap = getItem(position).getGap();

        //create the crew object with the information
        Crew crew = new Crew(name, cm, no, damage, gap);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.textView1);
        TextView tvCm = (TextView) convertView.findViewById(R.id.textView2);
        TextView tvNo = (TextView) convertView.findViewById(R.id.textView3);
        TextView tvDamage = (TextView) convertView.findViewById(R.id.textView4);
        TextView tvGap = (TextView) convertView.findViewById(R.id.textView5);
        TextView tvRank = (TextView) convertView.findViewById(R.id.rank);

        if (position == 0){
            convertView.setBackgroundResource(R.drawable.listview_bottom_border);
            tvName.setText(name);
            tvCm.setText(cm);
            tvNo.setText(no);
            tvDamage.setText(damage);
            tvGap.setText(gap);
            tvRank.setText("");
            tvName.setTextColor(Color.WHITE);
            tvCm.setTextColor(Color.WHITE);
            tvNo.setTextColor(Color.WHITE);
            tvDamage.setTextColor(Color.WHITE);
            tvGap.setTextColor(Color.WHITE);
        }
        if (position > 0){
            tvName.setText(name);
            tvName.setTypeface(null, Typeface.BOLD);
            tvCm.setText(cm);
            tvNo.setText(no);
            tvDamage.setText(damage);
            tvGap.setText(gap);
            String rank = Integer.toString(position);
            tvRank.setText(rank);
        }

        return convertView;

    }

    public void refill(List<Crew> newCrewList) {
        list.clear();
        list.addAll(newCrewList);
        notifyDataSetChanged();
    }
}

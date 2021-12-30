package com.example.nevermore.webscraper;

import android.content.Context;
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
 * Created by mstha on 2/28/2019.
 */

public class SuggestionAdapter extends ArrayAdapter<Suggestion>{
    private static final String TAG = "SuggestionAdapter";
    private Context mContext;
    int mResource;

    public SuggestionAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Suggestion> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String id = getItem(position).getId();

        Suggestion suggestion = new Suggestion(name, id);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.suggName);
        TextView tvId = (TextView) convertView.findViewById(R.id.suggId);

        tvName.setText(name);
        tvId.setText(id);

        return convertView;
    }
}

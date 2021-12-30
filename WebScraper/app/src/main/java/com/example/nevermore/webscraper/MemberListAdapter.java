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

/**
 * Created by Never More on 1/13/2019.
 */

public class MemberListAdapter extends ArrayAdapter<Member>{
    private Context mContext;
    private ArrayList<Member> list;
    int mResource;

    private static class ViewHolder {
        TextView tvMemberName,tvMemberLevel,tvMemberReps;
    }

    public MemberListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Member> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String memberName = getItem(position).getName();
        String memberLevel = getItem(position).getLevel();
        String memberReput = Integer.toString(getItem(position).getReputation());
        int memberReputation = getItem(position).getReputation();

        Member member = new Member(memberName,memberLevel,memberReputation);

        final View result;

        //ViewHolder object
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);
            holder= new ViewHolder();

            holder.tvMemberName = convertView.findViewById(R.id.memberName);
            holder.tvMemberLevel = convertView.findViewById(R.id.memberLevel);
            holder.tvMemberReps = convertView.findViewById(R.id.memberReps);

            memberReput = Integer.toString(memberReputation);

            result = convertView;
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.tvMemberName.setText(memberName);
        holder.tvMemberLevel.setText(memberLevel);
        holder.tvMemberReps.setText(memberReput);

        return convertView;
    }
}

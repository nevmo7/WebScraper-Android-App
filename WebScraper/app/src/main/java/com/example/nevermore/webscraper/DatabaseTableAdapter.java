package com.example.nevermore.webscraper;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by mstha on 2/8/2019.
 */

public class DatabaseTableAdapter extends RecyclerView.Adapter<DatabaseTableAdapter.ViewHolder> implements Filterable{

    private static final String TAG = "DatabaseTableAdapter";

    private Context mContext;
    private ArrayList<DataTable> dataTables = new ArrayList<>();
    private ArrayList<DataTable> dataTablesFull;
    private DatabaseHelper dbHelper;
    private CoordinatorLayout coordinatorLayout;

    public DatabaseTableAdapter(Context mContext, ArrayList<DataTable> dataTables, DatabaseHelper dbHelper, CoordinatorLayout coordinatorLayout) {
        this.mContext = mContext;
        this.dataTables = dataTables;
        dataTablesFull = new ArrayList<>(dataTables);
        this.dbHelper = dbHelper;
        this.coordinatorLayout = coordinatorLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.db_table_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final DataTable dataTable = dataTables.get(position);

        holder.clcrname.setText(dataTable.getClcrName());
        holder.clcr.setText(dataTable.getClcr());
        holder.dateCreated.setText("Date created: "+dataTable.getDateCreated());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SavedMemberActivity.class);
                intent.putExtra("db_table_name", dataTable.getTableName());
                intent.putExtra("clan_name", dataTable.getClcrName());
                intent.putExtra("date_created", dataTable.getDateCreated());
                mContext.startActivity(intent);
            }
        });
        
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteData(dataTable.getTableName());
                int result = dbHelper.deleteLogData(dataTable.getTableName());
                dataTables.remove(position);
                notifyDataSetChanged();
                if (result > 0){
                   showSnackbar("Table deleted");
                }else{
                    showSnackbar("Failed to delete table");
                }

            }
        });
    }

    private void showSnackbar(String message){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public int getItemCount() {
        return dataTables.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView clcrname,clcr,dateCreated;
        ImageView deleteBtn;
        RelativeLayout parent, delbtnLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            clcrname = itemView.findViewById(R.id.clcrName);
            clcr = itemView.findViewById(R.id.clcr);
            dateCreated = itemView.findViewById(R.id.dateCreated);
            parent = itemView.findViewById(R.id.parent_layout);
            deleteBtn = itemView.findViewById(R.id.deleteTableBtn);
        }
    }

    @Override
    public Filter getFilter() {
        return dataFilter;
    }

    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<DataTable> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                filteredList.addAll(dataTablesFull);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (DataTable data : dataTablesFull){
                    if (data.getClcrName().toLowerCase().contains(filterPattern)){
                        filteredList.add(data);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            dataTables.clear();
            dataTables.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };
}

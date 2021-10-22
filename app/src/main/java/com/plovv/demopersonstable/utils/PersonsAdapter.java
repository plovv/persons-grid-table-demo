package com.plovv.demopersonstable.utils;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.plovv.demopersonstable.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.ViewHolder> {

    public interface IOnItemClickHandler {
        void onItemClick(ArrayList<String> row);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout;
        private IOnItemClickHandler clickHandler;

        public ViewHolder(LinearLayout itemLayout, IOnItemClickHandler clickHandler) {
            super(itemLayout);

            this.layout = itemLayout;
            this.clickHandler = clickHandler;
        }

        public void bindRowData(ArrayList<String> data) {
            for(int i = 0; i < layout.getChildCount(); i++){
                TextView textView = (TextView)layout.getChildAt(i);
                textView.setText(data.get(i));
            }

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickHandler != null) {
                        clickHandler.onItemClick(data);
                    }
                }
            });
        }
    }

    private IOnItemClickHandler rowClickHandler;
    private ArrayList<ArrayList<String>> dataRows;
    private ArrayList<String> columns;
    private HashMap<String, Integer> columnWidths;
    private int rowHeight;

    public PersonsAdapter(ArrayList<ArrayList<String>> dataRows, ArrayList<String> columns, HashMap<String, Integer> columnWidths, int rowHeight, IOnItemClickHandler rowClickHandler) {
        this.dataRows = new ArrayList<>(dataRows);
        this.columns = new ArrayList<>(columns);
        this.columnWidths = new HashMap<>(columnWidths);
        this.rowHeight = rowHeight;
        this.rowClickHandler = rowClickHandler;
    }

    private int getColumnsCount() {
        if (columns == null) {
            return  0;
        }

        return columns.size();
    }

    public void updateDataRows(ArrayList<ArrayList<String>> dataRows){
        this.dataRows = new ArrayList<>(dataRows);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        itemLayout.setMinimumHeight(rowHeight);

        // add the views for each row, dynamically
        for(int i = 0; i < getColumnsCount(); i++){
            String currColumn = columns.get(i);
            TextView txtField = (TextView)LayoutInflater.from(parent.getContext()).inflate(R.layout.list_column, itemLayout, false);

            // add text view
            txtField.setWidth(columnWidths.get(currColumn));
            txtField.setSingleLine(true);
            txtField.setEllipsize(TextUtils.TruncateAt.END);

            itemLayout.addView(txtField);
        }

        return new ViewHolder(itemLayout, rowClickHandler);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindRowData(dataRows.get(position));
    }

    @Override
    public int getItemCount() {
        return dataRows == null ? 0 : dataRows.size();
    }

}

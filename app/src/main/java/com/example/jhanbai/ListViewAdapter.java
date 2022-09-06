package com.example.jhanbai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;

public class ListViewAdapter extends ArrayAdapter<ListData> {

    private LayoutInflater layoutInflater;

    private ListData data;

    private  int pos;


    static class ViewHolder {
        TextView idText;
        TextView col_02;
        TextView col_03;
        TextView col_04;
        TextView col_05;
        TextView col_06;
        TextView col_07;
        TextView col_08;
        TextView col_09;
        TextView col_10;
        Button deleteButton;
    }



    public ListViewAdapter(Context context, int resource, List<ListData> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView,final ViewGroup parent) {
        data = (ListData)getItem(position);

        pos = position;

        ViewHolder holder;

        if(null == convertView) {
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        holder = new ViewHolder();

        holder.deleteButton = convertView.findViewById(R.id.DeleteButton);


        holder.idText = (TextView) convertView.findViewById(R.id.id);
        holder.col_02 = (TextView) convertView.findViewById(R.id.col_02);
        holder.col_03 = (TextView) convertView.findViewById(R.id.col_03);
        holder.col_04 = (TextView) convertView.findViewById(R.id.col_04);
        holder.col_05 = (TextView) convertView.findViewById(R.id.col_05);
        holder.col_06 = (TextView) convertView.findViewById(R.id.col_06);
        holder.col_07 = (TextView) convertView.findViewById(R.id.col_07);
        holder.col_08 = (TextView) convertView.findViewById(R.id.col_08);
        holder.col_09 = (TextView) convertView.findViewById(R.id.col_09);
        holder.col_10 = (TextView) convertView.findViewById(R.id.col_10);

        //------------ ListData.java  ゲッター ----------------
        holder.idText.setText(data.getId());
        holder.col_02.setText(data.getCol_02());
        holder.col_03.setText(data.getCol_03());
        holder.col_04.setText(data.getCol_04());
        holder.col_05.setText(data.getCol_05());
        holder.col_06.setText(data.getCol_06());
        holder.col_07.setText(data.getCol_07());
        holder.col_08.setText(data.getCol_08());
        holder.col_09.setText(data.getCol_09());
        holder.col_10.setText(data.getCol_10());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, R.id.DeleteButton);

            }
        });


        return convertView;

    }

    public int remove(int pos) {
            return pos;
    }


    }



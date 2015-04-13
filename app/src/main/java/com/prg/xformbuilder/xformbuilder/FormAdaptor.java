package com.prg.xformbuilder.xformbuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class FormAdaptor extends ArrayAdapter<FormList> {
    Context context;
    int layoutResourceId;

    FormList formLists[] = null;

    public FormAdaptor(Context context, int resource, FormList[] forms) {
        super(context, resource, forms);
        this.context = context;
        this.layoutResourceId = resource;
        this.formLists = forms;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        formViewHolder holder = null;


        if(row == null){

            LayoutInflater inflater = LayoutInflater.from(context);


            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new formViewHolder();

            holder.img = (ImageView) row.findViewById(R.id.formImage);
            holder.baslik = (TextView) row.findViewById(R.id.formTitle);
            holder.detay = (TextView) row.findViewById(R.id.username);
holder.formID = (TextView) row.findViewById(R.id.frmId);

            row.setTag(holder);
        }
        else{
            holder = (formViewHolder) row.getTag();
        }

        FormList fInfo = formLists[position];
        holder.img.setImageResource(fInfo.getFormImage());
        holder.baslik.setText(fInfo.getFormTitle());
        holder.detay.setText(fInfo.getUserName());
        holder.formID.setText(String.valueOf(fInfo.getFormId()));

        return row;
    }


    static class formViewHolder{
        ImageView img;
        TextView baslik;
        TextView detay;
        TextView formID;
    }
}

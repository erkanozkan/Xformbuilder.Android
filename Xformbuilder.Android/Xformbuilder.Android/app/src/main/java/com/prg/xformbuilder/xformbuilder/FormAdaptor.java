package com.prg.xformbuilder.xformbuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Profesor-PC on 7.4.2015.
 */
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
        bankaViewHolder holder = null;


        if(row == null){

            LayoutInflater inflater = LayoutInflater.from(context);


            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new bankaViewHolder();

            holder.img = (ImageView) row.findViewById(R.id.imageView1);
            holder.baslik = (TextView) row.findViewById(R.id.basligi);
            holder.detay = (TextView) row.findViewById(R.id.telefon);

            row.setTag(holder);
        }
        else{
            holder = (bankaViewHolder) row.getTag();
        }

        FormList bBilgi = formLists[position];
        holder.img.setImageResource(bBilgi.getFormImage());
        holder.baslik.setText(bBilgi.getFormTitle());
        holder.detay.setText(bBilgi.getUserName());
        return row;
    }


    static class bankaViewHolder{
        ImageView img;
        TextView baslik;
        TextView detay;
    }
}

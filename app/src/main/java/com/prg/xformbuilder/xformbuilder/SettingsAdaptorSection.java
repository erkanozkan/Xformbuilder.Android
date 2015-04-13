package com.prg.xformbuilder.xformbuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;



public class SettingsAdaptorSection extends ArrayAdapter<SettingsModel> {

    private final Context context;
    private final ArrayList<SettingsModel> modelsArrayList;

    public SettingsAdaptorSection(Context context, ArrayList<SettingsModel> modelsArrayList) {

        super(context, R.layout.example_settings_custom_row, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 2. Get rowView from inflater

        View rowView = null;
        if(!modelsArrayList.get(position).isGroupHeader()){
            rowView = inflater.inflate(R.layout.example_settings_custom_row, parent, false);

            // 3. Get icon,title & counter views from the rowView
           // ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon); resim eklemek istersek
            TextView titleView = (TextView) rowView.findViewById(R.id.item_title);
           // TextView counterView = (TextView) rowView.findViewById(R.id.item_counter);  sayaç ekleme

            // 4. Set the text for textView
          //  imgView.setImageResource(modelsArrayList.get(position).getIcon());
            titleView.setText(modelsArrayList.get(position).getTitle());
         //   counterView.setText(modelsArrayList.get(position).getCounter()); sayaç ekleme
        }
        else{
            rowView = inflater.inflate(R.layout.settings_group_header, parent, false);
            TextView titleView = (TextView) rowView.findViewById(R.id.header);
            titleView.setText(modelsArrayList.get(position).getTitle());
        }

        // 5. retrn rowView
        return rowView;
    }


}


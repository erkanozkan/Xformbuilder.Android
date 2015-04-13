package com.prg.xformbuilder.xformbuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;


public class SettingsAdaptor extends ArrayAdapter<String> {


    public SettingsAdaptor(Context context, int resource, String[] settingItem) {
        super(context, R.layout.settings_custom_row, settingItem);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
LayoutInflater layoutInflater  = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.settings_custom_row,parent,false);

        String singleRow = getItem(position);
        TextView rowText = (TextView)customView.findViewById(R.id.textView_settingRow);
        rowText.setText(singleRow);



        return  customView;
    }



}

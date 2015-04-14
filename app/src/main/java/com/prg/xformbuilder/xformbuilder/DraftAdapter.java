package com.prg.xformbuilder.xformbuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Profesor-PC on 14.4.2015.
 */

public class DraftAdapter extends ArrayAdapter<DraftList> {
    Context context;
    int layoutResourceId;

    DraftList draftLists[] = null;

    public DraftAdapter(Context context, int resource, DraftList[] drafts) {
        super(context, resource, drafts);
        this.context = context;
        this.layoutResourceId = resource;
        this.draftLists = drafts;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        draftViewHolder holder = null;


        if(row == null){

            LayoutInflater inflater = LayoutInflater.from(context);

            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new draftViewHolder();

            holder.img = (ImageView) row.findViewById(R.id.DraftImage);
            holder.date = (TextView) row.findViewById(R.id.draftDate);
            holder.formId = (TextView) row.findViewById(R.id.frmId);
            holder.draftId = (TextView) row.findViewById(R.id.draftId);

            row.setTag(holder);
        }
        else{
            holder = (draftViewHolder) row.getTag();
        }

        DraftList fInfo = draftLists[position];
        holder.img.setImageResource(fInfo.getDraftImage());
        holder.date.setText(fInfo.getDraftDate());
        holder.formId.setText(fInfo.getFormId());
        holder.draftId.setText(String.valueOf(fInfo.getDraftId()));

        return row;
    }


    static class draftViewHolder{
        ImageView img;
        TextView date;
        TextView formId;
        TextView draftId;
    }
}

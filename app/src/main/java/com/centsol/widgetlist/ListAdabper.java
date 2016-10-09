package com.centsol.widgetlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Project Har Zindagi
 * Created by Ali on 10/9/2016.
 */

public class ListAdabper extends BaseAdapter {

    Context mContext;
    private ArrayList<WidgetModel> mDataset;

    public ListAdabper(Context c,ArrayList<WidgetModel> myDataset) {
        mDataset = myDataset;
        mContext = c;

    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataset.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (view == null) {

            view = inflater.inflate(R.layout.widgets_list_row_view, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            holder.app_image.setImageDrawable(mDataset.get(position).icon);
            holder.mAppName.setText(mDataset.get(position).appName);

            setView(holder,position);
            view.setTag(holder);
        }else{
            ViewHolder holder = (ViewHolder) view.getTag();


            holder.app_image.setImageDrawable(mDataset.get(position).icon);
            holder.mAppName.setText(mDataset.get(position).appName);
            ((LinearLayout) holder.scrollLayout.findViewById(R.id.widgets_cell_list)).removeAllViews();
            setView(holder,position);
        }
        return view;
    }
public  void setView(ViewHolder holder,int position){
    for (int i = 0; i < mDataset.get(position).widgetItems.size(); i++) {
        View childView = LayoutInflater.from(mContext)
                .inflate(R.layout.widget_cell, holder.scrollLayout, false);

        ((LinearLayout) holder.scrollLayout.findViewById(R.id.widgets_cell_list)).addView(childView);
    }


    for (int i = 0; i < mDataset.get(position).widgetItems.size(); i++) {

        if (mDataset.get(position).widgetItems.get(i).image != null) {

            ((ImageView) holder.scrollLayout.getChildAt(i).findViewById(R.id.widget_preview)).setImageDrawable(mDataset.get(position).widgetItems.get(i).image);
        } else {

            ((ImageView) holder.scrollLayout.getChildAt(i).findViewById(R.id.widget_preview)).setImageDrawable(mDataset.get(position).icon);
        }

        ((TextView) holder.scrollLayout.getChildAt(i).findViewById(R.id.widget_name)).setText(mDataset.get(position).widgetItems.get(i).lable);

    }
}
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView mAppName;
        public ImageView app_image;

        public LinearLayout scrollLayout;

        public ViewHolder(View v) {

            mAppName = (TextView) v.findViewById(R.id.section);
            app_image = (ImageView) v.findViewById(R.id.app_image);
            scrollLayout = (LinearLayout) v.findViewById(R.id.widgets_cell_list);

        }
    }
}

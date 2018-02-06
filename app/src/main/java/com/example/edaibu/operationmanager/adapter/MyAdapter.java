package com.example.edaibu.operationmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.edaibu.operationmanager.R;

import java.util.List;

/**
 * Created by ${gyj} on 2017/9/22.
 */

public class MyAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> list;
    public MyAdapter(Context context, List<String> list)
    {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view==null){
            viewHolder = new ViewHolder();
           view = LayoutInflater.from(context).inflate(R.layout.item, null);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_name.setText(list.get(i));
        return view;
    }
   private class ViewHolder {
       private TextView tv_name;
    }
}

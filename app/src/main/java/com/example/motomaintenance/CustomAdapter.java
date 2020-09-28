package com.example.motomaintenance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

class CustomAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int resourcedId;
    private String[] model;
    private String[] manufacturer;
    private String[] now_odo;

    static class ViewHolder {
        TextView mdl;
        TextView mnf;
        TextView info;
        TextView now_odo;
        RelativeLayout maclist;
    }

    CustomAdapter(Context context, int resourcedId, String[] model, String[] manufacturer, String[] now_odo) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resourcedId = resourcedId;
        this.model = model;
        this.manufacturer = manufacturer;
        this.now_odo = now_odo;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(resourcedId, parent, false);

            holder = new ViewHolder();
            holder.mdl = convertView.findViewById(R.id.model);
            holder.mnf = convertView.findViewById(R.id.manufacturer);
            holder.info = convertView.findViewById(R.id.info);
            holder.maclist = convertView.findViewById(R.id.machine_list);
            holder.now_odo = convertView.findViewById(R.id.now_odo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String nowodo2 =("");
        if(!(now_odo[position]).equals(" ")) {
            int odoi = Integer.parseInt(now_odo[position]);
            nowodo2 = ("現在の走行距離 " + (String.format("%,d", odoi) + " Km"));
        }

        holder.mdl.setText(model[position]);
        holder.mnf.setText(manufacturer[position]);
        holder.now_odo.setText(nowodo2);
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, R.id.info);
            }
        });

        holder.maclist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, R.id.machine_list);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return model.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
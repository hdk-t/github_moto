package com.htapp.Moto_Maintenance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

class HistoryAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int resourcedId;
    private String[] day;
    private String[] odo;
    private String[] cnt;

    static class ViewHolder {
        TextView dy;
        TextView od;
        TextView ct;
        RelativeLayout rl1;
    }

    HistoryAdapter(Context context, int resourcedId, String[] day, String[] odo, String[] cnt) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resourcedId = resourcedId;
        this.day = day;
        this.odo = odo;
        this.cnt = cnt;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(resourcedId, parent, false);

            holder = new ViewHolder();
            holder.dy = convertView.findViewById(R.id.day);
            holder.od = convertView.findViewById(R.id.odo);
            holder.ct = convertView.findViewById(R.id.content);
            holder.rl1 = convertView.findViewById(R.id.RelativeLayout2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int odoi = Integer.parseInt(odo[position]);
        String odo3 = String.format("%,d",odoi) + " Km";

        holder.dy.setText(day[position]);
        holder.od.setText(odo3);
        holder.ct.setText(cnt[position]);

        holder.rl1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, R.id.RelativeLayout2);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return day.length;
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
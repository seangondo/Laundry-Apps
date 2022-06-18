package com.pat.laundryapps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context c;
    ArrayList id, telp, tgl, stat, metode, diterima;
    LayoutInflater inflater;

    public CustomAdapter (Context c, ArrayList id, ArrayList telp, ArrayList tgl,
                          ArrayList stat, ArrayList metode, ArrayList diterima) {
        this.c = c;
        this.id = id;
        this.telp = telp;
        this.tgl = tgl;
        this.stat = stat;
        this.metode = metode;
        this.diterima = diterima;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return id.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.my_lvelement, null);
        TextView myid = (TextView) view.findViewById(R.id.tvID);
        TextView mytelp = (TextView) view.findViewById(R.id.tvNotelp);
        TextView mytgl = (TextView) view.findViewById(R.id.tvTgl);
        TextView mystat = (TextView) view.findViewById(R.id.tvStat);
        TextView mymetode = (TextView) view.findViewById(R.id.tvMethod);
        TextView myditerima = (TextView) view.findViewById(R.id.tvTerima);
        myid.setText(String.valueOf(id.get(i)));
        mytelp.setText(String.valueOf("No: "+telp.get(i)));
        mytgl.setText(String.valueOf("Order Date: "+tgl.get(i)));
        mystat.setText(String.valueOf("Status: "+stat.get(i)));
        mymetode.setText(String.valueOf("Metode Ambil: "+metode.get(i)));
        myditerima.setText(String.valueOf("Status Terima: "+diterima.get(i)));
        if(String.valueOf(diterima.get(i)).equals("Belum Diterima") & String.valueOf(stat.get(i)).equals("Selesai")) {
            view.setBackgroundColor(Color.YELLOW);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }
        return view;
    }
}

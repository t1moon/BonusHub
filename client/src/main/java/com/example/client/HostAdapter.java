package com.example.client;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.bonuslib.host.Host;

import java.util.List;

/**
 * Created by Timur on 29-Apr-17.
 */

public class HostAdapter extends RecyclerView.Adapter<HostAdapter.MyViewHolder>  {

    private List<Host> hostsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, descrpition;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.rv_title);
            descrpition = (TextView) view.findViewById(R.id.rv_description);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public HostAdapter(Context context,
                       List<Host> hostsList) {
        this.hostsList = hostsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.host_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Host host = hostsList.get(position);
        holder.title.setText(host.getTitle());
        holder.descrpition.setText(host.getDescription());
        // loading album cover using Glide library
//        Glide.with(context).load(host.getThumbnail()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return hostsList.size();
    }
}

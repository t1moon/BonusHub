package com.example.client;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.bonuslib.client_host.ClientHost;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Timur on 29-Apr-17.
 */

public class HostAdapter extends RecyclerView.Adapter<HostAdapter.MyViewHolder>  {
//
//    private List<Host> hostsList;
//    private List<Integer> points;

//    private Map<Host, Integer> pointsToHost;
    private List<ClientHost> clientHostList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, descrpition, points;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.rv_title);
            descrpition = (TextView) view.findViewById(R.id.rv_description);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            points = (TextView) view.findViewById(R.id.rv_points);
        }
    }


    public HostAdapter(Context context,
                       List<ClientHost> clientHostList) {
//        this.hostsList = hostsList;
//        this.points = points;
//
//        Iterator<Host> it1 = hostsList.iterator();
//        Iterator<Integer> it2 = points.iterator();
//
//        while (it1.hasNext() && it2.hasNext()) {
//            this.pointsToHost.put(it1.next(), it2.next());
//        }
        this.clientHostList= clientHostList;
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
        Host host = null;
        try {
            host = HelperFactory.getHelper().getHostDAO().getHostById(clientHostList.get(position).getHost().getId()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int point = clientHostList.get(position).getPoints();

        holder.title.setText(host.getTitle());
        holder.descrpition.setText(host.getDescription());
        holder.points.setText(Integer.toString(point));
        // loading album cover using Glide library
//        Glide.with(context).load(host.getThumbnail()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return clientHostList.size();
    }
}

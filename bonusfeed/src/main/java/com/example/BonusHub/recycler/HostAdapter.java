package com.example.BonusHub.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.BonusHub.retrofit.RetrofitFactory;
import com.example.BonusHub.db.client_host.ClientHost;
import com.example.BonusHub.db.HelperFactory;
import com.example.BonusHub.db.host.Host;
import com.example.timur.BonusHub.R;

import java.sql.SQLException;
import java.util.List;

public class HostAdapter extends RecyclerView.Adapter<HostAdapter.MyViewHolder>  {

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
            host = HelperFactory.getHelper().getHostDAO().getHostById(clientHostList.get(position).getHost().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int point = clientHostList.get(position).getPoints();

        if (host != null) {
            holder.title.setText(host.getTitle());
            holder.descrpition.setText(host.getDescription());
            Log.d("User's Points", Integer.toString(point));
            if (host.getLoyalityType() == 1) {
                holder.points.setText(Integer.toString(point));
            }
            else {
                holder.points.setText(Integer.toString(point) + "/" + Integer.toString(Math.round(host.getLoyalityParam())));
            }

            if (host.getProfile_image() != null) {
                String pathToImageProfile = RetrofitFactory.retrofitClient().baseUrl() + RetrofitFactory.MEDIA_URL + host.getProfile_image();
                // loading album cover using Glide library
                Log.d("Image", pathToImageProfile);
                Glide
                        .with(context)
                        .load(pathToImageProfile)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(holder.thumbnail);
            } else {
                // set default
                Glide
                        .with(context)
                        .load(R.drawable.test2)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(holder.thumbnail);
            }
        }

    }

    @Override
    public int getItemCount() {
        return clientHostList.size();
    }

    public ClientHost getItemByPosition(int position) {
        return clientHostList.get(position);
    }
}

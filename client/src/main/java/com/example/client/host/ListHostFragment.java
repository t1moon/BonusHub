package com.example.client.host;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bonuslib.client.Client;
import com.example.bonuslib.client_host.ClientHost;
import com.example.bonuslib.db.HelperFactory;
import com.example.client.MainActivity;
import com.example.client.R;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.client.MainActivity.getClientId;


public class ListHostFragment extends Fragment {
    private List<ClientHost> clientHostsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HostAdapter mAdapter;
    private MainActivity mainActivity;

    public ListHostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_list_host, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new HostAdapter(getActivity(), clientHostsList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                goToHostFragment(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        setInfo();
        prepareHostData();

        return rootView;
    }

    private void setInfo() {
        Client client = null;
        try {
            client = HelperFactory.getHelper().getClientDAO().getClientById(getClientId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (client != null) {
            NavigationView nvDrawer = (NavigationView) getActivity().findViewById(R.id.navigation_view);
            View header = nvDrawer.getHeaderView(0);
            TextView profileName = (TextView) header.findViewById(R.id.tv_profile_name);
            profileName.setText(client.getName());
        }

    }

    public void goToHostFragment(int position) {
        final Bundle bundle = new Bundle();
        int host_id = mAdapter.getItemByPosition(position).getHost().getId();
        bundle.putInt("host_id", host_id);
        mainActivity.pushFragment(new HostFragment(), true, bundle);
    }

    private void prepareHostData() {
        Client client = null;
        int client_id = getClientId();
        try {
            client = HelperFactory.getHelper().getClientDAO().getClientById(client_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<ClientHost> clientHosts = new ArrayList<>();
        try {
            clientHosts = HelperFactory.getHelper().getClientHostDAO().lookupHostForClient(client);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (ClientHost item : clientHosts) {
            clientHostsList.add(item);
        }

        mAdapter.notifyDataSetChanged();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getActivity().getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}

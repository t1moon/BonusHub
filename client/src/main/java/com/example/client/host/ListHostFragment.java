package com.example.client.host;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bonuslib.client.Client;
import com.example.bonuslib.client_host.ClientHost;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.example.client.MainActivity;
import com.example.client.R;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.example.client.MainActivity.getClientId;


public class ListHostFragment extends Fragment {
    private List<ClientHost> clientHostsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HostAdapter mAdapter;
    private MainActivity mainActivity;
    private SwipeRefreshLayout swipeRefreshLayout;
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

        setInfo();
        //prepareHostData();

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                prepareHostData();
            }
        });
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
        final HostListFetcher hostListFetcher = RetrofitFactory.retrofitClient().create(HostListFetcher.class);
        final Call<HostListResponse> call = hostListFetcher.listHosts(1);
        NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<HostListResponse>() {
            @Override
            public void onSuccess(HostListResponse result) {
                showResponse(result);
            }

            @Override
            public void onError(Exception ex) {
                showError(ex);
            }
        });
    }

    private void showResponse(HostListResponse response) {

        // clear tables
        HelperFactory.getHelper().clearTablesForClient(HelperFactory.getHelper().getConnectionSource());
        clientHostsList.clear();

        List<HostListResponse.HostPoints> hostPoints = response.getHosts();
        List<ClientHost> clientHosts = new ArrayList<>();
        ClientHost clientHost = null;
        for (HostListResponse.HostPoints hp : hostPoints) {
            Host host = new Host(hp.getTitle(), hp.getDescription(), hp.getAddress(), hp.getTime_open(), hp.getTime_close());

            Client client = null;
            try {
                HelperFactory.getHelper().getHostDAO().createHost(host);
                client = HelperFactory.getHelper().getClientDAO().getClientById(getClientId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            clientHost = new ClientHost(client, host, hp.getPoints());
            clientHostsList.add(clientHost);
            Toast.makeText(mainActivity.getApplicationContext(), hp.getTitle() + hp.getPoints(), Toast.LENGTH_SHORT).show();
        }


        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
    private void showError(Throwable error) {
        new AlertDialog.Builder(mainActivity)
                .setTitle("Ошибка")
                .setMessage(error.getMessage())
                .setPositiveButton("OK", null)
                .show();

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

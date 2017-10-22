package com.example.BonusHub.fragment.clientapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.BonusHub.activity.ClientMainActivity;
import com.example.BonusHub.db.HelperFactory;
import com.example.BonusHub.db.client.Client;
import com.example.BonusHub.db.client_host.ClientHost;
import com.example.BonusHub.db.host.Host;
import com.example.BonusHub.recycler.HostAdapter;
import com.example.BonusHub.recycler.RecyclerTouchListener;
import com.example.BonusHub.retrofit.ClientApiInterface;
import com.example.BonusHub.retrofit.RetrofitFactory;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.utils.AuthUtils;
import com.example.BonusHub.retrofit.clientapp.HostListResponse;
import com.example.timur.BonusHub.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ListHostFragment extends Fragment implements NetworkThread.ExecuteCallback<HostListResponse> {
    private List<ClientHost> clientHostsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HostAdapter mAdapter;
    private ClientMainActivity mainActivity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Integer hostsCallbackId;

    public ListHostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (ClientMainActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hostsCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(hostsCallbackId);
            hostsCallbackId = null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_list_host, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mainActivity.hasConnection()) {
                    getFromInternet();
                } else {
                    swipeRefreshLayout.setRefreshing(false);

                }

            }
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new HostAdapter(getActivity(), clientHostsList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        StaggeredGridLayoutManager mStaggeredLayoutManager;
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredLayoutManager);
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

        if (mainActivity.hasConnection()) {
            getFromInternet();
        } else {
            getFromCache();
        }

        ImageView imgView = (ImageView) getActivity().findViewById(R.id.backdrop);
        Glide
                .with(getActivity().getApplicationContext())
                .load(R.drawable.bonus_hub_logo)
                .fitCenter()
                .into(imgView);

        return rootView;
    }


    public void goToHostFragment(int position) {
        final Bundle bundle = new Bundle();
        int host_id = mAdapter.getItemByPosition(position).getHost().getId();
        bundle.putInt("host_id", host_id);
        mainActivity.pushFragment(new HostFragment(), true, bundle);
    }

    private void getFromCache() {
        clientHostsList.clear();
        Client client = null;
        int client_id = mainActivity.getPreferences(Context.MODE_PRIVATE).
                getInt(ClientMainActivity.CLIENT_ID, -1);
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
    private void getFromInternet() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        clientHostsList.clear();
        final ClientApiInterface clientApiInterface = RetrofitFactory.retrofitClient().create(ClientApiInterface.class);
        final Call<HostListResponse> call = clientApiInterface.listHosts(AuthUtils.getCookie(mainActivity.getApplicationContext()));
        if (hostsCallbackId == null) {
            hostsCallbackId = NetworkThread.getInstance().registerCallback(this);
            NetworkThread.getInstance().execute(call, hostsCallbackId);
        }
    }

    private void showResponse(HostListResponse response) {
        // clear tables
        HelperFactory.getHelper().clearTablesForClient(HelperFactory.getHelper().getConnectionSource());
        clientHostsList.clear();

        List<HostListResponse.HostPoints> hostPoints = response.getHosts();
        List<ClientHost> clientHosts = new ArrayList<>();
        ClientHost clientHost = null;
        Client client = null;
        int client_id = mainActivity.getPreferences(Context.MODE_PRIVATE).
                getInt(ClientMainActivity.CLIENT_ID, -1);

        try {
            client = HelperFactory.getHelper().getClientDAO().getClientById(client_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (HostListResponse.HostPoints hp : hostPoints) {
            Host host = new Host(hp.getTitle(), hp.getDescription(), hp.getAddress(), hp.getTime_open(), hp.getTime_close());
            host.setProfile_image(hp.getProfile_image());
            try {
                HelperFactory.getHelper().getHostDAO().createHost(host);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.d("User's Points", Integer.toString(hp.getPoints()));
            clientHost = new ClientHost(client, host, hp.getPoints());
            try {
                HelperFactory.getHelper().getClientHostDAO().createClientHost(client, host, hp.getPoints());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            clientHostsList.add(clientHost);
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

        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResponse(Call<HostListResponse> call, Response<HostListResponse> response) {

    }

    @Override
    public void onFailure(Call<HostListResponse> call, Response<HostListResponse> response) {
        NetworkThread.getInstance().unRegisterCallback(hostsCallbackId);
        hostsCallbackId = null;
        if (response.code() == 403) {
            Toast.makeText(getActivity(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
            AuthUtils.logout(getActivity().getApplicationContext());
            AuthUtils.setCookie(getActivity().getApplicationContext(), "");

        }
        else if(response.code() > 500) {
            Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(HostListResponse result) {
        NetworkThread.getInstance().unRegisterCallback(hostsCallbackId);
        hostsCallbackId = null;
        showResponse(result);
    }

    @Override
    public void onError(Exception ex) {
        NetworkThread.getInstance().unRegisterCallback(hostsCallbackId);
        hostsCallbackId = null;
        showError(ex);
    }

}

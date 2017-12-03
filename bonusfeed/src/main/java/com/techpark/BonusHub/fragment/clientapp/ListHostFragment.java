package com.techpark.BonusHub.fragment.clientapp;

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
import com.techpark.BonusHub.activity.ClientMainActivity;
import com.techpark.BonusHub.db.HelperFactory;
import com.techpark.BonusHub.db.client.Client;
import com.techpark.BonusHub.db.client_host.ClientHost;
import com.techpark.BonusHub.db.host.Host;
import com.techpark.BonusHub.recycler.HostAdapter;
import com.techpark.BonusHub.recycler.RecyclerTouchListener;
import com.techpark.BonusHub.retrofit.ClientApiInterface;
import com.techpark.BonusHub.retrofit.RetrofitFactory;
import com.techpark.BonusHub.threadManager.NetworkThread;
import com.techpark.BonusHub.utils.AuthUtils;
import com.techpark.BonusHub.retrofit.clientapp.HostListResponse;
import com.techpark.BonusHub.utils.EndlessScrollListener;
import com.techpark.timur.BonusHub.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListHostFragment extends Fragment implements NetworkThread.ExecuteCallback<HostListResponse> {
    private List<ClientHost> clientHostsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HostAdapter mAdapter;
    private ClientMainActivity mainActivity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Integer hostsCallbackId;
    EndlessScrollListener scrollListener;

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
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new HostAdapter(getActivity(), clientHostsList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        StaggeredGridLayoutManager mStaggeredLayoutManager;
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredLayoutManager);
        scrollListener = new EndlessScrollListener(mStaggeredLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (mainActivity.hasConnection()) {
                    loadNextData(totalItemsCount);
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mainActivity.hasConnection()) {
                    scrollListener.resetState();
                    getFromInternet();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
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
                .load(R.drawable.bonusfeed_logo)
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
        List<ClientHost> newClientHostsList = new ArrayList<>();
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
            newClientHostsList.add(item);
        }
        clientHostsList.clear();
        clientHostsList.addAll(newClientHostsList);
        mAdapter.notifyDataSetChanged();
    }

    private void getFromInternet() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        final ClientApiInterface clientApiInterface = RetrofitFactory.retrofitClient().create(ClientApiInterface.class);
        final Call<HostListResponse> call = clientApiInterface.listHosts(0, AuthUtils.getCookie(mainActivity.getApplicationContext()));
        if (hostsCallbackId == null) {
            hostsCallbackId = NetworkThread.getInstance().registerCallback(this);
            NetworkThread.getInstance().execute(call, hostsCallbackId);
        }
    }

    private void loadNextData(int totalItemsCount) {
        final ClientApiInterface clientApiInterface = RetrofitFactory.retrofitClient().create(ClientApiInterface.class);
        final Call<HostListResponse> call = clientApiInterface.listHosts(totalItemsCount, AuthUtils.getCookie(mainActivity.getApplicationContext()));
        call.enqueue(new Callback<HostListResponse>() {
            @Override
            public void onResponse(Call<HostListResponse> call, Response<HostListResponse> response) {
                showNewData(response.body());
            }

            @Override
            public void onFailure(Call<HostListResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNewData(HostListResponse response) {
        List<HostListResponse.HostPoints> hostPoints = response.getHosts();
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
            host.setLoyalityParam(hp.getLoyalityParam());
            host.setLoyalityType(hp.getLoyalityType());
            host.setLatitude(hp.getLatitude());
            host.setLongitude(hp.getLongitude());
            try {
                HelperFactory.getHelper().getHostDAO().createHost(host);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            clientHost = new ClientHost(client, host, hp.getPoints());
            try {
                HelperFactory.getHelper().getClientHostDAO().createClientHost(client, host, hp.getPoints());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            clientHostsList.add(clientHost);
        }


        mAdapter.notifyDataSetChanged();
    }

    private void showResponse(HostListResponse response) {
        // clear tables
        HelperFactory.getHelper().clearTablesForClient(HelperFactory.getHelper().getConnectionSource());
        //clientHostsList.clear();
        List<ClientHost> newClientHostsList = new ArrayList();
        List<HostListResponse.HostPoints> hostPoints = response.getHosts();
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
            host.setLoyalityParam(hp.getLoyalityParam());
            host.setLoyalityType(hp.getLoyalityType());
            host.setLatitude(hp.getLatitude());
            host.setLongitude(hp.getLongitude());
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
            newClientHostsList.add(clientHost);
        }
        clientHostsList.clear();
        clientHostsList.addAll(newClientHostsList);

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
        swipeRefreshLayout.setRefreshing(false);
        NetworkThread.getInstance().unRegisterCallback(hostsCallbackId);
        hostsCallbackId = null;
        if (response.code() == 401) {
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
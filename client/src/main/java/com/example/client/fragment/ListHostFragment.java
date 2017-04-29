package com.example.client.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.example.client.DividerItemDecoration;
import com.example.client.HostAdapter;
import com.example.client.R;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ListHostFragment extends Fragment {

    private List<Host> hostList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HostAdapter mAdapter;

    public ListHostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_list_host, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new HostAdapter(hostList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);


        prepareHostData();

        return rootView;
    }

    private void prepareHostData() {
        Host host = new Host("first", "desc");
        hostList.add(host);

        host = new Host("second", "qwe");
        hostList.add(host);

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

}

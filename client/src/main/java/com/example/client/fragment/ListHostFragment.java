package com.example.client.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.example.client.DividerItemDecoration;
import com.example.client.GridSpacingItemDecoration;
import com.example.client.HostAdapter;
import com.example.client.R;
import com.example.client.RecyclerTouchListener;


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
        mAdapter = new HostAdapter(getActivity(), hostList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                goToHostFragment();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareHostData();

        try {
            Glide.with(this).load(R.drawable.bonus_hub_logo).into((ImageView) getActivity().findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public void goToHostFragment() {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new HostFragment(), "");
        ft.addToBackStack(null);
        ft.commit();
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
    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getActivity().getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}

package com.example.BonusHub.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BonusHub.activity.HostMainActivity;
import com.example.BonusHub.db.staff.Staff;
import com.example.BonusHub.recycler.RecyclerItemTouchHelperLeft;
import com.example.BonusHub.recycler.StaffRecyclerAdapter;
import com.example.BonusHub.retrofit.HostApiInterface;
import com.example.BonusHub.retrofit.RetrofitFactory;
import com.example.BonusHub.retrofit.staff.GetStaffResponse;
import com.example.BonusHub.retrofit.staff.Hire;
import com.example.BonusHub.retrofit.staff.HireResponse;
import com.example.BonusHub.retrofit.staff.Retire;
import com.example.BonusHub.retrofit.staff.RetireResponse;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.utils.AuthUtils;
import com.example.timur.BonusHub.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class StaffListFragment extends Fragment implements RecyclerItemTouchHelperLeft.RecyclerItemTouchHelperListener {
    private StaffRecyclerAdapter adapter;
    private ArrayList<Staff> staffList= new ArrayList<>();
    RecyclerView recyclerView;
    TextView no_task_tv;
    private static Fragment fragmentInstance;
    HostApiInterface hostApiInterface;

    private static NetworkThread.ExecuteCallback<GetStaffResponse> getStaffCallback;
    private static NetworkThread.ExecuteCallback<HireResponse> hireCallback;
    private static NetworkThread.ExecuteCallback<RetireResponse> retireCallback;
    private Integer getStaffCallbackId;
    private Integer hireCallbackId;
    private Integer retireCallbackId;

    public StaffListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_staff, container, false);
        prepareCallback();

        no_task_tv = (TextView) view.findViewById(R.id.no_task_tv);
        recyclerView = (RecyclerView) view.findViewById(R.id.tasks_list);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new StaffRecyclerAdapter(staffList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        fragmentInstance = this;

        ItemTouchHelper.SimpleCallback itemTouchHelperLeftCallback = new RecyclerItemTouchHelperLeft(
                0, ItemTouchHelper.LEFT, this);

        new ItemTouchHelper(itemTouchHelperLeftCallback).attachToRecyclerView(recyclerView);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator.forSupportFragment(fragmentInstance).setBeepEnabled(false).initiateScan();

            }
        });
        fab.setImageResource(R.drawable.ic_add_24dp);


        hostApiInterface = RetrofitFactory.retrofitHost().create(HostApiInterface.class);
        getStaff();
        return view;
    }

    private void prepareCallback() {
        getStaffCallback = new NetworkThread.ExecuteCallback<GetStaffResponse>() {
            @Override
            public void onResponse(Call<GetStaffResponse> call, Response<GetStaffResponse> response) {

            }

            @Override
            public void onFailure(Call<GetStaffResponse> call, Response<GetStaffResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(getStaffCallbackId);
                getStaffCallbackId = null;
            }

            @Override
            public void onSuccess(GetStaffResponse result) {
                NetworkThread.getInstance().unRegisterCallback(getStaffCallbackId);
                getStaffCallbackId = null;
                showStaff(result);
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(getStaffCallbackId);
                getStaffCallbackId = null;
                showError(ex);
            }
        };

        hireCallback = new NetworkThread.ExecuteCallback<HireResponse>() {
            @Override
            public void onResponse(Call<HireResponse> call, Response<HireResponse> response) {

            }

            @Override
            public void onFailure(Call<HireResponse> call, Response<HireResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(hireCallbackId);
                hireCallbackId = null;
            }

            @Override
            public void onSuccess(HireResponse result) {
                NetworkThread.getInstance().unRegisterCallback(hireCallbackId);
                getStaffCallbackId = null;
                getStaff();
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(hireCallbackId);
                getStaffCallbackId = null;
                showError(ex);
            }
        };
        retireCallback = new NetworkThread.ExecuteCallback<RetireResponse>() {
            @Override
            public void onResponse(Call<RetireResponse> call, Response<RetireResponse> response) {

            }

            @Override
            public void onFailure(Call<RetireResponse> call, Response<RetireResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(retireCallbackId);
                retireCallbackId = null;
            }

            @Override
            public void onSuccess(RetireResponse result) {
                NetworkThread.getInstance().unRegisterCallback(retireCallbackId);
                retireCallbackId = null;
                //showStaff(result);
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(retireCallbackId);
                retireCallbackId = null;
                showError(ex);
            }
        };
    }
    private void showError(Exception error) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Ошибка")
                .setMessage(error.getMessage())
                .setPositiveButton("OK", null)
                .show();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getStaffCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(getStaffCallbackId);
        }
        if (hireCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(hireCallbackId);
        }
        if (retireCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(retireCallbackId);
        }

    }

    private void getStaff() {
        final Call<GetStaffResponse> call = hostApiInterface.getStaff(AuthUtils.getCookie(
                ((HostMainActivity) getActivity()).getApplicationContext()));
        if (getStaffCallbackId == null) {
            getStaffCallbackId = NetworkThread.getInstance().registerCallback(getStaffCallback);
            NetworkThread.getInstance().execute(call, getStaffCallbackId);
        }
    }

    // Get the results:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
                ((HostMainActivity)getActivity()).popFragment();
            } else {
                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                final Call<HireResponse> call = hostApiInterface.hire(new Hire(result.getContents()),
                        AuthUtils.getCookie(((HostMainActivity) getActivity()).getApplicationContext()));
                if (hireCallbackId == null) {
                    hireCallbackId = NetworkThread.getInstance().registerCallback(hireCallback);
                    NetworkThread.getInstance().execute(call, hireCallbackId);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, final int direction, int position) {
        if (viewHolder instanceof StaffRecyclerAdapter.StaffHolder) {
            // backup of removed item for undo purpose
            final Staff deletedItem = staffList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

            final String userId = deletedItem.getUser_id();
            if (direction == ItemTouchHelper.LEFT){
                Snackbar snackbar;
                snackbar = Snackbar.make(getView(), getString(R.string.delete_undo), Snackbar.LENGTH_LONG);
                final Call<RetireResponse> call = hostApiInterface.retire(new Retire(userId),
                        AuthUtils.getCookie(((HostMainActivity) getActivity()).getApplicationContext()));
                if (retireCallbackId == null) {
                    retireCallbackId = NetworkThread.getInstance().registerCallback(retireCallback);
                    NetworkThread.getInstance().execute(call, retireCallbackId);
                }


                snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // undo is selected, restore the deleted item
                        adapter.restoreItem(deletedItem, deletedIndex);
                        if (direction == ItemTouchHelper.LEFT) {
                            final Call<HireResponse> call = hostApiInterface.hire(new Hire(userId),
                                    AuthUtils.getCookie(((HostMainActivity) getActivity()).getApplicationContext()));
                            if (hireCallbackId == null) {
                                hireCallbackId = NetworkThread.getInstance().registerCallback(hireCallback);
                                NetworkThread.getInstance().execute(call, hireCallbackId);
                            }
                        }
                    }
                });
                snackbar.setActionTextColor(Color.parseColor("#FFC107"));
                snackbar.show();
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void showStaff(GetStaffResponse response) {
        List<GetStaffResponse.Staff> staffListLocal = response.getStaff();
        staffList.clear();
        no_task_tv.setVisibility(View.GONE);
        for (GetStaffResponse.Staff s: staffListLocal) {
            Staff staff = new Staff();
            staff.setLogin(s.getLogin());
            staff.setUser_id(s.getWorker_id());
            staffList.add(staff);
        }
        adapter.notifyDataSetChanged();
    }

}

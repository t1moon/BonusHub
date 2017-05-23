package com.example.BonusHub.activity.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.BonusHub.activity.AuthUtils;
import com.example.BonusHub.activity.activity.LogInActivity;
import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.api.host.HostResult;
import com.example.BonusHub.activity.retrofit.ApiInterface;
import com.example.BonusHub.activity.threadManager.NetworkThread;
import com.example.BonusHub.activity.retrofit.RetrofitFactory;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsPojo;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsResponse;
import com.example.timur.BonusHub.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the { ScanQrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanQrFragment extends Fragment implements NetworkThread.ExecuteCallback<UpdatePointsResponse> {

    public final static int HOST_PERCENTAGE = 10;

    private Integer updatePointsCallbackId;

    String client_identificator = null;
    private static Fragment fragmentInstance;
    MainActivity mainActivity;

    EditText et_bill;
    SwitchCompat switchCompat;

    public ScanQrFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updatePointsCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(updatePointsCallbackId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan_qr, container, false);
        et_bill = (EditText) rootView.findViewById(R.id.et_bill);
        switchCompat = (SwitchCompat) rootView.findViewById(R.id.switch_gprs);

        fragmentInstance = this;
        // ENABLE THAT
        //IntentIntegrator.forSupportFragment(fragmentInstance).initiateScan();

        final Button update_points_btn = (Button) rootView.findViewById(R.id.update_points_btn);

        update_points_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePoints(switchCompat.isChecked());
            }
        });

        return rootView;
    }

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // Get the results:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
                mainActivity.popFragment();
            } else {
                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                client_identificator = result.getContents();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updatePoints(boolean isAddTo) {

        client_identificator = "QfgnJKEGNRojer";

        final ApiInterface apiInterface = RetrofitFactory.retrofitHost().create(ApiInterface.class);
        final Call<UpdatePointsResponse> call;

        int bill = Integer.parseInt(et_bill.getText().toString());
        call = apiInterface.update_points(new UpdatePointsPojo(client_identificator, bill, isAddTo), AuthUtils.getCookie(mainActivity));

        if (updatePointsCallbackId == null) {
            updatePointsCallbackId = NetworkThread.getInstance().registerCallback(this);
            NetworkThread.getInstance().execute(call,updatePointsCallbackId);
        }
    }

    private void showResponse(UpdatePointsResponse result) {
        if (result.getCode() == 0) {
            Toast.makeText(getActivity().getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            // if something went wrong
            Toast.makeText(getActivity().getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void showError(Throwable error) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Ошибка")
                .setMessage(error.getMessage())
                .setPositiveButton("OK", null)
                .show();

    }

    @Override
    public void onResponse(Call<UpdatePointsResponse> call, Response<UpdatePointsResponse> response) {
    }

    @Override
    public void onFailure(Call<UpdatePointsResponse> call, Response<UpdatePointsResponse> response) {
        NetworkThread.getInstance().unRegisterCallback(updatePointsCallbackId);
        updatePointsCallbackId = null;
        Toast.makeText(getActivity(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
        AuthUtils.logout(getActivity());
        goToLogin();
    }

    @Override
    public void onSuccess(UpdatePointsResponse result) {
        NetworkThread.getInstance().unRegisterCallback(updatePointsCallbackId);
        updatePointsCallbackId = null;
        showResponse(result);
    }

    @Override
    public void onError(Exception ex) {
        NetworkThread.getInstance().unRegisterCallback(updatePointsCallbackId);
        updatePointsCallbackId = null;
        showError(ex);
    }


}

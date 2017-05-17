package com.example.BonusHub.activity.fragment;


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

import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.retrofit.ApiInterface;
import com.example.BonusHub.activity.retrofit.NetworkThread;
import com.example.BonusHub.activity.retrofit.RetrofitFactory;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsPojo;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsResponse;
import com.example.timur.BonusHub.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Use the { ScanQrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanQrFragment extends Fragment {

    public final static int HOST_PERCENTAGE = 10;

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

        // JUST FOR TESTING
        client_identificator = "QfgnJKEGNRojer";

        final ApiInterface apiInterface = RetrofitFactory.retrofitHost().create(ApiInterface.class);
        final Call<UpdatePointsResponse> call;

        final int host_id = 1;
        int bill = Integer.parseInt(et_bill.getText().toString());
        call = apiInterface.update_points(new UpdatePointsPojo(host_id, client_identificator, bill, isAddTo));

        NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<UpdatePointsResponse>() {
            @Override
            public void onSuccess(UpdatePointsResponse result) {
                showResponse(result);
            }
            @Override
            public void onError(Exception ex) {
                showError(ex);
            }
        });
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


}

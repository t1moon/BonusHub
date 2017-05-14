package com.example.BonusHub.activity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.BonusHub.activity.retrofit.ApiInterface;
import com.example.BonusHub.activity.retrofit.NetworkThread;
import com.example.BonusHub.activity.retrofit.RetrofitFactory;
import com.example.BonusHub.activity.retrofit.Withdraw;
import com.example.BonusHub.activity.retrofit.WithdrawResponse;
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

    private static Fragment fragmentInstance;

    public ScanQrFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan_qr, container, false);

        // Inflate the layout for this fragment
        final Button scan_btn = (Button) rootView.findViewById(R.id.scan_btn);

        fragmentInstance = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.forSupportFragment(fragmentInstance).setPrompt("Some prompt").initiateScan();

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
        String client_identificator = null;
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
//                client_identificator = "QfgnJKEGNRojer";
                client_identificator = result.getContents();
                final ApiInterface apiInterface = RetrofitFactory.retrofitHost().create(ApiInterface.class);
                final Call<WithdrawResponse> call = apiInterface.withdraw(new Withdraw(1, 18, client_identificator));
                NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<WithdrawResponse>() {
                    @Override
                    public void onSuccess(WithdrawResponse result) {
                        showResponse(result);
                    }

                    @Override
                    public void onError(Exception ex) {
                        showError(ex);
                    }
                });




            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void showResponse(WithdrawResponse result) {
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

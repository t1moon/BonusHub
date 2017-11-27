package com.example.BonusHub.fragment;


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

import com.example.BonusHub.activity.StaffMainActivity;
import com.example.BonusHub.retrofit.ScoreApiInterface;
import com.example.BonusHub.utils.AuthUtils;
import com.example.BonusHub.activity.HostMainActivity;
import com.example.BonusHub.activity.LogInActivity;
import com.example.BonusHub.retrofit.HostApiInterface;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.retrofit.RetrofitFactory;
import com.example.BonusHub.retrofit.updatePoints.UpdatePointsPojo;
import com.example.BonusHub.retrofit.updatePoints.UpdatePointsResponse;
import com.example.timur.BonusHub.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the { ScanQrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanQrFragment extends Fragment implements NetworkThread.ExecuteCallback<UpdatePointsResponse> {

    private Integer updatePointsCallbackId;

    String client_identificator = null;
    private static Fragment fragmentInstance;
    HostMainActivity hostMainActivity;
    StaffMainActivity staffMainActivity;
    EditText et_bill;
    SwitchCompat switchCompat;

    public ScanQrFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AuthUtils.getRole(getActivity().getApplicationContext()).equals("Host")) {
            staffMainActivity = (StaffMainActivity) getActivity();
        }
        else {
            hostMainActivity = (HostMainActivity) getActivity();
        }
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
        IntentIntegrator.forSupportFragment(fragmentInstance).setBeepEnabled(false).initiateScan();

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
                if (hostMainActivity != null) {
                    hostMainActivity.popFragment();
                }
                else {
                    staffMainActivity.popFragment();
                }
            } else {
                //Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                client_identificator = result.getContents();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updatePoints(boolean isAddTo) {

        final ScoreApiInterface scoreApiInterface = RetrofitFactory.retrofitScore().create(ScoreApiInterface.class);
        final Call<UpdatePointsResponse> call;
        String bill_str = "";
        bill_str = et_bill.getText().toString();
        if (bill_str != "") {
            Float bill = Float.parseFloat(et_bill.getText().toString());
            int loyality_type = getActivity().getPreferences(MODE_PRIVATE).getInt("loy_type", -1);
            if ((loyality_type == -1) || (loyality_type == 1)) {
                if (switchCompat.isChecked()) {
                    call = scoreApiInterface.updateBonus(new UpdatePointsPojo(client_identificator, -bill), AuthUtils.getCookie(getActivity().getApplicationContext()));
                } else {
                    call = scoreApiInterface.updateBonus(new UpdatePointsPojo(client_identificator, bill), AuthUtils.getCookie(getActivity().getApplicationContext()));
                }
            } else {
                if (switchCompat.isChecked()) {
                    call = scoreApiInterface.updateCups(new UpdatePointsPojo(client_identificator, -bill), AuthUtils.getCookie(getActivity().getApplicationContext()));
                } else {
                    call = scoreApiInterface.updateCups(new UpdatePointsPojo(client_identificator, bill), AuthUtils.getCookie(getActivity().getApplicationContext()));
                }
            }
            if (updatePointsCallbackId == null) {
                updatePointsCallbackId = NetworkThread.getInstance().registerCallback(this);
                NetworkThread.getInstance().execute(call, updatePointsCallbackId);
            }
        }
    }

    private void showResponse(UpdatePointsResponse result) {
        if (result.getCode() == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "Транзакция успешно проведена", Toast.LENGTH_SHORT).show();
        } else {
            // if something went wrong
            Toast.makeText(getActivity().getApplicationContext(), "Нехватка средств на балансе", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(getActivity(), "Ошибка аутентификации. Попробуйте пройти повторную авторизацию", Toast.LENGTH_SHORT).show();
        if (response.code() == 400) {
            Toast.makeText(getActivity(), "Некорректный User_ID", Toast.LENGTH_SHORT).show();

        }
        if (response.code() == 401) {
            Toast.makeText(getActivity(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
            AuthUtils.logout(getActivity());
            goToLogin();

        }
        if (response.code() == 403) {
            Toast.makeText(getActivity(), "Вы не имеете прав доступа", Toast.LENGTH_SHORT).show();
            AuthUtils.logout(getActivity());
            goToLogin();

        }
        else if(response.code() > 500) {
            Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
        }
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
        Toast.makeText(getActivity(), "Ошибка соединения с сервером. Проверьте интернет подключение.", Toast.LENGTH_SHORT).show();
    }


}

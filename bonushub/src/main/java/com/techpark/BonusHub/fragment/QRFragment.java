package com.techpark.BonusHub.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.techpark.BonusHub.activity.LogInActivity;
import com.techpark.BonusHub.activity.StaffMainActivity;
import com.techpark.BonusHub.retrofit.HostApiInterface;
import com.techpark.BonusHub.retrofit.RetrofitFactory;
import com.techpark.BonusHub.retrofit.getInfo.GetInfoResponse;
import com.techpark.BonusHub.threadManager.NetworkThread;
import com.techpark.BonusHub.utils.AuthUtils;
import com.techpark.BonusHub.utils.FragmentType;
import com.techpark.timur.BonusHub.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import retrofit2.Call;
import retrofit2.Response;


public class QRFragment extends Fragment {

    View rootView;
    public final static int QRcodeWidth = 800;
    Bitmap bitmap;
    private SwipeRefreshLayout swipeRefreshLayout;
    ImageView imageView;

    private static NetworkThread.ExecuteCallback<GetInfoResponse> netInfoCallback;
    private Integer netInfoCallbackId;

    private LogInActivity logInActivity;

    public QRFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareCallbacks();
        logInActivity = (LogInActivity) getActivity();
        prepareCallbacks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qrcode, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.iv_qrcode);
        final ProgressBar spinner;
        spinner = (ProgressBar)rootView.findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfo();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = TextToImageEncode(AuthUtils.getUserId(getActivity().getApplicationContext()));
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                            spinner.setVisibility(View.GONE);
                        }
                    });

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return rootView;
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];


        int black = 0;
        int white = 0;
        if (isAdded()) {
            black = getResources().getColor(R.color.black);
            white = getResources().getColor(R.color.white);
        }

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? black : white;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private void getInfo() {
        swipeRefreshLayout.setRefreshing(true);
        final HostApiInterface hostApiInterface = RetrofitFactory.retrofitHost().create(HostApiInterface.class);
        final Call<GetInfoResponse> call = hostApiInterface.getInfo(AuthUtils.getCookie(getActivity().getApplicationContext()));
        if (netInfoCallbackId == null) {
            netInfoCallbackId = NetworkThread.getInstance().registerCallback(netInfoCallback);
            NetworkThread.getInstance().execute(call, netInfoCallbackId);
        }
    }

    public void goToMainActivity() {
        swipeRefreshLayout.setRefreshing(false);
        Intent intent = null;

        intent = new Intent(getActivity(), StaffMainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void goToLogin() {
        logInActivity.setCurrentFragment(FragmentType.LogInFragment);
        logInActivity.pushFragment(new LogInFragment(), true);
    }

    private void showError(Exception error) {
        new AlertDialog.Builder(logInActivity)
                .setTitle("Ошибка")
                .setMessage("Ошибка сервера. Попробуйте повторить запрос позже")
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

    private void prepareCallbacks() {
        netInfoCallback = new NetworkThread.ExecuteCallback<GetInfoResponse>() {
            @Override
            public void onResponse(Call<GetInfoResponse> call, Response<GetInfoResponse> response) {

            }

            @Override
            public void onFailure(Call<GetInfoResponse> call, Response<GetInfoResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(netInfoCallbackId);
                netInfoCallbackId = null;
                swipeRefreshLayout.setRefreshing(false);
                if (response.code() == 400) {
                    Toast.makeText(getActivity(), "Вы не являетесь сотрудником или владельцем какого-либо заведения", Toast.LENGTH_SHORT).show();
                }
                if (response.code() == 401) {
                    Toast.makeText(getActivity(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
                    AuthUtils.logout(getActivity());
                    goToLogin();

                }
                if (response.code() == 404) {
                    Toast.makeText(getActivity(), "Данное заведение не существует", Toast.LENGTH_SHORT).show();

                }
                else if(response.code() > 500) {
                    Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(GetInfoResponse result) {
                NetworkThread.getInstance().unRegisterCallback(netInfoCallbackId);
                //AuthUtils.setHosted(getActivity().getApplicationContext(), true);
                netInfoCallbackId = null;
                goToMainActivity();
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(netInfoCallbackId);
                netInfoCallbackId = null;
                swipeRefreshLayout.setRefreshing(false);
                showError(ex);
            }
        };
    }

}

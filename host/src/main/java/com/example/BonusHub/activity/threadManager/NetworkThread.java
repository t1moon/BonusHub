package com.example.BonusHub.activity.threadManager;


import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;


public class NetworkThread {
    private final static NetworkThread INSTANCE = new NetworkThread();

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private ExecuteCallback callback;

    public void setCallback(ExecuteCallback newCallback) {
        callback = newCallback;
    }

    private NetworkThread() {
    }

    public static NetworkThread getInstance() {
        return INSTANCE;
    }

    public <T> void execute(final Call<T> call) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    final Response<T> response = call.execute();
                    if (response.isSuccessful() && callback != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(call, response);
                                callback.onSuccess(response.body());
                            }
                        });
                    } else if (callback != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(call, response);
                            }
                        });
                    }
                } catch (final IOException e) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }

    public interface ExecuteCallback<T> {
        void onResponse(Call<T> call, Response<T> response);
        void onFailure(Call<T> call,  Response<T> response);
        void onSuccess(T result);

        void onError(Exception ex);
    }
}

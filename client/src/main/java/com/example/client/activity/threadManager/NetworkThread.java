package com.example.client.activity.threadManager;


import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NetworkThread {
    private final static NetworkThread INSTANCE = new NetworkThread();

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private NetworkThread() {
    }

    public static NetworkThread getInstance() {
        return INSTANCE;
    }

    public <T> void execute(final Call<T> call, final ExecuteCallback<T> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<T> response = call.execute();
                    if (response.isSuccessful()) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(call, response);
                                callback.onSuccess(response.body());
                            }
                        });
                    } else {
                        final String error = response.errorBody().string();
                        throw new IOException(error);
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

    public interface ExecuteCallback<T> extends Callback<T> {
        @Override
        void onResponse(Call<T> call, Response<T> response);
        @Override
        void onFailure(Call<T> call, Throwable t);
        void onSuccess(T result);

        void onError(Exception ex);


    }
}

package com.techpark.client.threadManager;


import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;


public class NetworkThread {
    private final static NetworkThread INSTANCE = new NetworkThread();

    private final Executor executor = Executors.newCachedThreadPool();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Map<Integer, ExecuteCallback> callbacks = new HashMap<>();
    private static int idCounter = 1;

    public Integer registerCallback(ExecuteCallback newCallback) {
        callbacks.put(idCounter, newCallback);
        return idCounter++;
    }

    public void unRegisterCallback (int iD) {
        callbacks.remove(iD);
        if (callbacks.isEmpty()) {
            idCounter = 1;
        }
    }

    private NetworkThread() {
    }

    public static NetworkThread getInstance() {
        return INSTANCE;
    }

    public <T> void execute(final Call<T> call, final Integer callbackId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<T> response = call.execute();
                    if (response.isSuccessful() && callbacks.containsKey(callbackId)) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final NetworkThread.ExecuteCallback<T> callback;
                                callback = callbacks.get(callbackId);
                                callback.onResponse(call, response);
                                callback.onSuccess(response.body());
                            }
                        });
                    } else if (callbacks.containsKey(callbackId)) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final NetworkThread.ExecuteCallback<T> callback = callbacks.get(callbackId);
                                callback.onFailure(call, response);
                            }
                        });
                    }
                } catch (final IOException e) {
                    if (callbacks.containsKey(callbackId)) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final NetworkThread.ExecuteCallback<T> callback = callbacks.get(callbackId);
                                callback.onError(e);
                            }
                        });
                    }
                }
            }
        });
    }

    public interface ExecuteCallback<T> {
        void onResponse(Call<T> call, Response<T> response);
        void onFailure(Call<T> call, Response<T> response);
        void onSuccess(T result);

        void onError(Exception ex);
    }
}

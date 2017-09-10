package com.example.BonusHub.threadManager;


import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;

import retrofit2.Call;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;


public class NetworkThread {
    private final static NetworkThread INSTANCE = new NetworkThread();

    private final Executor executor = Executors.newCachedThreadPool();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    @SuppressLint("UseSparseArrays")
    private final Map<Integer, ExecuteCallback> callbackMap = new HashMap<>();
    private static int idCounter = 1;

    public Integer registerCallback(ExecuteCallback newCallback) {
        callbackMap.put(idCounter, newCallback);
        return idCounter++;
    }

    public void unRegisterCallback (int iD) {
        callbackMap.remove(iD);
        if (callbackMap.isEmpty()) {
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
                    if (response.isSuccessful() && callbackMap.containsKey(callbackId)) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final NetworkThread.ExecuteCallback<T> callback;
                                callback = callbackMap.get(callbackId);
                                callback.onResponse(call, response);
                                callback.onSuccess(response.body());
                            }
                        });
                    } else if (callbackMap.containsKey(callbackId)) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final NetworkThread.ExecuteCallback<T> callback = callbackMap.get(callbackId);
                                callback.onFailure(call, response);
                            }
                        });
                    }
                } catch (final IOException e) {
                    if (callbackMap.containsKey(callbackId)) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final NetworkThread.ExecuteCallback<T> callback = callbackMap.get(callbackId);
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
        void onFailure(Call<T> call,  Response<T> response);
        void onSuccess(T result);
        void onError(Exception ex);
    }
}

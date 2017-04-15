package com.example.BonusHub.activity.threadManager;

import android.content.Context;

import java.util.concurrent.Executor;

/**
 * Created by mike on 15.04.17.
 */

public class LoginThreadManager {
    private final String url = "";
    private static final LoginThreadManager LOADER = new LoginThreadManager();

    public static LoginThreadManager getInstance() {return LOADER;
    }

    public interface Callback {
        void onLoaded(boolean result);
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void load(final Context context) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String result;
                try {
                    result = APIProcessor.getInstance().loadInternal(url);
                } catch (IOException e) {
                    result = null;
                }
                if (result != null) {
                    LoginStorage.getInstance(context).saveLogin();
                }
                if (callback != null) {
                    callback.onLoaded(true);
                }
            }
        });
    }
}

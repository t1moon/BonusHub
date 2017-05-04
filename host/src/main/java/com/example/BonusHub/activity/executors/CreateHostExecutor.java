package com.example.BonusHub.activity.executors;

import android.util.Log;

import com.example.BonusHub.activity.Ui;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;

import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Timur on 04-May-17.
 */

public class CreateHostExecutor {

    private final static String TAG = CreateHostExecutor.class.getSimpleName();

    private static final CreateHostExecutor CREATE_HOST = new CreateHostExecutor();

    public static CreateHostExecutor getInstance() {
        return CREATE_HOST;
    }

    public interface Callback {
        void onCreated(int host_id);
    }

    private final Executor executor = Executors.newCachedThreadPool();

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void createHost(final Host host) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int host_id = -1;
                try {
                    host_id = HelperFactory.getHelper().getHostDAO().createHost(host);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (host_id != -1) {
                    notifyCreated(host_id);
                }

            }
        });
    }

    private void notifyCreated(final int host_id) {
        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onCreated(host_id);
                Log.d(TAG, "notify UI");
            }
        });
    }
}

package com.example.BonusHub.executors;

import android.util.Log;

import com.example.BonusHub.Ui;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;

import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Timur on 04-May-17.
 */

public class GetHostInfoExecutor {

    private final static String TAG = GetHostInfoExecutor.class.getSimpleName();

    private static final GetHostInfoExecutor GET_HOST_INFO = new GetHostInfoExecutor();

    public static GetHostInfoExecutor getInstance() {
        return GET_HOST_INFO;
    }

    public interface Callback {
        void onLoaded(Host host);
    }

    private final Executor executor = Executors.newCachedThreadPool();

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void loadInfo(final int host_id) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Host host = null;
                try {
                    host = HelperFactory.getHelper().getHostDAO().getHostById(host_id);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (host != null) {
                    notifyLoaded(host);
                }

            }
        });
    }

    private void notifyLoaded(final Host host) {
        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onLoaded(host);
                Log.d(TAG, "notify UI");
            }
        });
    }
}

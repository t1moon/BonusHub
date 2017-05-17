package com.example.BonusHub.activity.executors;

import android.util.Log;

import com.example.BonusHub.activity.Ui;
import com.example.BonusHub.activity.fragment.EditFragment;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Timur on 04-May-17.
 */

public class EditHostInfoExecutor {

    private final static String TAG = EditHostInfoExecutor.class.getSimpleName();

    private static final EditHostInfoExecutor EDIT_HOST_INFO = new EditHostInfoExecutor();

    public static EditHostInfoExecutor getInstance() {
        return EDIT_HOST_INFO;
    }

    public interface Callback {
        void onEdited(int resultCode);
    }

    private final Executor executor = Executors.newCachedThreadPool();

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void editInfo(final int host_id, final Host host) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int result_code = EditFragment.RESULT_OK;
                if (host_id != -1) {
                    UpdateBuilder<Host, Integer> updateBuilder = null;
                    try {
                        updateBuilder = HelperFactory.getHelper().
                                getHostDAO().updateBuilder();
                        updateBuilder.where().eq("host_id", host_id);
                        updateBuilder.updateColumnValue("title", host.getTitle());
                        updateBuilder.updateColumnValue("description", host.getDescription());
                        updateBuilder.updateColumnValue("address", host.getAddress());
                        updateBuilder.updateColumnValue("time_open", host.getTime_open());
                        updateBuilder.updateColumnValue("time_close", host.getTime_close());
                        updateBuilder.update();

                    } catch (SQLException e) {
                       result_code = EditFragment.RESULT_FAIL;
                    }
                    notifyEdited(result_code);
                }

            }
        });
    }

    private void notifyEdited(final int resultCode) {
        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onEdited(resultCode);
                Log.d(TAG, "notify UI");
            }
        });
    }
}

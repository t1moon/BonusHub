package com.techpark.BonusHub.executor;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.techpark.BonusHub.db.HelperFactory;
import com.techpark.BonusHub.db.host.Host;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Timur on 04-May-17.
 */

public class DbExecutorService {
    private DbExecutorCallback callback;

    private final static String TAG = DbExecutorService.class.getSimpleName();

    private static final DbExecutorService DB_EXECUTOR_SERVICE = new DbExecutorService();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public static DbExecutorService getInstance() {
        return DB_EXECUTOR_SERVICE;
    }

    public void setCallback(DbExecutorCallback newCallback) {
        callback = newCallback;
    }

    private final Executor executor = Executors.newCachedThreadPool();

    public void loadInfo(final int host_id) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Host host = null;
                try {
                    host = HelperFactory.getHelper().getHostDAO().getHostById(host_id);
                } catch (final SQLException e) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
                if (host != null) {
                    final Map<String, Host> result = new HashMap<String, Host>();
                    result.put("host", host);
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(result);
                        }
                    });
                }

            }
        });
    }

    public void createHost(final Host host) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int host_id = -1;
                try {
                    host_id = HelperFactory.getHelper().getHostDAO().createHost(host);
                } catch (final SQLException e) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
                if (host_id != -1) {
                    final Map<String, Integer> result = new HashMap<String, Integer>();
                    result.put("host_id", host_id);
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSuccess(result);
                            }
                        }
                    });
                }
            }
        });
    }

    public void editInfo(final int host_id, final Host host) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
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

                    } catch (final SQLException e) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            final Map<String, Integer> result = new HashMap<String, Integer>();
                            result.put("result_code", 0);
                            callback.onSuccess(result);
                        }
                    });
                }
            }
        });
    }

    public void upload(final Context context, final int host_id, final Uri targetUri) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (host_id != -1) {
                    UpdateBuilder<Host, Integer> updateBuilder = null;
                    try {
                        updateBuilder = HelperFactory.getHelper().
                                getHostDAO().updateBuilder();
                        updateBuilder.where().eq(Host.HOST_ID_FIELD_NAME, host_id);
                        updateBuilder.updateColumnValue(Host.HOST_IMAGE_FIELD_NAME, targetUri.toString());
                        updateBuilder.update();
                    } catch (final SQLException e) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, String> result = new HashMap<String, String>();
                            result.put("image", targetUri.toString());
                            callback.onSuccess(result);
                        }
                    });
                }
            }
        });
    }

    public interface DbExecutorCallback {
        void onSuccess(Map<String, ?> result);

        void onError(Exception ex);

    }
}

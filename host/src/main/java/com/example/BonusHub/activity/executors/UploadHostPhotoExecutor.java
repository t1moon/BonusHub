package com.example.BonusHub.activity.executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;

import com.example.BonusHub.activity.Ui;
import com.example.BonusHub.activity.fragment.ProfileFragment;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Timur on 04-May-17.
 */

public class UploadHostPhotoExecutor {

    private final static String TAG = UploadHostPhotoExecutor.class.getSimpleName();

    private static final UploadHostPhotoExecutor UPLOAD_HOST_PHOTO= new UploadHostPhotoExecutor();

    public static UploadHostPhotoExecutor getInstance() {
        return UPLOAD_HOST_PHOTO;
    }

    public interface Callback {
        void onUploaded(int resultCode, BitmapDrawable bdrawable);
    }

    private final Executor executor = Executors.newCachedThreadPool();

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void upload(final Context context, final int host_id, final Uri targetUri) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int result_code = EditFragment.UPLOAD_RESULT_OK;
                if (host_id != -1) {
                    UpdateBuilder<Host, Integer> updateBuilder = null;
                    BitmapDrawable bdrawable = null;
                    try {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(targetUri));
                            bdrawable = new BitmapDrawable(context.getResources(), bitmap);
                        } catch (FileNotFoundException e) {
                            result_code = EditFragment.UPLOAD_RESULT_FILE_NOT_FOUND;
                        }

                        updateBuilder = HelperFactory.getHelper().
                                getHostDAO().updateBuilder();
                        updateBuilder.where().eq(Host.HOST_ID_FIELD_NAME, host_id);
                        updateBuilder.updateColumnValue(Host.HOST_IMAGE_FIELD_NAME, targetUri.toString());
                        updateBuilder.update();

                    } catch (SQLException e) {
                       result_code = EditFragment.UPLOAD_RESULT_FAIL;
                    }
                    notifyEdited(result_code, bdrawable);
                }

            }
        });
    }

    private void notifyEdited(final int resultCode, final BitmapDrawable bdrawable) {
        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onUploaded(resultCode, bdrawable);
                Log.d(TAG, "notify UI");
            }
        });
    }
}

package com.techpark.BonusHub.retrofit.editInfo;

import java.io.File;

/**
 * Created by Timur on 19-May-17.
 */

public class UploadPojo {
    private String path;
    private File file;


    public UploadPojo(String path, File file) {
        this.path = path;
        this.file = file;
    }
}

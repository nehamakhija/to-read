package com.example.neham.to_read;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

public class MyFileProvider extends FileProvider {

    public final static String TAG = "MyFileProvider";
    String filename;

    public MyFileProvider(String filename) {
        super();
        this.filename = filename;
    }

    public Uri getDatabaseURI(Context c) {

        File data = Environment.getDataDirectory();
        String dbName = filename;
        String currentDBPath = "//data//com.example.neham.to_read//files//" + dbName;
        File exportFile = new File(data, currentDBPath);
        Log.d(TAG, exportFile.exists() + "");
        return getFileUri(c, exportFile);
    }

    public Uri getFileUri(Context c, File f) {
        return getUriForFile(c, "com.example.neham.to_read.fileprovider", f);
    }

}

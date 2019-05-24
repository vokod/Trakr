package com.awolity.trakr.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class FileLoggingTree extends Timber.DebugTree {

    private static final String TAG = FileLoggingTree.class.getSimpleName();
    private static final String PATH = Environment.getExternalStorageDirectory() + "/Trakr";
    public FileLoggingTree() {
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {

        try {
            File direct = new File(PATH);

            if (!direct.exists()) {
                //noinspection ResultOfMethodCallIgnored
                direct.mkdir();
            }

            String fileNameTimeStamp = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.getDefault()).format(new Date());
            String logTimeStamp = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm:ss:SSS aaa",
                    Locale.getDefault()).format(new Date
                    ());

            String fileName = fileNameTimeStamp + ".html";

            File file = new File(PATH
                    + File.separator + fileName);

            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();

            if (file.exists()) {
                OutputStream fileOutputStream = new FileOutputStream(file, true);
                fileOutputStream.write(("<p style=\"background:lightgray;\"><strong style=\"background:lightblue;\">&nbsp&nbsp" + logTimeStamp + " :&nbsp&nbsp</strong>&nbsp&nbsp" + message + "</p>").getBytes());
                fileOutputStream.close();

            }
        } catch (Exception e) {
            Log.e(TAG, "Error while logging into file : " + e);
        }
    }
}

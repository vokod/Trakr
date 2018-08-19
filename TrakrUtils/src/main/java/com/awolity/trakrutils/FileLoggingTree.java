package com.awolity.trakrutils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

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

    private Context context;

    public FileLoggingTree(Context context) {
        this.context = context;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        try {
            File direct = new File(PATH);

            if (!direct.exists()) {
                direct.mkdir();
            }

            String fileNameTimeStamp = new SimpleDateFormat("yyyy-MM-dd_hh",
                    Locale.getDefault()).format(new Date());
            String logTimeStamp = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm:ss:SSS aaa",
                    Locale.getDefault()).format(new Date
                    ());

            String fileName = fileNameTimeStamp + ".html";

            File file = new File(PATH
                    + File.separator + fileName);

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

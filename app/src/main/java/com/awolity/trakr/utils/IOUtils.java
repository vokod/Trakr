package com.awolity.trakr.utils;

import android.os.Environment;

import java.io.File;

public class IOUtils {

    public static String getLegalizedFilename(String illegalFileName) {
        return illegalFileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkCreateFolder(String folder) {
        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}
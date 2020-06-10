package com.example.application.ui;

import android.content.Context;

import java.io.File;

public class ClearData {

    private Context context;
    public ClearData(Context context){
        this.context = context;
    }

    /**
     * *清除所有数据库(/data/data/包名/databases)
     */
    public void clearDatabases() {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * 只删除文件夹下的文件
     *
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

}

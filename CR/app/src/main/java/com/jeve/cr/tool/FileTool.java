package com.jeve.cr.tool;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created by zhangliang on 2017/12/13.
 */

public class FileTool {
    /**
     * 删除文件
     */
    public static Boolean deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                deleteFile(file1);
            }
        }
        return !file.exists();
    }

    /**
     * 关闭IO流
     */
    public static void close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

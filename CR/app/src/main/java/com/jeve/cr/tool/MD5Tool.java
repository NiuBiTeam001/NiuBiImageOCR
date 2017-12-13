package com.jeve.cr.tool;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import static com.umeng.commonsdk.statistics.common.DataHelper.toHexString;

/**
 * Created by zhangliang on 2017/12/13.
 */

public class MD5Tool {
    /**
     * 取得文件的md5值(32位)
     */
    public static String fileMd5(String fileName) {
        InputStream in = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new FileInputStream(fileName);
            byte[] buffer = new byte[1024 * 4];
            int numRead;
            while ((numRead = in.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            return toHexString(md5.digest());
        } catch (Exception e) {
            return "";
        } finally {
            FileTool.close(in);
        }
    }
}

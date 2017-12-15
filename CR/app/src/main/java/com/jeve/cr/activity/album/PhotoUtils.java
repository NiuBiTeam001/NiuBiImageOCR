package com.jeve.cr.activity.album;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.MediaStore;

import com.jeve.cr.CrApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * note: 用于获取设备上的照片
 * Created by zhangliang on 2017/12/14.
 */

public class PhotoUtils {

    public static ArrayList<File> getPhotoList() {
        ArrayList<File> photoList = new ArrayList<>();
        Cursor cursor = getCursor();
        if (cursor != null) {
            File file;
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//图片绝对路径
                file = new File(path);
                if (file.exists()) {
                    photoList.add(file);
                }
            }
        }
        sortPhotoList(photoList);
        return photoList;
    }

    /**
     * 将list的图片进行以修改时间进行排序，时间最近修改在前
     *
     * @param list
     * @return
     */
    private static void sortPhotoList(ArrayList<File> list) {
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                if (file1 == null || file2 == null) {
                    return 0;
                }
                return Long.valueOf(file2.lastModified()).compareTo(file1.lastModified());
            }
        });
    }

    @SuppressLint("Recycle")
    private static Cursor getCursor() {
        try {
            Cursor cursor = CrApplication.getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.DATA},
                    MediaStore.Images.Media.SIZE + ">=20480", /*过滤掉小于20kb的图片*/
                    null, MediaStore.Images.Media.DATE_ADDED);
            return cursor;
        } catch (Exception e) {
            return null;
        }
    }
}

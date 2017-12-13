package com.jeve.cr.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.Constant;
import com.jeve.cr.R;
import com.jeve.cr.activity.feedback.FeedbackActivity;
import com.jeve.cr.config.MainConfig;
import com.jeve.cr.tool.BitmapTool;
import com.jeve.cr.tool.FileTool;
import com.jeve.cr.tool.MD5Tool;
import com.jeve.cr.tool.OCRTool;
import com.jeve.cr.tool.UMTool;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import static android.R.attr.writePermission;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "zl---MainActivity---";
    private String[] permissionArray;
    private String originalPath;
    private Uri originalUri;
    private static final int CAMERA_REQUEST_CODE = 2;//调用相机请求码
    private static final int CAMERA_FLAG = 1;//标志从相机进入裁剪
    private String cameraPermission = "android.permission.CAMERA";
    private String writePermission = "android.permission.WRITE_EXTERNAL_STORAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        UMTool.getInstence().openDebug();
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            OCRTool.getInstence().OCRTest(Environment.getExternalStorageDirectory().getAbsolutePath() + "/img001.png",
                    new OCRTool.OcrCallBack() {
                        @Override
                        public void success(String str) {

                        }

                        @Override
                        public void error(String error) {

                        }
                    });
        } else {
            requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        ImageView camera_iv = (ImageView) findViewById(R.id.main_activity_camera);
        ImageView photo_iv = (ImageView) findViewById(R.id.main_activity_photo);
        Button feedback_btn = (Button) findViewById(R.id.main_activity_feedback);
        camera_iv.setOnClickListener(this);
        photo_iv.setOnClickListener(this);
        feedback_btn.setOnClickListener(this);
    }

    @Override
    public void requestSuccess(String permission) {
        super.requestSuccess(permission);
        OCRTool.getInstence().OCRTest(Environment.getExternalStorageDirectory().getAbsolutePath() + "/img001.png",
                new OCRTool.OcrCallBack() {
                    @Override
                    public void success(String str) {

                    }

                    @Override
                    public void error(String error) {

                    }
                });
    }


    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                result = rest == PackageManager.PERMISSION_GRANTED;
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_activity_camera:
                startCamera();
                break;
            case R.id.main_activity_photo:
                break;
            case R.id.main_activity_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 开启相机
     */
    private void startCamera() {
        boolean isCameraGranted = checkPermission(cameraPermission);
        Boolean isWriteGranted = checkPermission(writePermission);
        if (isCameraGranted && isWriteGranted) {
            originalPath = Constant.ORIGINAL_PATH + getString(R.string.app_name)+ "_" + getCurrentDate(System.currentTimeMillis()) + ".jpg";
            Log.d(TAG, "-----设置路径----   originalPath=" + originalPath);
            File originalDir = new File(Constant.ORIGINAL_PATH);
            if (!originalDir.exists()) {
                originalDir.mkdirs();
            }
            File originalFile = new File(originalPath);
            //确定只调用系统相机，而不调用其他三方相机
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String systemCameraPackageName = getSystemCameraPackageName(MainActivity.this);
            if (TextUtils.isEmpty(systemCameraPackageName)) {
                systemCameraPackageName = "com.android.camera";
            }
            Intent intent = this.getPackageManager()
                    .getLaunchIntentForPackage(systemCameraPackageName);
            if (intent != null) {
                cameraIntent.setPackage(systemCameraPackageName);
            }
            //7.0的StrictMode政策，使不能直接获取到uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                originalUri = FileProvider.getUriForFile(this, Constant.PACKAGE_NAME + ".provider", originalFile);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                originalUri = Uri.fromFile(originalFile);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, originalUri);
            cameraIntent.putExtra("return-data", false);//将结果不保存在onActivityResult方法参数的data中
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
//            overridePendingTransition(R.anim.bottom_in_anim, R.anim.top_out_anim);
            long currentTime = System.currentTimeMillis();
            MainConfig.getInstance().setTakePhotoTime(currentTime);
        } else {
            requestNeedPermissions();
        }
    }

    /**
     * 获取系统相机的包名
     *
     * @param context
     * @return
     */
    private String getSystemCameraPackageName(Activity context) {
        try {
            String systemCameraPackageName = "";
            List<PackageInfo> packages = context.getPackageManager()
                    .getInstalledPackages(0);
            for (int i = 0; i < packages.size(); i++) {
                try {
                    PackageInfo packageInfo = packages.get(i);
                    String strLabel = packageInfo.applicationInfo.loadLabel(
                            context.getPackageManager()).toString();
                    Log.d(TAG, "------------------" + strLabel);
                    if ("相机,照相机,照相,拍照,摄像,Camera,camera".contains(strLabel)) {
                        systemCameraPackageName = packageInfo.packageName;
                        Log.d(TAG, "+++++++++++++++++++" + systemCameraPackageName);
                        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(systemCameraPackageName)) {
                return systemCameraPackageName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前时间格式：yyyyMMdd_HHmmssSSS
     *
     * @param time
     * @return
     */
    private String getCurrentDate(long time) {
        String pattern = "yyyyMMdd_HHmmssSSS";
        Date date = new Date(time);
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

    /**
     * 申请需要的权限
     */
    private void requestNeedPermissions() {
        permissionArray = new String[]{cameraPermission, writePermission};
        requestPermission(this,permissionArray);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMTool.getInstence().appStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            Log.d(TAG, "拍照完成进入：onActivityResult");
            Log.d(TAG, "originalPath=" + originalPath);
            try {
                File originalFile = new File(originalPath);
                if (originalFile.length() > 0) {
                    Bitmap originalBitmap = BitmapTool.loadImage(originalPath,0);
                    if (originalBitmap == null) {
                        Toast.makeText(this, getString(R.string.main_activity_takephoto_tip), Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    int degree = BitmapTool.getPictureDegree(originalPath);
                    if (degree != 0) {
                        originalBitmap = BitmapTool.rotateBitmap(originalBitmap, degree);
                    }
                    BitmapTool.savePrimitiveImag(originalBitmap);
//                    Intent clipingIntent = new Intent(this, ClippingActivity.class);
//                    clipingIntent.setFlags(CAMERA_FLAG);
//                    startActivity(clipingIntent);
                    new DeleteSystemSamePhotoThread(originalPath).start();
                }
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.main_activity_takephoto_tip), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 删除系统相机保存的照片
     */
    private class DeleteSystemSamePhotoThread extends Thread {
        String deletePath;

        private DeleteSystemSamePhotoThread(String deletePath) {
            this.deletePath = deletePath;
        }

        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<File> files = checkTheTimePhoto(MainActivity.this);
            if (files.size() > 0) {
                int i = 0;
                for (File file : files) {
                    ++i;
                    String path = file.getPath();
                    String md5 = MD5Tool.fileMd5(path);
                    String md = MD5Tool.fileMd5(deletePath);
                    if (md5.equals(md)) {
                        if (!path.contains("Album")) {
                            FileTool.deleteFile(file);
                        }
                        break;
                    }
                }
            }
            refreshLocalPhoto(originalPath, MainActivity.this);
//            if (!MainConfig.getInstance().getCfgSaveOriginalPhoto()) {
//                FileTool.deleteFile(new File(deletePath));
//            }
        }
    }

    /**
     * 获取手机在某个时间之后改变的图片
     */
    public static ArrayList<File> checkTheTimePhoto(Context context) {
        ArrayList<File> fileList = new ArrayList<>();
        try {
            Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(imageUri, null, MediaStore.Images.Media.MIME_TYPE + " = ? or " +
                    MediaStore.Images.Media.MIME_TYPE + " = ? ", new String[]{"image/jpeg", "image/png"}, MediaStore
                    .Images.Media.DATE_MODIFIED);
            if (cursor != null) {

                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    if (path == null) {
                        continue;
                    }

                    File file = new File(path);

                    if (!file.exists()) {
                        continue;
                    }
                    long time = file.lastModified();
                    if (time >= MainConfig.getInstance().getTakePhotoTime()) {
                        fileList.add(file);
                    }

                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return fileList;
        }
        return fileList;
    }

    /**
     * 通知本地相册刷新
     */
    public static void refreshLocalPhoto(String imagePath, Context context) {

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(imagePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
}

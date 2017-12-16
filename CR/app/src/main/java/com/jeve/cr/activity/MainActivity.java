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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.Constant;
import com.jeve.cr.R;
import com.jeve.cr.activity.album.AlbumActivity;
import com.jeve.cr.activity.feedback.FeedbackActivity;
import com.jeve.cr.activity.imageEdit.ImageEditActivity;
import com.jeve.cr.config.MainConfig;
import com.jeve.cr.tool.BitmapTool;
import com.jeve.cr.tool.DeviceTool;
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

import static android.R.attr.breadCrumbShortTitle;
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
    private Boolean dealImage = false;

    private DrawerLayout drawer;
    private ImageView showimage_iv;
    private RelativeLayout select_again_re, edit_re, ocr_re;
    private TextView result_tv;
    private LinearLayout select_ll;
    private TextView explain;
    private ScrollView result_scroll;
    private String photoPath;//从相册选取的照片路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if (intent != null) {
            photoPath = intent.getStringExtra("path");
        }
        initViews();
        UMTool.getInstence().openDebug();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        ImageView camera_iv = (ImageView) findViewById(R.id.main_activity_camera);
        ImageView photo_iv = (ImageView) findViewById(R.id.main_activity_photo);
        TextView feedback = (TextView) findViewById(R.id.feedback);
        TextView version = (TextView) findViewById(R.id.version);
        showimage_iv = (ImageView) findViewById(R.id.showimage_iv);
        RelativeLayout drawer_re = (RelativeLayout) findViewById(R.id.drawer_re);
        select_again_re = (RelativeLayout) findViewById(R.id.select_again_re);
        edit_re = (RelativeLayout) findViewById(R.id.edit_re);
        ocr_re = (RelativeLayout) findViewById(R.id.ocr_re);
        result_tv = (TextView) findViewById(R.id.result_tv);
        explain = (TextView) findViewById(R.id.explain);
        select_ll = (LinearLayout) findViewById(R.id.select_ll);
        result_scroll = (ScrollView) findViewById(R.id.result_scroll);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        String versionContent = "v" + DeviceTool.getVersionName(this);
        version.setText(versionContent);
        camera_iv.setOnClickListener(this);
        photo_iv.setOnClickListener(this);
        drawer_re.setOnClickListener(this);
        feedback.setOnClickListener(this);
        select_again_re.setOnClickListener(this);
        edit_re.setOnClickListener(this);
        ocr_re.setOnClickListener(this);
    }

    @Override
    public void requestSuccess(String permission) {
        super.requestSuccess(permission);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_activity_camera:
                startCamera();
                break;
            case R.id.main_activity_photo:
                if (checkPermission(writePermission)) {
                    startActivity(new Intent(this, AlbumActivity.class));
                }else {
                    requestPermission(this,writePermission);
                }
                break;
            case R.id.drawer_re:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.select_again_re:
                showimage_iv.setVisibility(View.GONE);
                select_again_re.setVisibility(View.GONE);
                edit_re.setVisibility(View.GONE);
                ocr_re.setVisibility(View.GONE);
                result_tv.setVisibility(View.GONE);
                result_scroll.setVisibility(View.GONE);
                explain.setVisibility(View.VISIBLE);
                select_ll.setVisibility(View.VISIBLE);
                break;
            case R.id.edit_re:
                dealImage = true;
                startActivity(new Intent(this, ImageEditActivity.class));
                break;
            case R.id.ocr_re:
                OCRTool.getInstence().OCRTest(BitmapTool.PRIMITIVE_PATH, new OCRTool.OcrCallBack() {
                    @Override
                    public void success(String str) {
                        Log.d("LJW", "识别成功" + str);
                        result_tv.setText(str);
                        result_tv.setVisibility(View.VISIBLE);
                        result_scroll.setVisibility(View.VISIBLE);
                        showimage_iv.setVisibility(View.GONE);
                        edit_re.setVisibility(View.INVISIBLE);
                        ocr_re.setVisibility(View.INVISIBLE);
                        explain.setVisibility(View.GONE);
                        select_ll.setVisibility(View.GONE);
                    }

                    @Override
                    public void error(String error) {
                        Log.d("LJW", "识别失败" + error.toString());
                    }
                });
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
            originalPath = Constant.ORIGINAL_PATH + getString(R.string.app_name) + "_" + getCurrentDate(System
                    .currentTimeMillis()) + ".jpg";
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
                originalUri = FileProvider.getUriForFile(this, Constant.PACKAGE_NAME + ".fileProvider", originalFile);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                originalUri = Uri.fromFile(originalFile);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, originalUri);
            cameraIntent.putExtra("return-data", false);//将结果不保存在onActivityResult方法参数的data中
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            long currentTime = System.currentTimeMillis();
            MainConfig.getInstance().setTakePhotoTime(currentTime);
        } else {
            requestNeedPermissions();
        }
    }

    /**
     * 获取系统相机的包名
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
        requestPermission(this, permissionArray);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMTool.getInstence().appStart();
        if (dealImage) {
            dealImage = false;
            //更新照片
            Bitmap newBitmap = BitmapTool.loadImage(BitmapTool.PRIMITIVE_PATH, 0);
            showimage_iv.setImageBitmap(newBitmap);
        }
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
                    Bitmap originalBitmap = BitmapTool.loadImage(originalPath, DeviceTool.getWidthAndHeight(this)
                            .width);
                    if (originalBitmap == null) {
                        Toast.makeText(this, getString(R.string.main_activity_takephoto_tip), Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    int degree = BitmapTool.getPictureDegree(originalPath);
                    if (degree != 0) {
                        originalBitmap = BitmapTool.rotateBitmap(originalBitmap, degree);
                    }
                    originalBitmap = BitmapTool.scBitmap(originalBitmap, DeviceTool.getWidthAndHeight(this).width);
                    showimage_iv.setImageBitmap(originalBitmap);
                    showimage_iv.setVisibility(View.VISIBLE);

                    select_again_re.setVisibility(View.VISIBLE);
                    edit_re.setVisibility(View.VISIBLE);
                    ocr_re.setVisibility(View.VISIBLE);

                    BitmapTool.savePrimitiveImag(originalBitmap);
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

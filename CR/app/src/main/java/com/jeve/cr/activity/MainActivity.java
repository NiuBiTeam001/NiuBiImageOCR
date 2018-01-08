package com.jeve.cr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.Constant;
import com.jeve.cr.R;
import com.jeve.cr.activity.album.AlbumActivity;
import com.jeve.cr.activity.feedback.FeedbackActivity;
import com.jeve.cr.activity.imageEdit.ImageEditActivity;
import com.jeve.cr.activity.result.ResultActivity;
import com.jeve.cr.adapter.MainBackViewPagerAdapter;
import com.jeve.cr.bean.RecordOcrTotalTimes;
import com.jeve.cr.bean.SaveUs;
import com.jeve.cr.bean.UserRecord;
import com.jeve.cr.config.MainConfig;
import com.jeve.cr.tool.BitmapTool;
import com.jeve.cr.tool.DeviceTool;
import com.jeve.cr.tool.FileTool;
import com.jeve.cr.tool.MD5Tool;
import com.jeve.cr.tool.OCRTool;
import com.jeve.cr.tool.RecordOcrTotalTimesTool;
import com.jeve.cr.tool.UMTool;
import com.jeve.cr.tool.UserInitTool;
import com.jeve.cr.tool.UserSystemTool;
import com.jeve.cr.update.UpdateEditActivity;
import com.jeve.cr.update.UpdateManager;
import com.jeve.cr.youmi.UmiManager;

import net.youmi.android.nm.sp.SpotManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.R.id.list;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "zl---MainActivity---";
    private String originalPath;
    private static final int CAMERA_REQUEST_CODE = 2;//调用相机请求码
    private String cameraPermission = "android.permission.CAMERA";
    private String writePermission = "android.permission.WRITE_EXTERNAL_STORAGE";
    private final int RESULT_ACTIVITY_PHOTO = 0;
    private final int RESULT_ACTIVITY_DEAL = 3;
    private int orcModen;
    private final int CAMERA_REQUEST_PERMISSION_CODE = 5;
    private final int SD_REQUEST_PERMISSION_CODE = 6;
    private int allOcrCount = 0;
    private int photoSelectClick = 0;

    private DrawerLayout drawer;
    private ImageView showimage_iv;
    private RelativeLayout select_again_re, edit_re, ocr_re;
    private RelativeLayout copy_tip_re;
    private LinearLayout select_ll;
    private TextView main_ocr_count;
    private ViewPager back_viewpager;
    private LinearLayout main_count_ll;
    private RelativeLayout drawer_re;
    private RelativeLayout ad_re, get_free_re;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        //update
        new UpdateManager().checkUpdate(this);
        UMTool.getInstence().sendEvent(UMTool.Action.CR_APP_START);
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        ImageView camera_iv = (ImageView) findViewById(R.id.main_activity_camera);
        ImageView photo_iv = (ImageView) findViewById(R.id.main_activity_photo);
        TextView feedback = (TextView) findViewById(R.id.feedback);
//        TextView rate_us = (TextView) findViewById(R.id.rate_us);
        TextView version = (TextView) findViewById(R.id.version);
        TextView copy_tip_tv = (TextView) findViewById(R.id.copy_tip_tv);
        showimage_iv = (ImageView) findViewById(R.id.showimage_iv);
        ad_re = (RelativeLayout) findViewById(R.id.ad_re);
        get_free_re = (RelativeLayout) findViewById(R.id.get_free_re);
        drawer_re = (RelativeLayout) findViewById(R.id.drawer_re);
        select_again_re = (RelativeLayout) findViewById(R.id.select_again_re);
        edit_re = (RelativeLayout) findViewById(R.id.edit_re);
        ocr_re = (RelativeLayout) findViewById(R.id.ocr_re);
        copy_tip_re = (RelativeLayout) findViewById(R.id.copy_tip_re);
        main_ocr_count = (TextView) findViewById(R.id.main_ocr_count);
        select_ll = (LinearLayout) findViewById(R.id.select_ll);
        back_viewpager = (ViewPager) findViewById(R.id.back_viewpager);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        main_count_ll = (LinearLayout) findViewById(R.id.main_count_ll);
        String versionContent = "v" + DeviceTool.getVersionName(this);
        version.setText(versionContent);
        camera_iv.setOnClickListener(this);
        get_free_re.setOnClickListener(this);
        ad_re.setOnClickListener(this);
        photo_iv.setOnClickListener(this);
        drawer_re.setOnClickListener(this);
        feedback.setOnClickListener(this);
        select_again_re.setOnClickListener(this);
        edit_re.setOnClickListener(this);
        ocr_re.setOnClickListener(this);
        copy_tip_re.setOnClickListener(this);
//        TextView stopOcr = (TextView) findViewById(R.id.stop_scan_ocr);
//        stopOcr.setOnClickListener(this);
        TextView update = (TextView) findViewById(R.id.update);
        update.setOnClickListener(this);
        MainBackViewPagerAdapter mainBackViewPagerAdapter = new MainBackViewPagerAdapter(this);
        back_viewpager.setAdapter(mainBackViewPagerAdapter);
        back_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                orcModen = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (MainConfig.getInstance().getChangeModeTip()) {
            //提示用户可以自由复制
            copy_tip_tv.setText(getString(R.string.main_change_mode_tip));
            copy_tip_re.setVisibility(View.VISIBLE);
        }

        //获取用户使用次数
        UserInitTool.initUser(this, handler);
        //获取总使用数
        RecordOcrTotalTimesTool.getRecordOcrTotalTimes(new RecordOcrTotalTimesTool.RecordOcrTotalTimesListener() {
            @Override
            public void onRecordOcrTotalTimesListener(RecordOcrTotalTimes times) {
                allOcrCount = times.getTotalTimes();
            }
        });
    }

    @Override
    public void requestSuccess(int requestCode, List<String> permission) {
        super.requestSuccess(requestCode, permission);
        switch (requestCode) {
            case SD_REQUEST_PERMISSION_CODE:
                //相册
                UMTool.getInstence().sendEvent(UMTool.Action.CR_PHOTO_CLICK);
                startActivityForResult(new Intent(this, AlbumActivity.class), RESULT_ACTIVITY_PHOTO);
                break;
            case CAMERA_REQUEST_PERMISSION_CODE:
                //相机
                UMTool.getInstence().sendEvent(UMTool.Action.CR_CAMERA_CLICK);
                startCamera();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_activity_camera:
                UMTool.getInstence().sendEvent(UMTool.Action.CR_CAMERA_CLICK);
                if (!MainConfig.getInstance().getPhotoSelectTip()) {
                    MainConfig.getInstance().setPhotoSelectTip(true);
                    showPhotoSelectTipPop();
                    photoSelectClick = 4;
                    return;
                }
                startCamera();
                break;
            case R.id.main_activity_photo:
                if (!MainConfig.getInstance().getPhotoSelectTip()) {
                    MainConfig.getInstance().setPhotoSelectTip(true);
                    showPhotoSelectTipPop();
                    photoSelectClick = 5;
                    return;
                }
                if (checkPermission(writePermission)) {
                    UMTool.getInstence().sendEvent(UMTool.Action.CR_PHOTO_CLICK);
                    startActivityForResult(new Intent(this, AlbumActivity.class), RESULT_ACTIVITY_PHOTO);
                } else {
                    requestPermission(this, writePermission, SD_REQUEST_PERMISSION_CODE);
                }
                break;
            case R.id.drawer_re:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.feedback:
                UMTool.getInstence().sendEvent(UMTool.Action.CR_FEEDBACK_CLICK);
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            /*case R.id.rate_us:
                ShareTool.goToMarket();
                break;*/
            case R.id.select_again_re:
                UMTool.getInstence().sendEvent(UMTool.Action.CR_SELECT_AGAIN);
                showimage_iv.setVisibility(View.GONE);
                select_again_re.setVisibility(View.GONE);
                edit_re.setVisibility(View.GONE);
                ocr_re.setVisibility(View.GONE);
                main_count_ll.setVisibility(View.VISIBLE);
                select_ll.setVisibility(View.VISIBLE);
                back_viewpager.setVisibility(View.VISIBLE);
                ad_re.setVisibility(View.VISIBLE);
                get_free_re.setVisibility(View.VISIBLE);
                break;
            case R.id.edit_re:
                UMTool.getInstence().sendEvent(UMTool.Action.CR_DEAL_CLICK);
                startActivityForResult(new Intent(this, ImageEditActivity.class), RESULT_ACTIVITY_DEAL);
                break;
            case R.id.ocr_re:
                String OBJECT_ID = "19db0d160b";
                BmobQuery<SaveUs> query = new BmobQuery<>();
                query.getObject(OBJECT_ID, new QueryListener<SaveUs>() {
                    @Override
                    public void done(SaveUs saveUs, BmobException e) {
                        //注意saveUs可能为null
                        if (saveUs != null && saveUs.getStop()) {
                            Toast.makeText(MainActivity.this, getString(R.string.main_all_stop), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!DeviceTool.isNetworkConnected(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, getString(R.string.main_net_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (allOcrCount >= 12000) {//用于测试。发布的第一个月要修改
                            Toast.makeText(MainActivity.this, getString(R.string.main_all_count), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (MainConfig.getInstance().getUserLeaveOcrTimes() == 0) {
                            //没有次数,叫用户看广告赚取次数
                            Toast.makeText(MainActivity.this, getString(R.string.main_ad_watch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //获取网络次数
                        UserSystemTool.getInstance().updateUserTimes(-1, new UserSystemTool.UserRecordUpdateListener() {
                            @Override
                            public void onUserRecordUpdateListener(int respondCode) {
                                //总次数加一
                                RecordOcrTotalTimesTool.updateTotalTimes(1);
                                //用户次数减一
                                MainConfig.getInstance().setUserLeaveOcrTimes(MainConfig.getInstance().getUserLeaveOcrTimes() - 1);
                                //设置用户次数
                                setOcrCount(MainConfig.getInstance().getUserLeaveOcrTimes());
                                if (respondCode == UserSystemTool.SUCCESS) {
                                    UMTool.getInstence().sendEvent(UMTool.Action.CR_CR_CLICK);
                                    if (orcModen == 0) {
                                        UMTool.getInstence().sendEvent(UMTool.Action.CR_TEXT_CR);
                                        //文字识别
                                        OCRTool.getInstence().OCRTest(BitmapTool.PRIMITIVE_PATH, new OCRTool.OcrCallBack() {
                                            @Override
                                            public void success(String str) {
                                                Log.d("LJW", "识别成功" + str);
                                                if (TextUtils.isEmpty(str)) {
                                                    UMTool.getInstence().sendEvent(UMTool.Action.CR_CR_ERROR);
                                                    handler.sendEmptyMessage(0);
                                                    return;
                                                }
                                                UMTool.getInstence().sendEvent(UMTool.Action.CR_CR_SUCCESS);
                                                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                                intent.putExtra("result", str);
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void error(String error) {
                                                UMTool.getInstence().sendEvent(UMTool.Action.CR_CR_ERROR);
                                                handler.sendEmptyMessage(1);
                                            }
                                        });
                                    } else if (orcModen == 1) {
                                        UMTool.getInstence().sendEvent(UMTool.Action.CR_BANK_CR);
                                        //银行卡识别
                                        OCRTool.getInstence().OCRBankCard(BitmapTool.PRIMITIVE_PATH, new OCRTool
                                                .OcrBankCallBack() {
                                            @Override
                                            public void success(String carNum, String bankName) {
                                                Log.d("LJW", "识别成功bank" + carNum + " - " + bankName);
                                                if (TextUtils.isEmpty(carNum)) {
                                                    UMTool.getInstence().sendEvent(UMTool.Action.CR_CR_ERROR);
                                                    handler.sendEmptyMessage(0);
                                                    return;
                                                }
                                                UMTool.getInstence().sendEvent(UMTool.Action.CR_CR_SUCCESS);
                                                StringBuffer stringBuffer = new StringBuffer();
                                                stringBuffer.append(getString(R.string.main_bank_num));
                                                stringBuffer.append(carNum);
                                                stringBuffer.append("\n");
                                                stringBuffer.append(getString(R.string.main_bank_bank));
                                                stringBuffer.append(bankName);
                                                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                                intent.putExtra("result", stringBuffer.toString());
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void error(String error) {
                                                UMTool.getInstence().sendEvent(UMTool.Action.CR_CR_ERROR);
                                                handler.sendEmptyMessage(0);
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.main_net_error), Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.copy_tip_re:
                MainConfig.getInstance().setChangeModeTip(false);
                copy_tip_re.setVisibility(View.GONE);
                break;
            case R.id.get_free_re:
                //免费获取次数
                UserSystemTool.getInstance().queryUser(new UserSystemTool.UserRecordQueryListener() {
                    @Override
                    public void onUserRecordQueryLister(UserRecord record) {
                        if (record.getTodayGetTime()) {
                            Toast.makeText(MainActivity.this, getString(R.string.get_ocr_times), Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            if (record.getUseTimes() > 0) {
                                Toast.makeText(MainActivity.this, getString(R.string.get_ocr_time_tip), Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                            UserSystemTool.getInstance().updateUserTimes(2, new UserSystemTool
                                    .UserRecordUpdateListener() {
                                @Override
                                public void onUserRecordUpdateListener(int respondCode) {
                                    if (respondCode == UserSystemTool.SUCCESS) {
                                        //成功了，显示次数
                                        setOcrCount(2);
                                        UserSystemTool.getInstance().updateUserIsGetTimes(true, new UserSystemTool
                                                .UserRecordUpdateListener() {
                                            @Override
                                            public void onUserRecordUpdateListener(int respondCode) {
                                            }
                                        });
                                    } else {
                                        //失败给出提示
                                        Toast.makeText(MainActivity.this, getString(R.string.feedback_send_failed),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.ad_re:
                UmiManager.showSpot(this,handler);
                break;
//            case R.id.stop_scan_ocr:
//                stopOcrScan();
//                break;
            case R.id.update:
                startActivity(new Intent(this, UpdateEditActivity.class));
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
            Uri originalUri;
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
                    if ("相机,照相机,照相,拍照,摄像,Camera,camera".contains(strLabel)) {
                        systemCameraPackageName = packageInfo.packageName;
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
        String[] permissionArray = new String[]{cameraPermission, writePermission};
        requestPermission(this, permissionArray, CAMERA_REQUEST_PERMISSION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            UMTool.getInstence().sendEvent(UMTool.Action.CR_CAMERA_SUCCESS);
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
                    originalBitmap = BitmapTool.scBitmap(originalBitmap, showimage_iv);
                    showimage_iv.setImageBitmap(originalBitmap);
                    showimage_iv.setVisibility(View.VISIBLE);
                    back_viewpager.setVisibility(View.GONE);

                    select_again_re.setVisibility(View.VISIBLE);
                    edit_re.setVisibility(View.VISIBLE);
                    ocr_re.setVisibility(View.VISIBLE);
                    main_count_ll.setVisibility(View.GONE);
                    select_ll.setVisibility(View.GONE);
                    ad_re.setVisibility(View.GONE);
                    get_free_re.setVisibility(View.GONE);

                    BitmapTool.savePrimitiveImag(originalBitmap);
                    new DeleteSystemSamePhotoThread(originalPath).start();
                }
            } catch (Exception e) {
                UMTool.getInstence().sendEvent(UMTool.Action.CR_CAMERA_ERROR);
                Toast.makeText(this, getString(R.string.main_activity_takephoto_tip), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RESULT_ACTIVITY_PHOTO) {
            //相册
            if (data == null)
                return;
            String back = data.getStringExtra("success");
            if (TextUtils.equals("success", back)) {
                UMTool.getInstence().sendEvent(UMTool.Action.CR_PHOTO_SUCCESS);
                Bitmap originalBitmap = BitmapTool.loadImage(BitmapTool.PRIMITIVE_PATH, 0);
                originalBitmap = BitmapTool.scBitmap(originalBitmap, showimage_iv);
                showimage_iv.setImageBitmap(originalBitmap);
                showimage_iv.setVisibility(View.VISIBLE);
                select_ll.setVisibility(View.GONE);
                back_viewpager.setVisibility(View.GONE);

                select_again_re.setVisibility(View.VISIBLE);
                edit_re.setVisibility(View.VISIBLE);
                ocr_re.setVisibility(View.VISIBLE);
                main_count_ll.setVisibility(View.GONE);
                ad_re.setVisibility(View.GONE);
                get_free_re.setVisibility(View.GONE);
                BitmapTool.savePrimitiveImag(originalBitmap);
            }
        } else if (requestCode == RESULT_ACTIVITY_DEAL) {
            if (data == null)
                return;
            String back = data.getStringExtra("success");
            if (TextUtils.equals("success", back)) {
                UMTool.getInstence().sendEvent(UMTool.Action.CR_DEAL_SUCCESS);
                //更新照片
                Bitmap newBitmap = BitmapTool.loadImage(BitmapTool.PRIMITIVE_PATH, 0);
                showimage_iv.setImageBitmap(newBitmap);
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

    /**
     * 双击back退出
     */
    private long time;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - time > 2000) {
                time = System.currentTimeMillis();
                Toast.makeText(MainActivity.this, getString(R.string.main_out_tip), Toast.LENGTH_SHORT).show();
            } else {
                finish();
                return false;
            }
        }
        return false;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(MainActivity.this, getString(R.string.ocr_error), Toast.LENGTH_SHORT)
                        .show();
            } else if (msg.what == 1) {
                Toast.makeText(MainActivity.this, getString(R.string.ocr_error2), Toast.LENGTH_SHORT)
                        .show();
            } else if (msg.what == 2) {
                setOcrCount((Integer) msg.obj);
            } else if (msg.what == 3) {
                setOcrCount(MainConfig.getInstance().getUserLeaveOcrTimes());
                Toast.makeText(MainActivity.this, getString(R.string.main_net_error), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 4) {
                //启动相机
                startCamera();
            } else if (msg.what == 5) {
                //启动相册
                if (checkPermission(writePermission)) {
                    UMTool.getInstence().sendEvent(UMTool.Action.CR_PHOTO_CLICK);
                    startActivityForResult(new Intent(MainActivity.this, AlbumActivity.class), RESULT_ACTIVITY_PHOTO);
                } else {
                    requestPermission(MainActivity.this, writePermission, SD_REQUEST_PERMISSION_CODE);
                }
            }else if (msg.what == 6){
                setOcrCount((Integer) msg.obj);
            }
        }
    };

    /**
     * 设置识别次数
     *
     * @param ocrCount 次数
     */
    private void setOcrCount(int ocrCount) {
        main_ocr_count.setText(ocrCount + "");
    }

    /**
     * 拯救措施
     * 设置是否中止
     */
    private void stopOcrScan() {
        //TODO 需要正式隐藏到
        String OBJECT_ID = "19db0d160b";
        SaveUs saveUs = new SaveUs();
        saveUs.setStop(true);
        saveUs.update(OBJECT_ID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 获取是否中止
     *
     * @return
     */
    private SaveUs getOcrScan() {
        String OBJECT_ID = "19db0d160b";
        BmobQuery<SaveUs> query = new BmobQuery<>();
        final ArrayList<SaveUs> list = new ArrayList<>();
        query.getObject(OBJECT_ID, new QueryListener<SaveUs>() {
            @Override
            public void done(SaveUs saveUs, BmobException e) {
                //注意saveUs可能为null
                list.add(saveUs);
            }
        });
        return list.get(0);
    }

    @Override
    public void onBackPressed() {
        if (SpotManager.getInstance(this).isSpotShowing()) {
            UmiManager.hideSpot(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmiManager.spotOnPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UmiManager.spotOnStop(this);
    }

    //照片提示说明
    private void showPhotoSelectTipPop() {
        // TODO: 2016/5/17 构建一个popupwindow的布局
        View popupView = MainActivity.this.getLayoutInflater().inflate(R.layout.popwindowlayout, null);
        Point point = DeviceTool.getScreenSize(this);
        final PopupWindow window = new PopupWindow(popupView, (int) (point.x / 4.0f * 3), (int) (point.y / 4.0f * 3));
        // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
        window.setOutsideTouchable(false);
        // TODO：更新popupwindow的状态
        window.update();
        // TODO: 2016/5/17 以下拉的方式显示，并且可以设置显示的位置
        int setX = (int) ((point.x - (point.x / 4.0f * 3)) / 2);
        window.showAsDropDown(drawer_re, setX, 20);
        TextView close = popupView.findViewById(R.id.pop_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.sendEmptyMessage(photoSelectClick);
                window.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UmiManager.spotOnDestroy(this);
        UmiManager.spotExit(this);
    }
}

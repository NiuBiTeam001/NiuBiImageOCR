package com.jeve.cr.activity.feedback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.R;
import com.jeve.cr.tool.DeviceTool;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "zl--FeedbackActivity--";
    private EditText suggestion_et;
    private ProgressBar progressBar;
    private RelativeLayout send_re;
    private EditText type_et;
    private PopupWindow popupWindow;
    private RelativeLayout title_re;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initViews();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        RelativeLayout back_re = (RelativeLayout) findViewById(R.id.feedback_back_re);
        title_re = (RelativeLayout) findViewById(R.id.feedback_actionbar);
        type_et = (EditText) findViewById(R.id.request_type_et);
        send_re = (RelativeLayout) findViewById(R.id.feedback_send_re);
        suggestion_et = (EditText) findViewById(R.id.feedback_suggestion_et);
        progressBar = (ProgressBar) findViewById(R.id.feedback_progressbar);
        back_re.setOnClickListener(this);
        send_re.setOnClickListener(this);
        type_et.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feedback_back_re:
                finish();
                break;
            case R.id.request_type_et:
                showPopWindow(title_re);
                break;
            case R.id.feedback_other:
                type_et.setText(getString(R.string.feedback_tag_others));
                popupWindow.dismiss();
                break;
            case R.id.feedback_bug:
                type_et.setText(getString(R.string.feedback_tag_bug));
                popupWindow.dismiss();
                break;
            case R.id.feedback_language:
                type_et.setText(getString(R.string.feedback_tag_language));
                popupWindow.dismiss();
                break;
            case R.id.feedback_ui:
                type_et.setText(getString(R.string.feedback_tag_ui));
                popupWindow.dismiss();
                break;
            case R.id.feedback_send_re:
                String suggestion = suggestion_et.getText().toString();
                if (TextUtils.isEmpty(suggestion)) {
                    Toast.makeText(this, getString(R.string.feedback_send_tip), Toast.LENGTH_SHORT).show();
                    break;
                }
                send(suggestion);
                break;
        }
    }

    /**
     * 提交到BMob服务器
     *
     * @param suggestion 提交的建议
     */
    private void send(String suggestion) {
        progressBar.setVisibility(View.VISIBLE);
        noOperate();
        Feedback content = new Feedback();
        if (type_et.getText().toString().isEmpty()) {
            content.setTag(getString(R.string.feedback_tag_others));
        } else {
            content.setTag(type_et.getText().toString());
        }
        content.setContent(TextUtils.isEmpty(suggestion) ? "" : suggestion);
        content.setDeviceInfo(DeviceTool.getDeviceInfo());
        content.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                progressBar.setVisibility(View.GONE);
                operate();
                Log.d(TAG, "提交返回的字符串" + s);
                if (e != null) {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.feedback_send_failed), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                } else {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.feedback_send_success), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    /**
     * 提交过程中，不可操作
     */
    private void noOperate() {
        suggestion_et.setEnabled(false);
        suggestion_et.setFocusable(false);
        type_et.setClickable(false);
        send_re.setClickable(false);
    }

    /**
     * 可操作
     */
    private void operate() {
        suggestion_et.setEnabled(true);
        suggestion_et.setFocusable(true);
        type_et.setClickable(false);
        send_re.setClickable(true);
    }

    private void showPopWindow(View view) {
        closeKeyboard();
        View popLayout = LayoutInflater.from(this).inflate(R.layout.feedback_pop_item_layout, null);
        CheckedTextView others = popLayout.findViewById(R.id.feedback_other);
        CheckedTextView bug = popLayout.findViewById(R.id.feedback_bug);
        CheckedTextView language = popLayout.findViewById(R.id.feedback_language);
        CheckedTextView ui = popLayout.findViewById(R.id.feedback_ui);

        CheckedTextView[] checkedTextView = new CheckedTextView[]{others, bug, language, ui};
        String chooseType = type_et.getText().toString();
        if (!TextUtils.isEmpty(chooseType)) {
            for (CheckedTextView aCheckedTextView : checkedTextView) {
                if (chooseType.equals(aCheckedTextView.getText().toString())) {
                    aCheckedTextView.setChecked(true);
                    aCheckedTextView.setTextColor(getResources().getColor(R.color.text_color3));
                }
            }
        }

        others.setOnClickListener(this);
        bug.setOnClickListener(this);
        language.setOnClickListener(this);
        ui.setOnClickListener(this);

        LinearLayout popLayout_ll = popLayout.findViewById(R.id.pop_layout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) popLayout_ll.getLayoutParams();
        layoutParams.width = DeviceTool.getScreenSize(this).x - DeviceTool.dip2px(32);
        popLayout_ll.setLayoutParams(layoutParams);

        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;//设置阴影透明度
        getWindow().setAttributes(lp);
        popupWindow = new PopupWindow(popLayout, width, height, true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        popupWindow.setTouchable(true);
        Bitmap bitmap = null;
        popupWindow.setBackgroundDrawable(new BitmapDrawable(null, bitmap));
        popupWindow.showAsDropDown(view, DeviceTool.dip2px(16), DeviceTool.dip2px(8));
    }

    /**
     * 隐藏软键盘
     */
    private void closeKeyboard() {
        try {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager.isActive()) {
                IBinder windowToken = this.getCurrentFocus().getWindowToken();
                if (windowToken != null) {
                    manager.hideSoftInputFromWindow(windowToken, 2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

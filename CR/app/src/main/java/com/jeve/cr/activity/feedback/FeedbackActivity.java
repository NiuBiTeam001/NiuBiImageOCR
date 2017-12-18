package com.jeve.cr.activity.feedback;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.R;
import com.jeve.cr.activity.imageEdit.ImageEditActivity;
import com.jeve.cr.tool.DeviceTool;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "zl--FeedbackActivity--";
    private EditText suggestion_et;
    private EditText bug_et;
    private ProgressBar progressBar;
    private RelativeLayout send_re;

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
        send_re = (RelativeLayout) findViewById(R.id.feedback_send_re);
        suggestion_et = (EditText) findViewById(R.id.feedback_suggestion_et);
        progressBar = (ProgressBar) findViewById(R.id.feedback_progressbar);
        bug_et = (EditText) findViewById(R.id.feedback_bug_et);
        back_re.setOnClickListener(this);
        send_re.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feedback_back_re:
                finish();
                break;
            case R.id.feedback_send_re:
                String suggestion = suggestion_et.getText().toString();
                String bug = bug_et.getText().toString();
                if (TextUtils.isEmpty(suggestion) && TextUtils.isEmpty(bug)) {
                    Toast.makeText(this, getString(R.string.feedback_send_tip), Toast.LENGTH_SHORT).show();
                    break;
                }
                send(suggestion, bug);
                break;
        }
    }

    /**
     * 提交到BMob服务器
     *
     * @param suggestion 提交的建议
     * @param bug        提交的问题
     */
    private void send(String suggestion, String bug) {
        progressBar.setVisibility(View.VISIBLE);
        noOperate();
        Feedback content = new Feedback();
        content.setSuggestion(TextUtils.isEmpty(suggestion) ? "" : suggestion);
        content.setBug(TextUtils.isEmpty(bug) ? "" : bug);
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
                }
                finish();
            }
        });
    }

    /**
     * 提交过程中，不可操作
     */
    private void noOperate() {
        suggestion_et.setEnabled(false);
        suggestion_et.setFocusable(false);
        bug_et.setEnabled(false);
        bug_et.setFocusable(false);
        send_re.setClickable(false);
    }

    /**
     * 可操作
     */
    private void operate() {
        suggestion_et.setEnabled(true);
        suggestion_et.setFocusable(true);
        bug_et.setEnabled(true);
        bug_et.setFocusable(true);
        send_re.setClickable(true);
    }

    private void showPopWindow(){
        closeKeyboard();
        View view = LayoutInflater.from(this).inflate(R.layout.feedback_pop_item_layout,null);
        CheckedTextView others = view.findViewById(R.id.feedback_other);
        CheckedTextView bug = view.findViewById(R.id.feedback_bug);
        CheckedTextView language = view.findViewById(R.id.feedback_language);
        CheckedTextView ui = view.findViewById(R.id.feedback_ui);

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

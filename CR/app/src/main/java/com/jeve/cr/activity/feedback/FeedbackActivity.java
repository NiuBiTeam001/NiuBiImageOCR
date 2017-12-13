package com.jeve.cr.activity.feedback;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.R;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

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
        EditText suggestion_et = (EditText) findViewById(R.id.feedback_suggestion_et);
        EditText bug_et = (EditText) findViewById(R.id.feedback_bug_et);
        back_re.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}

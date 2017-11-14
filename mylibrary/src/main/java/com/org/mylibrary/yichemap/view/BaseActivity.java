package com.org.mylibrary.yichemap.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.org.mylibrary.R;

/**
 * Created by Administrator on 2017/11/10/010.
 */

public class BaseActivity extends AppCompatActivity {
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        //progressDialog.setContentView(R.layout.popwindow);
    }

    //展示对话框
    public void showDialog() {
        progressDialog.show();
    }

    //隐藏对话框
    public void dismissDialog() {
        progressDialog.dismiss();
    }
}

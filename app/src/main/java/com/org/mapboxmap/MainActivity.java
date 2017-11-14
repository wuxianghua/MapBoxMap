package com.org.mapboxmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.org.mylibrary.yichemap.view.FindCarNativeActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //地图容器
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                FindCarNativeActivity.navigatorThis(this);
        }
    }
}

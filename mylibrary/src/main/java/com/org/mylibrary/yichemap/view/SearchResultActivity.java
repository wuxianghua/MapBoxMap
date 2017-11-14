package com.org.mylibrary.yichemap.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.org.mylibrary.R;
import com.org.mylibrary.yichemap.mode.CarParkingInfos;
import com.org.mylibrary.yichemap.utils.SharedPreferenceUtil;
import com.palmap.core.util.UtilsKt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/11/011.
 */

public class SearchResultActivity extends Activity {

    //查询车位
    private EditText lookForCar;
    //车位源数据
    private String parkData;
    //车位数据
    private CarParkingInfos carParkingInfos;
    private ListView listView;
    //车位名称
    private List<String> parkList;
    private Gson gson;
    //展示一键寻车信息
    private LinearLayout showSaveParkInfo;
    private TextView showParkInfo;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initView();
        initData();
    }

    String parkName;
    String parkTime;
    //初始化数据
    private void initData() {
        gson = new Gson();
        parkList = new ArrayList<>();
        parkData = UtilsKt.loadFromAsset(this, "parkData.json");
        carParkingInfos = gson.fromJson(parkData, CarParkingInfos.class);
        parkName = SharedPreferenceUtil.getValue(this,"parkinfo","parkName","");
        parkTime = SharedPreferenceUtil.getValue(this,"parkinfo","parkTime","");
        if (parkName == null || parkTime == null) {
            showSaveParkInfo.setVisibility(View.GONE);
        }
        showParkInfo.setText(parkTime+" 停于 "+ parkName);
        arrayAdapter = new ArrayAdapter(this,R.layout.simple_list_item_1,parkList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String carNum = parkList.get(position);
                Intent intent = new Intent();
                intent.putExtra("carNum",carNum);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        lookForCar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    listView.setVisibility(View.GONE);
                    showSaveParkInfo.setVisibility(View.VISIBLE);
                }else {
                    listView.setVisibility(View.VISIBLE);
                    showSaveParkInfo.setVisibility(View.GONE);
                    String searchContent = lookForCar.getText().toString();
                    if (parkList.size() != 0) {
                        parkList.clear();
                    }
                    if(searchContent!=null||searchContent!="") {

                        int length = carParkingInfos.carportInfos.size();
                        for (int i = 0; i < length; i++) {
                            String parkName = carParkingInfos.carportInfos.get(i).carport;
                            if (parkName.contains(searchContent)) {
                                parkList.add(parkName);
                            }
                        }
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initView() {
        lookForCar = (EditText) findViewById(R.id.edt_look_car);
        listView = (ListView) findViewById(R.id.show_search_result);
        showSaveParkInfo = (LinearLayout) findViewById(R.id.show_save_park_info);
        showParkInfo = (TextView) findViewById(R.id.show_park_info);
    }

    public void lookForCar(View view) {
        String carNum = lookForCar.getText().toString().trim();
        Intent intent = new Intent();
        intent.putExtra("carNum",carNum);
        setResult(RESULT_OK,intent);
        finish();
    }
    //一键寻车
    public void onKeyFindCar(View view) {
        Intent intent = new Intent();
        intent.putExtra("carNum",parkName);
        intent.putExtra("onKey",true);
        setResult(RESULT_OK,intent);
        finish();
    }
}

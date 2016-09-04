package com.kamin.dr.cgmblelibaryuse;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.kamin.cgmblelib.ble.BleLibary;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends BleLibary {
    //Debug
    private static final String TAG = MainActivity.class.getName();
    @InjectView(R.id.btnOpenBle)
    Button btnOpenBle;
    @InjectView(R.id.btnCloseBle)
    Button btnCloseBle;
    @InjectView(R.id.btnScan)
    Button btnScan;
    @InjectView(R.id.tvResult)
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "CGM--in onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

    }

    @OnClick({R.id.btnOpenBle, R.id.btnCloseBle, R.id.btnScan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOpenBle:
                Log.i(TAG, "CGM--in btnOpenBle OnClick()");
                super.openBle(true);
                break;
            case R.id.btnCloseBle:
                Log.i(TAG, "CGM--in btnCloseBle OnClick()");
                super.openBle(false);
                break;
            case R.id.btnScan:
                Log.i(TAG, "CGM--in btnScan OnClick()");
                super.scanBleDevice(true);
                break;
        }
    }

    @Override
    public void showDeviceMapList(List<Map<String, Object>> deviceMapList) {
        Log.i(TAG, "CGM--in showDeviceMapList()");
        if(deviceMapList!=null) {
            Map<String, Object> deviceMap = deviceMapList.get(0);
            tvResult.setText(deviceMap.get("name").toString());
        }
    }
}

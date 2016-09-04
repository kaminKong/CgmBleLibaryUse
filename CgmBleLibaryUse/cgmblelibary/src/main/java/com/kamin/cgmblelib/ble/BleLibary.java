package com.kamin.cgmblelib.ble;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BleLibary extends AppCompatActivity implements BleInterface {
    //Debug
    private static final String TAG = BleLibary.class.getName();
    private static final int REQUEST_ENABLE_BT = 1;
    private Context mainContext=this;
    private BleService mBleService;
    private boolean mIsBind;
    private List<Map<String, Object>> deviceMapList;
    private static final long OPENBLE_PERIOD = 12 * 1000; // Stop scanning after 10 seconds.

    public abstract void showDeviceMapList(List<Map<String, Object>> deviceMapList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "CGM--BleLibary.onCreate()");
        super.onCreate(savedInstanceState);
        if(!isSupportBle())//是否支持蓝牙ble，如果不支持结束应用
        {
            Toast.makeText(mainContext, "Bluetooth BLE is not supported.", Toast.LENGTH_LONG).show();
            ((Activity) mainContext).finish();
        }
        this.doBindService();//绑定服务
        registerReceiver(bleReceiver, makeIntentFilter());//注册广播
        deviceMapList=new ArrayList<>();
    }
    /**
     * Check for your device to support Ble
     *
     * @return true is support    false is not support
     */
    public boolean isSupportBle() {
        Log.i(TAG, "CGM--isSupportBle()");
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }
    /**
     * 绑定服务
     */
    private void doBindService() {
        Log.i(TAG, "CGM--BleLibary.doBindService()");
        Intent serviceIntent = new Intent(this, BleService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() { // Code to manage Service lifecycle.
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "CGM--BleLibary.onServiceConnected()");
            mBleService = ((BleService.LocalBinder) service).getService();
            if (!mBleService.initialize()) {
                Toast.makeText(BleLibary.this, "Bluetooth  is not supported", Toast.LENGTH_SHORT).show();
                ((Activity) mainContext).finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBleService = null;
            mIsBind = false;
        }
    };

    private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "CGM--onReceive()");
            if (intent.getAction().equals(BleService.ACTION_BLUETOOTH_DEVICE)) {
                String tmpDevName = intent.getStringExtra("name");
                String tmpDevAddress = intent.getStringExtra("address");
                Log.i(TAG, "CGM--name: " + tmpDevName + ", address: " + tmpDevAddress);
                HashMap<String, Object> deviceMap = new HashMap<>();
                deviceMap.put("name", tmpDevName);
                deviceMap.put("address", tmpDevAddress);
                deviceMap.put("isConnect", false);
                deviceMapList.add(deviceMap);
                showDeviceMapList(deviceMapList);
            }
//            else if (intent.getAction().equals(BleService.ACTION_GATT_CONNECTED)) {
//                deviceList.get(0).put("isConnect", true);
//                deviceAdapter.notifyDataSetChanged();
//                dismissDialog();
//            } else if (intent.getAction().equals(BleService.ACTION_GATT_DISCONNECTED)) {
//                deviceList.get(0).put("isConnect", false);
//                serviceList.clear();
//                characteristicList.clear();
//                deviceAdapter.notifyDataSetChanged();
//                serviceAdapter.notifyDataSetChanged();
//                dismissDialog();
//            } else if (intent.getAction().equals(BleService.ACTION_SCAN_FINISHED)) {
//                btn_scanBle.setEnabled(true);
//                dismissDialog();
//            }
        }
    };

    private static IntentFilter makeIntentFilter() {
        Log.i(TAG, "CGM--makeIntentFilter()");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_BLUETOOTH_DEVICE);
        intentFilter.addAction(BleService.ACTION_SCAN_FINISHED);
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }


    @Override
    public void openBle(Boolean enable) {
        if(enable){//打开蓝牙
            if(mBleService.isEnabledBluetooth()){
                if (!mBleService.isScanning()) {
                    mBleService.scanLeDevice(true);
                }
            }else
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "CGM--postDelayed()");
                        if (!mBleService.isScanning()) {
                            mBleService.scanLeDevice(true);
                        }
                    }
                }, OPENBLE_PERIOD);
            }
            if (mBleService.enableBluetooth(true)) {
                Toast.makeText(BleLibary.this, "Bluetooth was opened", Toast.LENGTH_SHORT).show();
            }

        }else{//关闭蓝牙
            mBleService.enableBluetooth(false);
        }

    }

    @Override
    public void scanBleDevice(Boolean enable) {
        if(enable){//蓝牙开始扫描
            if (!mBleService.isScanning()) {
                mBleService.scanLeDevice(true);
            }
        }
        else{//取消扫描
            if (mBleService.isScanning()) {
                mBleService.scanLeDevice(false);
            }
        }
    }

    /**
     * 解绑服务
     */
    private void doUnBindService() {
        if (mIsBind) {
            unbindService(serviceConnection);
            mBleService = null;
            mIsBind = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.doUnBindService();
        unregisterReceiver(bleReceiver);
    }

    @Override
    public void onBackPressed() {
        if (mBleService.isScanning()) {
            mBleService.scanLeDevice(false);
            return;
        }
        super.onBackPressed();
    }
}

package com.kamin.cgmblelib.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xmin on 2016/9/1.
 */
public class BleService extends Service implements Constants {
    //Debug
    private static final String TAG = BleService.class.getName();
    //Member fields
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private String mBluetoothDeviceAddress;
    private int mConnState = STATE_DISCONNECTED;
    private static BleService instance = null;
    public BleService() {
        instance = this;
        Log.i(TAG, "blelib--BleService()");
    }
    public static BleService getInstance() {
        if (instance == null) throw new NullPointerException("BleService is not bind.");
        return instance;
    }
    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "CGM--BleService.onBind(),mBinder="+mBinder);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        instance = null;
        return super.onUnbind(intent);
    }
    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        Log.d(TAG, "CGM-close()");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        Log.i(TAG, "CGM--BleService.initialize(),begin----");
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "CGM-Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "CGM-Unable to obtain a BluetoothAdapter.");
            return false;
        }
        Log.i(TAG, "CGM--BleService.initialize(),end----mBluetoothAdapter="+mBluetoothAdapter);
        return true;
    }
    /**
     * Turn on or off the local Bluetooth adapter;do not use without explicit
     * user action to turn on Bluetooth.
     *
     * @param enable if open ble
     * @return if ble is open return true
     */
    public boolean enableBluetooth(boolean enable) {
        Log.i(TAG, "CGM--enableBluetooth(),enable="+enable);
        if (enable) {
            if (!mBluetoothAdapter.isEnabled()) {
                return mBluetoothAdapter.enable();
            }
            return true;
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                return mBluetoothAdapter.disable();
            }
            return false;
        }
    }



}

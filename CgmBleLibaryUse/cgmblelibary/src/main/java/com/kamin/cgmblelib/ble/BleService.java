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

import com.kamin.cgmblelib.biz.AdvertisingBiz;

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
    private static final long SCAN_PERIOD = 5 * 1000; // Stop scanning after 10 seconds.
    private boolean isScanning;
    private List<BluetoothDevice> mScanLeDeviceList;
    private static BleService instance = null;
    public BleService() {
        instance = this;
        Log.i(TAG, "CGM--BleService()");
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
        // For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
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
    public boolean isEnabledBluetooth(){
        return mBluetoothAdapter.isEnabled();
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
    /**
     * If Ble is scaning return true, if not return false.
     *
     * @return ble whether scanning
     */
    public boolean isScanning(){Log.d(TAG, "blelib--isScanning()");
        return isScanning;
    }
    /**
     * Scan Ble device.
     *
     * @param enable If true, start scan ble device.False stop scan.
     */
    public void scanLeDevice(boolean enable) {
        Log.i(TAG, "CGM--scanLeDevice");
        this.scanLeDevice(enable, SCAN_PERIOD);
    }
    /**
     * Scan Ble device.
     *
     * @param enable     If true, start scan ble device.False stop scan.
     * @param scanPeriod scan ble period time
     */
    public void scanLeDevice(final boolean enable, long scanPeriod) {
        Log.i(TAG, "CGM--scanLeDevice,enable="+enable);
        if (isScanning) return;
        if (enable) {
            //Stop scanning after a predefined scan period.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    mBluetoothAdapter.stopLeScan(mScanCallback);
                    broadcastUpdate(ACTION_SCAN_FINISHED);
                    if (mScanLeDeviceList != null) {
                        mScanLeDeviceList.clear();
                        mScanLeDeviceList = null;
                    }
                }
            }, scanPeriod);
            if (mScanLeDeviceList == null) {
                mScanLeDeviceList = new ArrayList<>();
            }
            mScanLeDeviceList.clear();
            isScanning = true;
            mBluetoothAdapter.startLeScan(mScanCallback);
        } else {
            isScanning = false;
            mBluetoothAdapter.stopLeScan(mScanCallback);
            broadcastUpdate(ACTION_SCAN_FINISHED);
            if (mScanLeDeviceList != null) {
                mScanLeDeviceList.clear();
                mScanLeDeviceList = null;
            }
        }
    }
    /**
     * Device scan callback
     */
    private final BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i(TAG, "CGM--onLeScan()");
            if (mScanLeDeviceList.contains(device)) return;
            mScanLeDeviceList.add(device);
            broadcastUpdate(ACTION_BLUETOOTH_DEVICE, device);
        }
    };
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    private void broadcastUpdate(final String action, BluetoothDevice device) {
        final Intent intent = new Intent(action);
        intent.putExtra("name", device.getName());
        intent.putExtra("address", device.getAddress());
        sendBroadcast(intent);
    }

}

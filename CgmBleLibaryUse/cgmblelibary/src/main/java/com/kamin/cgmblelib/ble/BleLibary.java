package com.kamin.cgmblelib.ble;



import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;
import com.kamin.cgmblelib.biz.AdvertisingBiz;
import com.kamin.cgmblelib.utils.Conversion;

public class BleLibary extends AppCompatActivity implements BleInterface {
    //Debug
    private static final String TAG = BleLibary.class.getName();
    private BluetoothAdapter mBluetoothAdapter;
    private Context mainContext=this;
    private BleService mBleService;
    private boolean mIsBind;
    private boolean mScanning =false;
    private static final long SCAN_PERIOD = 5 * 1000; // Stop scanning after 10 seconds.
    private BleDeviceListAdapter mBleDeviceListAdapter=null;
    AlertDialog mScanDeviceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "CGM--BleLibary.onCreate()");
        super.onCreate(savedInstanceState);

        if(!isSupportBle())//是否支持蓝牙ble，如果支持获取BluetoothAdapter对象mBluetoothAdapter
        {
            Toast.makeText(mainContext, "Bluetooth BLE is not supported.", Toast.LENGTH_LONG).show();
            ((Activity) mainContext).finish();
        }
        this.doBindService();//绑定服务
        // Initializes list view adapter.
        mBleDeviceListAdapter = new BleDeviceListAdapter((Activity) mainContext);//new适配器对象
        //注册广播？
        mScanDeviceDialog= new AlertDialog.Builder(mainContext) .setTitle("BLE Device Scan...").setAdapter(mBleDeviceListAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                final BluetoothDevice device = mBleDeviceListAdapter.getDevice(which);
                if (device == null) return;
                scanLeDevice(false);
                if(device.getName()!=null && device.getAddress()!=null) Log.i(TAG, "CGM--mScanDeviceDialog.onClick()--deviceName="+device.getName()+",devcieAddress="+device.getAddress());
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                Log.i(TAG, "CGM--mScanDeviceDialog.onCancel()");
                mScanDeviceDialog.dismiss();
                scanLeDevice(false);
            }
        }).create();

    }
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
            }else{
                mBluetoothAdapter=mBleService.getmBluetoothAdapter();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBleService = null;
            mIsBind = false;
        }
    };


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
        if (mScanning) return;//如果正在扫描，下面就不用做了
        if (enable) {//使能扫描
            //Stop scanning after a predefined scan period.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, scanPeriod);
            if(mBleDeviceListAdapter != null)//先把以前的适配器清空
            {
                mBleDeviceListAdapter.clear();
                mBleDeviceListAdapter.notifyDataSetChanged();
            }
            if(!mScanning) { //如果没有扫描，则开始扫描
                mScanning = true;
                Log.i(TAG, "CGM--scanLeDevice,startLeScan()");
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {//停止扫描
            if(mScanning) {//如果正在扫描，则停止
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        AdvertisingBiz advertisingBiz=null;
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            Log.i(TAG, "CGM--BluetoothAdapter.LeScanCallback.onLeScan()");

            //scanRecord       The content of the advertisement record(Advertising Data) offered by the remote device.
            advertisingBiz=new AdvertisingBiz(mainContext,scanRecord);
            advertisingBiz.handleAdvertise();
            Log.i(TAG, "CGM--BluetoothAdapter.LeScanCallback:advertisement data="+ Conversion.byteArrayToHexStr(scanRecord));
            Log.i(TAG, "CGM--BluetoothAdapter.LeScanCallback:advertisement manufactureName=" +advertisingBiz.advertisingInfo.manufacturerName);
            Log.i(TAG, "CGM--BluetoothAdapter.LeScanCallback:advertisement dataColectOnOff=" + advertisingBiz.advertisingInfo.datatCollected);
            Log.i(TAG, "CGM--BluetoothAdapter.LeScanCallback:advertisement deviceName=" + advertisingBiz.advertisingInfo.deviceName);

            ((Activity) mainContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("mLeScanCallback onLeScan run ");
                    if (advertisingBiz.advertisingInfo.manufacturerName.equals("CGM")) {
                        mBleDeviceListAdapter.addDevice(device);
                        mBleDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    @Override
    public void openBle(Boolean enable) {
        if(enable){//打开蓝牙
            if (mBleService.enableBluetooth(true)) {
               if (!mScanning) {
                    scanLeDevice(true);
                    mScanDeviceDialog.show();
                }
            }

        }else{//关闭蓝牙
            mBleService.enableBluetooth(false);
        }

    }

    @Override
    public void scanBleDevice(Boolean enable) {
        if(enable){//蓝牙开始扫描
            if (!mScanning) {
                scanLeDevice(true);
            }
            mScanDeviceDialog.show();
        }
        else{//取消扫描
            if (mScanning) {
                scanLeDevice(false);
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
    }


}

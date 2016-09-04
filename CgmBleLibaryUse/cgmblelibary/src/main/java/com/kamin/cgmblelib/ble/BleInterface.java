package com.kamin.cgmblelib.ble;

import android.bluetooth.BluetoothDevice;
import android.widget.BaseAdapter;

import java.util.List;


/**
 * Created by xmin on 2016/9/3.
 */
public interface BleInterface {
    //open ble
    public void openBle(Boolean enable);
    //scan ble,get device list
    public BaseAdapter scanBleDevice(Boolean enable);

}

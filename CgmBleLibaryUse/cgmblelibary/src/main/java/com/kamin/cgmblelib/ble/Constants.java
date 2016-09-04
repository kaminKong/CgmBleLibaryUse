package com.kamin.cgmblelib.ble;

/**
 * Created by xmin on 2016/9/1.
 */
public interface Constants {
    //Connection state
    int STATE_DISCONNECTED = 0;
    int STATE_CONNECTING = 1;
    int STATE_CONNECTED = 2;
    int STATE_DISCONNECTING = 3;

    //Action
    String ACTION_GATT_DISCONNECTED = "com.kamin.cgmblelib.ble.ACTION_GATT_DISCONNECTED";
    String ACTION_GATT_CONNECTING = "com.kamin.cgmblelib.ble.ACTION_GATT_CONNECTING";
    String ACTION_GATT_CONNECTED = "com.kamin.cgmblelib.ble.ACTION_GATT_CONNECTED";
    String ACTION_GATT_DISCONNECTING = "com.kamin.cgmblelib.ble.ACTION_GATT_DISCONNECTING";
    String ACTION_GATT_SERVICES_DISCOVERED = "com.kamin.cgmblelib.ble.ACTION_GATT_SERVICES_DISCOVERED";
    String ACTION_BLUETOOTH_DEVICE = "com.kamin.cgmblelib.ble.ACTION_BLUETOOTH_DEVICE";
    String ACTION_SCAN_FINISHED = "com.kamin.cgmblelib.ble.ACTION_SCAN_FINISHED";
}

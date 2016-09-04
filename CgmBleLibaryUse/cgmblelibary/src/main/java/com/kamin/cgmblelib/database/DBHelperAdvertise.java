package com.kamin.cgmblelib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kamin.cgmblelib.entities.AdvertisingInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xmin on 2016/8/22.
 */
public class DBHelperAdvertise {//对发射器广播数据的处理和存储
    private static final String TAG = "DBHelperAdvertise";
    private static DBHelper dbHelper=null;
    private DBHelperAdvertise(){    }
    /*下面的静态内部类同样利用了类加载机制来保证只创建一个instance实例。它与饿汉模式一样，也是利用了类加载机制，因此不存在多线程并发的问题。
    不一样的是，它是在内部类里面去创建对象实例。这样的话，只要应用中不使用内部类，JVM就不会去加载这个单例类，也就不会创建单例对象，从而实现懒汉式的延迟加载。
    也就是说这种方式可以同时保证延迟加载和线程安全。*/
    private static class SingletonHolder {
        private static DBHelperAdvertise dbHelperAdvertise=new DBHelperAdvertise();
    }
    /*想把数据存储到sqlite，（1）需要使用SQLiteOpenHelper子类DBHelper的 对象dbHelper的getWritableDatabase() 方法--》
    (2)dbHelper初始化(3)方便的调用saveAdvertise()方法需要生成本类DBHelperAdvertise对象
    所以为了方便的使用本类的方法，又为方法提供了需要使用的DBHelper的 对象dbHelper完成下面的方法*/
    public static DBHelperAdvertise getInstance(Context context){
        if(dbHelper==null) {dbHelper=new DBHelper(context);}
        return SingletonHolder.dbHelperAdvertise;
    }
    //把扫描到的广播信息存入数据库
    public void saveAdvertise(AdvertisingInfo advertise){
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        //向ContentValues中存放数据，第1个参数是ADVERTISEDDL中的列名，第2个参数是此列对应的值
        contentValues.put("deviceName",advertise.deviceName);
        contentValues.put("manufacturerName",advertise.manufacturerName);
        contentValues.put("datatCollected",advertise.datatCollected);
        contentValues.put("advertiseDateTime",advertise.advertiseDateTime);
        // //数据库执行插入命令
        synchronized (sqLiteDatabase) {
            sqLiteDatabase.insert(DBConstant.ADVERTISETB, null, contentValues);
        }
    }
   //从数据库中查询某个deviceName的广播信息（1条）
    public AdvertisingInfo queryByDeviceName(String deviceName){
        AdvertisingInfo advertisingInfo=null;
        SQLiteDatabase sqLiteDatabase=dbHelper.getReadableDatabase();
        Cursor cursor=null;//创建游标对象
        synchronized (sqLiteDatabase) {
           cursor=sqLiteDatabase.query(DBConstant.ADVERTISETB,new String[]{"deviceName,manufacturerName,datatCollected,advertiseDateTime"},
                                                "deviceName=?",new String[]{deviceName},null,null,null,null);
        }
        while (cursor.moveToNext()){//利用游标遍历所有数据对象
            Log.d(TAG, "CGM-queryByDeviceName: -->deviceName="+cursor.getString(cursor.getColumnIndex("deviceName"))+"--manufacturerName="+cursor.getString(cursor.getColumnIndex("manufacturerName"))+
            "----datatCollected="+cursor.getInt(cursor.getColumnIndex("datatCollected"))+"---advertiseDateTime="+cursor.getString(cursor.getColumnIndex("advertiseDateTime")));

            advertisingInfo=new AdvertisingInfo(cursor.getString(cursor.getColumnIndex("deviceName")),cursor.getString(cursor.getColumnIndex("manufacturerName")),
                    cursor.getInt(cursor.getColumnIndex("datatCollected")),cursor.getString(cursor.getColumnIndex("advertiseDateTime")));
        }
        cursor.close();
        return advertisingInfo;
    }
    //从数据库中查询某个manufacturerName的广播信息（列表）
    public List<AdvertisingInfo> queryByanufacturerName(String manufacturerName){
        List<AdvertisingInfo> advertisingInfoList=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=dbHelper.getReadableDatabase();
        Cursor cursor=null;//创建游标对象
        synchronized (sqLiteDatabase) {
            cursor=sqLiteDatabase.query(DBConstant.ADVERTISETB,new String[]{"deviceName,manufacturerName,datatCollected,advertiseDateTime"},
                    "manufacturerName=?",new String[]{manufacturerName},null,null,null,null);
        }
        while (cursor.moveToNext()){//利用游标遍历所有数据对象
            Log.d(TAG, "CGM-queryByanufacturerName: -->deviceName="+cursor.getString(cursor.getColumnIndex("deviceName"))+"--manufacturerName="+cursor.getString(cursor.getColumnIndex("manufacturerName"))+
                    "----datatCollected="+cursor.getInt(cursor.getColumnIndex("datatCollected"))+"---advertiseDateTime="+cursor.getString(cursor.getColumnIndex("advertiseDateTime")));

            AdvertisingInfo advertisingInfo=new AdvertisingInfo(cursor.getString(cursor.getColumnIndex("deviceName")),cursor.getString(cursor.getColumnIndex("manufacturerName")),
                    cursor.getInt(cursor.getColumnIndex("datatCollected")),cursor.getString(cursor.getColumnIndex("advertiseDateTime")));
            advertisingInfoList.add(advertisingInfo);
        }
        cursor.close();
        return advertisingInfoList.size()==0?null:advertisingInfoList;
    }
}

package com.kamin.cgmblelib.database;

/**
 * Created by xmin on 2016/8/22.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import  android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
    private static final String TAG = "DBHelper";

    public DBHelper(Context context){ //构造函数，此构造函数必不可少
        super(context, DBConstant.DBNAME,null,1);
    }
    //onCreate 方法会在调用getReadableDatabase或者WriteableDatabase时并且数据库不存在时才会被调用，数据库如果已经存在则不会调用。
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: create a database cgmDB");
        if(!TextUtils.isEmpty(DBConstant.ADVERTISEDDL)){
            db.execSQL(DBConstant.ADVERTISEDDL);
        }
        if(!TextUtils.isEmpty(DBConstant.DEVICEDDL)){
            db.execSQL(DBConstant.DEVICEDDL);
        }
        if(!TextUtils.isEmpty(DBConstant.RECEIVEDATADDL)){
            db.execSQL(DBConstant.RECEIVEDATADDL);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "CGM-onUpgrade:update a database ");
    }
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}

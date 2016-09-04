package com.kamin.cgmblelib.database;

/**
 * Created by xmin on 2016/8/21.
 */
public class DBConstant {
    //create table use database name and ddl sql string
    public static final String DBNAME="cgmDB";//database name
    /*对Android手机扫描到的发射器的广播信息进行增删查改*/
    public static final String ADVERTISEDDL= "create table if not exists advertise("//ddl sql string，datatCollected如果为00，表示此发射器还没有开始采集数据 如果为01表示已经开始采集数据了
            + "deviceName text primary key, "
            + "manufacturerName text, "
            + "datatCollected integer, "
            + "advertiseDateTime text)";
    public static final String ADVERTISETB="advertise"; //SQLiteDatabase.insert,delete,query,update use TableName
  /*对Android手机所连接的发射器的信息进行增删查改*/
   public static final String DEVICEDDL= "create table if not exists device("//ddl sql string
          + "deviceName text primary key, "
          + "deviceAddress text, "
          + "deviceType integer, "//0:Unknown   1:Classic - BR/EDR devices  2:Low Energy - LE-only   3: Dual Mode - BR/EDR/LE
          + "bondState integer, "  //10:not bonded (paired).  11: bonding (pairing) is in progress   12:bonded (paired).
          + "connectedDateTime1 text, "//第1次成功连接的时间
          + "connectedCount integer, "//连接的次数
          + "connectedDateTime2 text)";//新的连接更新这个时间
    public static final String DEVICETB="device"; //SQLiteDatabase.insert,delete,query,update use TableName
    /*从发射器接收到的信息进行增删查改*/
    public static final String RECEIVEDATADDL= "create table if not exists receiveData("//ddl sql string
            + "id integer primary key autoincrement, "
            + "receiveDataStr text, "// 从发射器收到的数据，十六进制形式字符串
            + "commandStr text, "// 从发射器收到的数据的第1个字节
            + "year integer, "// 从发射器收到的数据解析出的年
            + "month integer, "// 从发射器收到的数据解析出的月
            + "day integer, "// 从发射器收到的数据解析出的日
            + "hour integer, "// 从发射器收到的数据解析出的时
            + "minute integer, "// 从发射器收到的数据解析出的分
            + "operatingCurrent real, "// 从发射器收到的数据解析出的工作电流
            + "blankCurrent real, "// 从发射器收到的数据解析出的空白电流
            + "temperature real, "// 从发射器收到的数据解析出的温度
            + "battery integer, "// 从发射器收到的数据解析出的电池电量百分比
            + "glucoseId integer, "// 从发射器收到的数据解析出的血糖数据ID号
            + "sendDateTime text, "// 从发射器收到的数据的年月日时分
            + "receivedDateTime text)";//收到发射器数据的时间
    public static final String RECEIVEDATATB="receiveData"; //SQLiteDatabase.insert,delete,query,update use TableName
}

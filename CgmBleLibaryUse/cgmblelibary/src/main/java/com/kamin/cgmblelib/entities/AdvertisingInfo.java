/*
在具体的xxxInfo,java中有很多实体类中是没有get/set的方 法，而是直接使用public的字段名。
这一点，我是推荐这种方式的，特别是在移动开发中，get/set方法很多时候是完全没有必要的，而且是有性能消 耗的。
当然如果需要对字段设置一定的控制，get/set方法也是可以酌情使用的。
 */
package com.kamin.cgmblelib.entities;

/**
 * Created by xmin on 2016/8/17.
 */
public class AdvertisingInfo {
    public String deviceName;   //AD Data of AD Type=0x09
    public String manufacturerName;//AD Data of AD Type=0xFF 1th-3th byte
    public Integer datatCollected;////AD Data of AD Type=0xFF  4th byte,0（new）1（has collected）
    public String advertiseDateTime;//DateTime scan advertise
    public AdvertisingInfo(){}
    public AdvertisingInfo(String deviceName, String manufacturerName, Integer datatCollected, String advertiseDateTime) {
        this.deviceName = deviceName;
        this.manufacturerName = manufacturerName;
        this.datatCollected = datatCollected;
        this.advertiseDateTime = advertiseDateTime;
    }
}

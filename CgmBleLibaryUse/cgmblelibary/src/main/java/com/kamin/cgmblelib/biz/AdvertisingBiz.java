package com.kamin.cgmblelib.biz;

import android.content.Context;
import com.kamin.cgmblelib.database.DBHelperAdvertise;
import com.kamin.cgmblelib.entities.AdvertisingInfo;
import com.kamin.cgmblelib.utils.DataHandle;
import java.nio.ByteBuffer;

/**
 * Created by xmin on 2016/8/22.
 */
public class AdvertisingBiz {//记录扫描到的发射器的广播信息
    private Context context;
    public AdvertisingInfo advertisingInfo;
    public AdvertisingBiz(Context context,byte[] advData){
        this.context=context;
        advertisingInfo=parseAdvertisementData(advData);
        advertisingInfo.advertiseDateTime= DataHandle.getSysDate();
    }
    public  void handleAdvertise(){
        //查询数据库中是否含有此广播信息，如果有不再添加到数据库中，没有则添加
        AdvertisingInfo dbAdvertisingInfo= DBHelperAdvertise.getInstance(context).queryByDeviceName(advertisingInfo.deviceName);
        if(dbAdvertisingInfo==null){
            DBHelperAdvertise.getInstance(context).saveAdvertise(advertisingInfo);
        }
    }
    /*
       解析Android手机扫描到的每条广播信息，并把信息存为AdvertisingInfo对象
     */
    private  AdvertisingInfo parseAdvertisementData( byte[] advData){
        AdvertisingInfo advertisingInfo =new AdvertisingInfo();
        ByteBuffer buffer=ByteBuffer.wrap(advData);//not need .order(ByteOrder.LITTLE_ENDIAN); because not need to number
        while(buffer.remaining()>2){
            byte ad_length=buffer.get();
            if(ad_length==0) break;
            ad_length-=1;
            byte ad_type=buffer.get();
            switch(ad_type){
                case 0x01://ad_length>0  do buffer.position()
                case (byte)0xff:// Manufacturer Specific Data
                    byte[] manufacturerBytes=new byte[ad_length];
                    buffer.get(manufacturerBytes, 0, ad_length - 1);
                    advertisingInfo.manufacturerName=new String(manufacturerBytes).trim();
                    advertisingInfo.datatCollected=(int)buffer.get();
                    ad_length=0;//not need do buffer.position()
                    break;
                case  0x09: // Complete local device name
                    byte deviceBytes[] = new byte[ad_length];
                    buffer.get(deviceBytes);
                    advertisingInfo.deviceName=new String(deviceBytes).trim();
                    ad_length=0;//not need do buffer.position()
                    break;
                case  (byte)0x0a: // ad_length>0  do buffer.position()
                    break;
                default: // skip
                    break;
            }
            if (ad_length > 0) {
                buffer.position(buffer.position() + ad_length);
            }


        }
        return advertisingInfo;
    }
}

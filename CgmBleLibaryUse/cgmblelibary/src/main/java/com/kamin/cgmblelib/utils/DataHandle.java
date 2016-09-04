/**
 * public static byte[] subBytes(byte[] src, int beginIndex, int count) 				截取原来字节数组的一部分。从beginIndex开始截取，截count个
 * public static String dataBytesToHexStr(byte[] dataBytes,int beginIndex, int count)	以十六进制形式显示截取的原来字节数组的一部分。从beginIndex开始截取，截count个
 * public static Byte getDataVerifyCode(byte[] dataBytes)								参数不含校验码，获取字节数组的校验和，校验和是此字节数组所有元素的数值之和的低8位.
 * public static boolean dataVerify(byte[] dataBytes)									参数含有校验码，判断字节数组的校验码是否正确，正确返回true,错误返回false
 * public static byte[] getSendToEmitterVaue(byte commandByte,byte[] varBytes)			获得Android手机发送给发射器的命令，命令格式为：1个字节的命令帧,n个字节的参数帧，1个字节的校验和帧。第1个参数是命令帧，第2个参数是参数帧，校验和帧在方法内部获取
 * public static byte[] getSysDateTime()												把Android手机的系统日期（年月日时分秒）转换含7个字节的为字节数组，其中年占2个字节，低位在前，高位在后
 */
/*
 * @(#)Conversion.java     1.00 2016/08/15
 * Copyright 2016 Doctor R, Inc. All rights reserved.
 * Doctor R PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.kamin.cgmblelib.utils;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The {@code DataHandle} class do all operation to byteArray to handle data.
 * <p>
 *  For example:
 *  Returns a new sub byte[] with Hexadecimal String that is a subByte of byte[] dataBytes.
 * <blockquote><pre>
 * 	 	String HexStr="01 55 AA 00";
 *  	byte[] src=Conversion.hexStringToByteArray(HexStr);
 *  	System.out.println(Conversion.dataBytesToHexStr(src,1,2));
 * </pre></blockquote><p>
 * get verifyCode of the dataBytes you inputed
 * <blockquote><pre>
 *    	String HexStr="58	55	AA";
 *  	byte[] src=Conversion.hexStringToByteArray(HexStr);
 *  	byte verifyCode=Conversion.getDataVerifyCode(src);
 *  	String verifyCodestr=Conversion.byteToHexStr(verifyCode);
 *  	System.out.println(verifyCodestr);
 * </pre></blockquote><p>
 *  verifyCode is true or false your param byte[] include verifyCode
 * <blockquote><pre>
 *    	String HexStr="01 55 AA 00";
 *  	byte[] src=Conversion.hexStringToByteArray(HexStr);
 *		if(Conversion.dataVerify(src)) System.out.println("right");	else System.out.println("error");
 * </pre></blockquote><p>
 * get value of Sending to Emiiter：Set Emitter ID，Set Emitter Date
 * <blockquote><pre>
 *  	byte[] commandBytes=DataHandle.getSendToEmitterVaue((byte)0x09, "SN06160099".getBytes());
 *  	System.out.println(Conversion.byteArrayToHexStr(commandBytes));
 * </pre></blockquote><p>
 * <blockquote><pre>
 * 		byte varBytes[]=DataHandle.getSysDateTime();
 * 		byte[] commandBytes=DataHandle.getSendToEmitterVaue((byte)0x07, varBytes);
 * 		System.out.println(Conversion.byteArrayToHexStr(commandBytes));
 * </pre></blockquote><p>
 * Change Andrioid Phone System DateTime to byte[].include yyyyMMddhh:mm:ss and yyyy high bit is the second byte:07,low bit is the first byte:0E
 * <blockquote><pre>
 *  	System.out.println(Conversion.byteArrayToHexStr(DataHandle.getSysDateTime()));
 * </pre></blockquote><p>
 * @author  Kamin Kong
 * @see     Object#toString()
 * @see     Class
 * @see     StringBuilder
 * @see		String#String(byte[], java.nio.charset.Charset)
 * @since   JDK1.8
 */
public class DataHandle {

    /**
     * Returns a new sub byte[] that is a subByte of byte[] src.The
     * subarray begins with the character at the specified index and
     * get the specified length of this subarray. <p>
     * @param src
     * 			The bytes to be Intercepted
     * @param beginIndex
     * 			The index of the first byte to Intercept
     * @param count
     * 			The number of bytes to Intercept
     * @return the specified sub byte[]    if {@code src}==null or {@code src}.length==0 or return null.
     * @exception  IndexOutOfBoundsException  if the
     *             {@code beginIndex} is negative, or
     *             {@code beginIndex} is larger than {@code src}.length-{@code count}
     *             {@code count} is larger than the length of this {@code src} object.
     */
    public static byte[] subBytes(byte[] src, int beginIndex, int count) {

        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (beginIndex > src.length-count) {
            throw new StringIndexOutOfBoundsException(beginIndex+count);
        }
        if (count > src.length) {
            throw new StringIndexOutOfBoundsException(count);
        }
        if (src != null && src.length > 0) {
            byte[] bs = new byte[count];
            System.arraycopy(src, beginIndex, bs, beginIndex - beginIndex, beginIndex + count - beginIndex);
            return bs;
        }
        else{
            return null;
        }
    }

    /**
     * Returns a new sub byte[] with Hexadecimal String that is a subByte of byte[] dataBytes. <p>
     * For example:
     * <blockquote><pre>
     *  String HexStr="01 55 AA 00";
     *  byte[] src=Conversion.hexStringToByteArray(HexStr);
     *  System.out.println(Conversion.dataBytesToHexStr(src,1,2));
     * </pre></blockquote><p>
     * Reuslt is:	55 AA
     * @param dataBytes
     * 			The bytes to be Intercepted
     * @param beginIndex
     * 			The index of the first byte to Intercept
     * @param count
     * 			The number of bytes to Intercept
     * @return	Hexadecimal String
     * @see	#subBytes(byte[], int, int)
     * @see	Conversion#byteArrayToHexStr(byte[])
     */
    public static String dataBytesToHexStr(byte[] dataBytes,int beginIndex, int count){
        if(dataBytes==null||dataBytes.length<2){		//dataBytes has first command byte and last verify byte at least
            return "";
        }
        return Conversion.byteArrayToHexStr(subBytes(dataBytes, beginIndex, count));
    }

    /**
     * get verifyCode of the dataBytes you inputed <p>
     *  For example:
     * <blockquote><pre>
     *  	String HexStr="58	55	AA";
     *  	byte[] src=Conversion.hexStringToByteArray(HexStr);
     *  	byte verifyCode=Conversion.getDataVerifyCode(src);
     *  	String verifyCodestr=Conversion.byteToHexStr(verifyCode);
     *  	System.out.println(verifyCodestr);
     * </pre></blockquote><p>	 *
     * Result is: 57
     * @param dataBytes  your byte[] NOt include verifyCode
     * @return byte
     * @see	#subBytes(byte[], int, int)
     */
    public static Byte getDataVerifyCode(byte[] dataBytes){
        if(dataBytes==null||dataBytes.length==0){
            return null;
        }
        else{
            byte verifySum=0,verifyCode=0;
            byte[] mydata=subBytes(dataBytes, 0, dataBytes.length);
            if (mydata != null) {
                for(byte oneByte:mydata){
                    verifySum+=oneByte;
                }
            }
            return verifySum;
        }
    }
    /**
     * verifyCode is true or false your param byte[] include verifyCode<p>
     *  For example:
     * <blockquote><pre>
     *  	byte[] src={0x01,0x55,0x1A,0x70};
     * 		if(Conversion.dataVerify(src)) System.out.println("right");	else System.out.println("error");
     * </pre></blockquote><p>
     * but some Hexadecimal String can not show use byte,such as 0xAA=170>127,so byte[[] src={0xAA,0x55,0x1A,0x70} will error,example can use
     * <blockquote><pre>
     *  	String HexStr="01 55 AA 00";
     *  	byte[] src=Conversion.hexStringToByteArray(HexStr);
     *		if(Conversion.dataVerify(src)) System.out.println("right");	else System.out.println("error");
     * </pre></blockquote><p>
     * Result is: true
     * @param dataBytes	your byte[] include verifyCode
     * @return true  false.  if verifyCode is right return true,else return false
     * @see	#getDataVerifyCode(byte[])
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static boolean dataVerify(byte[] dataBytes){
        byte[] dataBytesNoVerifyCode=new byte[dataBytes.length-1];
        System.arraycopy(dataBytes, 0, dataBytesNoVerifyCode, 0, dataBytes.length-1);
        byte verifySum=getDataVerifyCode(dataBytesNoVerifyCode);
        if(dataBytes==null||dataBytes.length==0){
            return false;
        }
        else{
            byte verifyCode=dataBytes[dataBytes.length-1];
            return verifyCode == verifySum;
        }
    }


    /**
     * get value of Sending to Emiiter
     *  For example:Set Emitter ID
     * <blockquote><pre>
     *  	byte[] commandBytes=DataHandle.getSendToEmitterVaue((byte)0x09, "SN06160099".getBytes());
     *  	System.out.println(Conversion.byteArrayToHexStr(commandBytes));
     * </pre></blockquote><p>
     * Result is: 0x{09 53 4E 30 36 31 36 30 30 39 39 49}<p>
     * Set Emitter Date
     * <blockquote><pre>
     * 		byte varBytes[]=DataHandle.getSysDateTime();
     * 		byte[] commandBytes=DataHandle.getSendToEmitterVaue((byte)0x07, varBytes);
     * 		System.out.println(Conversion.byteArrayToHexStr(commandBytes));
     * </pre></blockquote><p>
     * Result is: 0x{07 E0 07 08 14 0E 09 20 41 },the data：0x{E0 07 08 14 0E 09 20} is 2016/08/20 14:09:32<p>
     * @param commandByte byte
     * @param varBytes,such as Emitter ID,System Date,
     * @return byte[] value of Sending to Emitter,include commandByte <Var> verifyByte
     * @see	#getDataVerifyCode(byte[])
     * @see ByteBuffer
     * @see java.io.UnsupportedEncodingException
     */
    public static byte[] getSendToEmitterVaue(byte commandByte,byte[] varBytes){
        byte[] sendToEmitterValue=new byte[varBytes.length+2];
        ByteBuffer buffer=ByteBuffer.wrap(sendToEmitterValue);
        buffer.put(commandByte);//put Command Byte
        buffer.put(varBytes);//put Var Bytes
        byte veifyByte=getDataVerifyCode(sendToEmitterValue);
        buffer.put(veifyByte);//put Verify Byte
        return sendToEmitterValue;//return buffer.array();//same
    }

    /**
     * Change Andrioid Phone System DateTime to byte[].include yyyyMMddhh:mm:ss and yyyy high bit is the first byte:07,low bit is the second byte:0E
     *  For example:
     * <blockquote><pre>
     *  	System.out.println(Conversion.byteArrayToHexStr(DataHandle.getSysDateTime()));
     * </pre></blockquote><p>
     * Result is: {07 E0 08 14 0E 12 03 } is 2016/08/20 14:18:03<p>
     * @return byte[]
     * @see	#getSendToEmitterVaue(byte, byte[])
     * @see ByteBuffer
     * @see Calendar
     */
    public static byte[] getSysDateTime(){
        Calendar c = Calendar.getInstance();
        byte[] sysDateTime=new byte[7];
        ByteBuffer buffer=ByteBuffer.wrap(sysDateTime);
        int year = c.get(Calendar.YEAR);
        byte[] yearBytes=new byte[]{(byte) ((year >> 8) & 0xFF),(byte) (year & 0xFF)};//now high bit is before
        buffer.put(yearBytes);
        buffer.put((byte)(c.get(Calendar.MONTH)+1));
        buffer.put((byte)c.get(Calendar.DAY_OF_MONTH));
        buffer.put((byte)c.get(Calendar.HOUR_OF_DAY));
        buffer.put((byte)c.get(Calendar.MINUTE));
        buffer.put((byte)c.get(Calendar.SECOND));
        return sysDateTime;
    }
    /**
     * get String current DateTime
     *  For example:
     * <blockquote><pre>
     *  	System.out.println(getSysDate());
     * </pre></blockquote><p>
     * Result is: 2016/08/22 17:49:19<p>
     * @return String
     * @see SimpleDateFormat
     * @see Date
     */
    public static String getSysDate(){
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return formatter.format(new Date());  //get currentDateTime
    }
}

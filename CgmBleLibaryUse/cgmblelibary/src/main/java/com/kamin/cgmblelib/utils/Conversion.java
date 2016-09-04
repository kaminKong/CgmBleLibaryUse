/**
 * @author kamin     version=1.0      date begin=2016/08/13    date end=2016/08/15
 * byte:一种数据类型，最多存8位整数，-128到127之间的整数，不能放这个范围之外的数据
 * 在输出AA（170）这种大于十进制数127的十六进制字节时由于byte的范围为-128到127，输出的结果为负数（byte最高为为符号位），显示在界面上是乱码，可以考虑用如下输出方式：String s="AA";byte[] result=Conversion.hexStringToByteArray(s);System.out.println(new String(result,"ISO-8859-1"));
 * public static String byteToHexStr(byte byteData)					把byte类型整数转换为十六进制字符串，输出格式为%02X是指2个十六进制位，输出结果不含0X，见下面函数例子。11
 * public static String byteArrayToHexStr(byte[] byteArray)			把字节数组转换成十六进制字符串，输出结果为大写每个自己用空格分割，见下面函数例子。43474d
 * public static String byteArrayToHexStr2(byte[] byteArray)		把字节数组转换成十六进制字符串，输出结果为小写无空格，见下面函数例子。43474D
 * public static final String byteArrayToHexStr3(byte[] byteArray)	把字节数组转换成十六进制字符串，输出结果为大写无空格，见下面函数例子。43:47:4D
 * public static String byteArrayToHexStr(byte[] byteArray, int len)把指定个数的字节数组转换为十六进制字符串，如果len>字节数组的长度，则全部输出，输出结果为大写每个字节用:分割43:47:4D
 * public static String byteArrayToHexStr(byte[] byteArray, boolean reverse)把字节数组转换成十六进制字符串,false从高位到低位转换，true从低位到高位转换43:47:4D
 * public static byte[] hexStringToByteArray(String hexString)		把十六进制字符串转换为字节数组,字符串必须为0，1，2，3，4，5，6，7，8，9,A,B,C,D,E,F中的字符，可以有空格，字母大小写都可以
 * public static byte[] shortToByte(Short number) 					把Short类型的整数转换为字节数组，转换后的字节数组第1个字节为高位，第2个字节为低位，用于输入指定的血糖ID
 * 如果转换后的字节数组的元素含有大于>127的数，println为负数
 */

/*
 * @(#)Conversion.java     1.00 2016/08/13
 * Copyright 2016 Doctor R, Inc. All rights reserved.
 * Doctor R PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.kamin.cgmblelib.utils;

import java.util.Formatter;
/**
 * The {@code Conversion} class represents all operation between byte[] 
 * and show byte[] with Hexadecimal String or with Decimal String. 
 * <p>
 *  For example:
 * <blockquote><pre>
 *     byte byteData=17;String result=Conversion.byteToHexStr(byteData);System.out.println(result);
 * </pre></blockquote><p>
 * <blockquote><pre>
 *    byte[] byteArray={67,71,77};String result=Conversion.byteArrayToHexStr(byteArray);System.out.println(result);
 * </pre></blockquote><p>
 * is equivalent to:
 * <blockquote><pre>
 *    byte[] byteArray={67,71,77};	String result=Conversion.byteArrayToHexStr(byteArray,false);System.out.println(result);
 * </pre></blockquote><p>
 * Here are some more examples of how strings can be used:
 * <blockquote><pre>
 *    String str="43474D";	byte[] result=Conversion.hexStringToByteArray(str);	for(byte b:result)	System.out.print(b+" ");
 * </pre></blockquote>
 * <p>
 *
 * @author  Kamin Kong
 * @see		String#format(String, Object...)
 * @see     StringBuilder
 * @see		String#charAt(int)
 * @see		Integer#toHexString(int)
 * @see		Formatter
 * @since   JDK1.8
 */

public class Conversion {	
	/**
	 * byte to HexString
	 * example:byte byteData=17;String result=Conversion.byteToHexStr(byteData);System.out.println(result);
	 * example result:11
	 * @param byteData byte
	 * @return	String
	 */
	public static String byteToHexStr(byte byteData){
		return String.format("%02X", byteData);
	}
	/**
	 * byte Array to HexString
	 * example:byte[] byteArray={67,71,77};String result=Conversion.byteArrayToHexStr(byteArray);System.out.println(result);
	 * example result:43 47 4D
	 * @param byteArray byte[]
	 * @return	String
	 */
	public static String byteArrayToHexStr(byte[] byteArray){
		 StringBuilder sbResult=null;
		 if (byteArray != null && byteArray.length > 0) {
			 sbResult = new StringBuilder(byteArray.length);
             for(byte byteChar : byteArray)             	
            	 sbResult.append(String.format("%02X ", byteChar));            
		 }
		 return sbResult.toString();
	}
	/**
	 * method 2  byte Array to HexString
	 * example same up
	 * example result:43474d
	 * @param byteArray byte[]
	 * @return	String
	 */
	public static String byteArrayToHexStr2(byte[] byteArray)
	{
		final String HEX = "0123456789abcdef";
		StringBuilder sb = new StringBuilder(byteArray.length * 2);
		for (byte b : byteArray)
		{			
			sb.append(HEX.charAt((b >> 4) & 0x0f));// 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数			
			sb.append(HEX.charAt(b & 0x0f));// 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
		}
		return sb.toString();
	}
	/**
	 * method 3  byte Array to HexString
	 * example same up
	 * example result:43474D
	 * @param byteArray byte[]
	 * @return String
	 */
	public static  String byteArrayToHexStr3(byte[] byteArray) {
		StringBuffer sb = new StringBuffer(byteArray.length);
		String sTemp;
		for (int i = 0; i < byteArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & byteArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
	/**
	 * byte Array to HexString by len
	 * example result    43:47:4D
	 * @param byteArray byte[]
	 * @param len		if len>byteArray.length then Convert all byteArra
	 * @return String
	 */
	public static String byteArrayToHexStr(byte[] byteArray, int len) {
		StringBuilder sb = new StringBuilder(byteArray.length * (2 + 1));
		Formatter formatter = new Formatter(sb);
		if(len>byteArray.length) 
			len=byteArray.length;
		else{ 
		    for (int i = 0; i < len; i++) {
		      if (i < len - 1)
		        formatter.format("%02X:", byteArray[i]);
		      else
		        formatter.format("%02X", byteArray[i]);

		    }
		    formatter.close();
			}
	
		return sb.toString();
			  
	}

	/**
	 * byte Array to HexString from low bit or high bit
	 * example:byte[] byteArray={67,71,77};	String result=Conversion.byteArrayToHexStr(byteArray,false);System.out.println(result);
	 * example result              43:47:4D(false)     4D:47:43(true)
	 * @param byteArray byte[]
	 * @param reverse  false:from high to low by order          true:from low to high by reverse order
	 * @return String
	 */
	public static String byteArrayToHexStr(byte[] byteArray, boolean reverse) {
	    StringBuilder sb = new StringBuilder(byteArray.length * (2 + 1));
	    Formatter formatter = new Formatter(sb);

	    if (!reverse) {
	      for (int i = 0; i < byteArray.length; i++) {
	        if (i < byteArray.length - 1)
	          formatter.format("%02X:", byteArray[i]);
	        else
	          formatter.format("%02X", byteArray[i]);

	      }
	    } else {
	      for (int i = (byteArray.length - 1); i >= 0; i--) {
	        if (i > 0)
	          formatter.format("%02X:", byteArray[i]);
	        else
	          formatter.format("%02X", byteArray[i]);

	      }
	    }
	    formatter.close();

	    return sb.toString();
	  }

	/**
	 * Convert hex String to Byte Array
	 * example:String str="43474D";	byte[] result=Conversion.hexStringToByteArray(str);	for(byte b:result)	System.out.print(b+" ");
	 * System.out.println(); System.out.println("byteArry to StringAscii----"+new String(result));
	 * example result    67 71 77 
	 * 					 byteArry to StringAscii----CGM
	 * @param hexString  you can enter any combination of numbers,space,uppercase and lowercase letters,
	 * 					 but String must have an even number of digits in order to specify byte values properly,such as :"AB 23 D2".
	 * 					 "AB 3 D" is error,Should be replaced by:"AB 03 0D".
	 * @return byte[]
	 * @see	#toByte(char)
	 */
	public static byte[] hexStringToByteArray(String hexString) {
		if (hexString == null || hexString.equals("")) {
		        return null;
		}
		if(hexString.contains("0x")) hexString=hexString.replaceAll("0x", "");
		hexString=hexString.toUpperCase();
		hexString=hexString.replaceAll("\\s*", "");//remove white space characters include carriage returns, line feeds, spaces, and tabs		
		int len = (hexString.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hexString.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}
	
	/**
	 * byte Array to AscciStr
	 * example:byte[] byteArray={65,66,67,68,69,70};System.out.println(Conversion.byteArrayToAsciiStr(byteArray));
	 * example result            ABCDEF
	 * @param byteArray byte[]
	 * @return String
	 */
	public static String byteArrayToAsciiStr(byte[] byteArray){
		return new String(byteArray);
	}
	/**
	 * byteArrayToHexStr3 and hexStringToByteArray use 
	 * @param c char
	 * @return byte
	 */
	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
	/**
	 * Short to Byte Arrary.the first byte is high bit,the second byte is low bit
	 * example:Short number=1;byte[] data=Conversion.shortToByte(number);System.out.println(Conversion.byteArrayToHexStr(data));
	 * example result            00 01
	 * @param number Short
	 * @return byte[]
	 */
	public static byte[] shortToByte(Short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = b.length-1; i >0; i--) {
			b[i] = Integer.valueOf(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

}

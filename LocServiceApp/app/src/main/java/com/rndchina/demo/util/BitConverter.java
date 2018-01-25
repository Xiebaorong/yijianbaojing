package com.rndchina.demo.util;

/**
 * Created by dell on 2017-8-28.
 */

public class BitConverter {
    public static String byteArrayToString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static byte[] StringToByteArray(String data) {
        int NumberChars = data.length();
        byte[] bytes = new byte[NumberChars / 2];
        for (int i = 0; i < NumberChars; i += 2)
            bytes[i / 2] = (byte)Integer.valueOf(data.substring(i, i+2),16).intValue();
        return bytes;
    }
    /**
     * 把double型数据转换成byte类型
     * @param d
     * @return
     */
    public static byte[] doubleToByte(double d){
        byte[] b=new byte[8];
        long l=Double.doubleToLongBits(d);
        for(int i=0;i<b.length;i++){
            b[i]=new Long(l).byteValue();
            l=l>>8;
        }
        return b;
    }
    /**
     * 把int型数据转换成byte类型
     * @param number
     * @return
     */
    public static byte[] intToByte(int number) {
        int temp = number;
        byte[] b=new byte[4];
        for (int i=b.length-1;i>-1;i--){
            b[i] = new Integer(temp&0xff).byteValue();      //将最高位保存在最低位
            temp = temp >> 8;       //向右移8位
        }
        return b;
    }

    /**
     * 把byte型数据转换成long类型
     * @param bb
     * @param index
     * @return
     */
    public static long getLong(byte[] bb, int index) {
        return ((((long) bb[index + 0] & 0xff) << 56)
                | (((long) bb[index + 1] & 0xff) << 48)
                | (((long) bb[index + 2] & 0xff) << 40)
                | (((long) bb[index + 3] & 0xff) << 32)
                | (((long) bb[index + 4] & 0xff) << 24)
                | (((long) bb[index + 5] & 0xff) << 16)
                | (((long) bb[index + 6] & 0xff) << 8) | (((long) bb[index + 7] & 0xff) << 0));
    }

}

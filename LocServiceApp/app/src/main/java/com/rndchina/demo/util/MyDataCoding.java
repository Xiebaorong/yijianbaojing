package com.rndchina.demo.util;

/**
 * Created by dell on 2017-8-28.
 */

public class MyDataCoding {
    public byte[] ushortToBytes(int n) {
        byte[] b = new byte[2];
        b[1] = (byte) ( n       & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }
    public byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }
    //用新的协议重新写2个类
    public byte[] GpsFram(String gpsId,double longitudes,double latitudes,int dspeed,int  dir,String time){
        // 定义发送报文的包体大小

        byte[] Buff = new byte[61];
        // 头定义
        Buff[0] = (byte)0xAA;
        Buff[1] = (byte)0xAA;
        Buff[2] = (byte)0xCC;
        Buff[3] = (byte)0xCC;
        Buff[4] = (byte)0x22;
        Buff[5] = (byte)0x00;

        // 包体大小
        int nBody = 51;
        //61 转化为网络字节序

        byte []body = intToBytes(nBody);

        int pos = 6;
        for(int i=0;i<4;i++) {
            // Buff[i+6] = body[i];
            Buff[pos] = body[i];
            pos++;
        }

        // body
        //GPSID设备编号
        byte []temp = new byte[20];
        //gpsId = "GZMZ0120039";//测试用gpsId
        //使用平台的默认字符集将此 String 编码为 byte 序列，并将结果存储到一个新的 byte 数组中.
        temp =  gpsId.getBytes();
        //System.out.println(temp.length);
        //当实际长度不足20字节时，右补二进制0
        for(int i=0;i<temp.length;i++){
            Buff[pos] = temp[i];
            pos++;
        }
        for(int i=0;i<20-temp.length;i++){
            Buff[pos] = 0x00;
            pos++;
        }

        // System.out.println("gpsid = "+gpsId);

        // 经度字段
        //double longitudes = 0.0;
        byte []dx =  BitConverter.doubleToByte(longitudes);
        for(int i= 0;i<8;i++)
            Buff[pos+i] = dx[i];
        pos += 8;
        System.out.println("经度为："+longitudes);
        // 纬度字段
        //double latitudes = 0.0;
        byte []dy = BitConverter.doubleToByte(latitudes);
        for(int i= 0;i<8;i++)
            Buff[pos +i] =dy[i];
        pos += 8;
        System.out.println("纬度为："+latitudes);
        // 速度字段
        // int dspeed = 0;
        byte []speeds = ushortToBytes(dspeed);
        Buff[pos ] = speeds[1];
        Buff[pos +1 ] = speeds[0];
        pos += 2;
        //方向字段
        //int  dir = 0;
        byte []dirs = ushortToBytes(dir);
        Buff[pos ] = dirs[1];
        Buff[pos +1 ] = dirs[0];
        pos += 2;

        // 高度
        double altitude =0;
        byte[] altitudeTT = BitConverter.intToByte((int)altitude);
        Buff[pos] = altitudeTT[1];
        Buff[pos +1] = altitudeTT[0];
        pos += 2;

        //精度
        Buff[pos] = (byte)0x00;
        Buff[pos+1] = (byte)0x00;
        pos += 2;

        try{

            String years ="2013";
            String month ="05";
            String day ="10";
            String hours ="12";
            String minutes ="10";
            String seconds ="10";

            int nYear = Integer.parseInt(years);
            byte []year = ushortToBytes(nYear);
            Buff[pos ] = year[1];
            Buff[pos +1 ] = year[0];
            pos += 2;

            int nMon = Integer.parseInt(month);
            Buff[pos] = (byte)nMon;
            pos++;

            int nDay = Integer.parseInt(day);
            Buff[pos] = (byte)nDay;
            pos++;

            int nHour = Integer.parseInt(hours);
            Buff[pos] = (byte)nHour;
            pos++;

            int nMin = Integer.parseInt(minutes);
            Buff[pos] = (byte)nMin;
            pos++;

            int nSec = Integer.parseInt(seconds);
            Buff[pos] = (byte)nSec;
        }catch (Exception e) {
            e.printStackTrace();
         }
        return Buff;
    }

}

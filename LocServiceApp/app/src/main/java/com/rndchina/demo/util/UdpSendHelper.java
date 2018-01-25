package com.rndchina.demo.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import android.net.wifi.WifiManager;
import android.util.Log;

/**
 *
 * UdpHelper帮助类
 *
 * @author caimingfu
 *
 */
public class UdpSendHelper extends Thread {
    private final static  String TAG = UdpSendHelper.class.getSimpleName();
    private static UdpSendHelper sendThread;

    private volatile boolean isExit = false;

    List<byte[]> bufferList = new ArrayList<byte[]>();
    private DatagramSocket socket;

    private int port = 9999;
    private String ip = "192.168.199.150";

    private UdpSendHelper() {
        try {
            socket = new DatagramSocket();
            ip =LocalDataUtils.getInstance().getServerIp();
            port = Integer.parseInt(LocalDataUtils.getInstance().getServerPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static synchronized UdpSendHelper getInstance() {
        if (sendThread == null) {
            sendThread = new UdpSendHelper();
            sendThread.start();
        }

        return sendThread;
    }

    public void exit() {

        synchronized (bufferList){
            isExit = true;
            bufferList.notifyAll();
        }
        if(socket != null){
            socket.close();
            socket = null;
        }



    }

    @Override
    public void run() {
        try{
            while (!isExit) {
                synchronized (bufferList) {
                    if (bufferList.size() == 0) {
                        bufferList.wait();
                    } else {

                        try {
                            byte[] data = bufferList.remove(0);
                            if(socket != null){
                                DatagramPacket packet = new DatagramPacket(data, data.length);
                                packet.setPort(port);
                                packet.setAddress(InetAddress.getByName(ip));
                                socket.send(packet);
                                System.out.println("发送数据长度:" + data.length + "字节");
                            }else{
                                isExit =true;
                            }
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                            isExit =true;
                            if(socket != null){
                                socket.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            isExit =true;
                            if(socket != null){
                                socket.close();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG,ex.toString());
        }

    }

    public  void addBuffer(byte[] buf) {
        synchronized(bufferList){
            bufferList.add(buf);
            bufferList.notify();
        }
    }
}

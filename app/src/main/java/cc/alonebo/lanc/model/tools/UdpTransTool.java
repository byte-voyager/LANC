package cc.alonebo.lanc.model.tools;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.model.bean.UdpTransMsg;
import cc.alonebo.lanc.model.listener.UdpMsgListener;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.UdpTransMsg2Byte;

/**
 * Created by alonebo on 17-5-16.
 */

public class UdpTransTool {

    private static boolean isReceiving = false;
    private UdpMsgListener mUdpMsgListener;
    private static UdpTransTool mUdpTransTool = null;
    private ExecutorService mSendExecutor = Executors.newSingleThreadExecutor();
    private  ExecutorService mReceiveExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService mCachedThreadPool = Executors.newCachedThreadPool();
    private String TAG = UdpTransTool.class.getName();
    /**
     * @return 返回UDPTransTool4Single的实例,双重判断
     */
    public static UdpTransTool getInstance() {
        if (mUdpTransTool == null) {
            synchronized (UdpTransTool.class) {
                if (mUdpTransTool == null) {
                    mUdpTransTool = new UdpTransTool();

                }
            }
        }
        return mUdpTransTool;
    }

    /**
     * @param udpMsgListener 设置一个收到信息的回调
     */
    public void setTransMsgCallBack(UdpMsgListener udpMsgListener) {
        this.mUdpMsgListener = udpMsgListener;
    }
    /**
     * @param msg 要发送的信息类,发送的端口为Constants.PORT_UDP
     */
    public  void sendMsg(final UdpTransMsg msg) {
        mSendExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //用DatagramSocket来发送消息
                DatagramSocket ds = null;
                try {
                    ds = new DatagramSocket();
                    byte[] needTransByte = UdpTransMsg2Byte.getBytes(msg);
                    InetAddress receiverInetAddress = InetAddress.getByName(msg.getReceiverIP());//指定发送到哪个IP
                    LogUtils.e(TAG,"要发送到:->"+receiverInetAddress.getHostAddress()+"的地址");
                    DatagramPacket transDp = new DatagramPacket(needTransByte,needTransByte.length,
                            receiverInetAddress, Constants.PORT_UDP);
                    ds.send(transDp);
                    LogUtils.e(TAG,"发送信息类型:"+msg.getTransType());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null){
                        if (!ds.isClosed()) ds.close();
                    }
                }
            }
        });
    }

    public void startReceiveMsg() {

        if (isReceiving) {
            LogUtils.e(TAG,"重复开启接收udp线程接收消息");
            return;
        }
        receiveAlways();
    }

    /**
     * 用来接收其他用户发送的数据,此方法会开启线程不断循环接收数据
     * 接收到后会调用handleTransMsg(TransMsg msg)来处理接收到
     * 的数据
     */
    private void receiveAlways() {
        isReceiving = true;
        mReceiveExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    DatagramSocket ds = null;
                    try {
                        //监听接收端口
                        ds = new DatagramSocket(Constants.PORT_UDP);
                        byte[] data = new byte[1024 * 1024];
                        DatagramPacket dp = new DatagramPacket(data, data.length);
                        ds.receive(dp);
                        byte[] data2 = new byte[dp.getLength()];
                        System.arraycopy(data, 0, data2, 0, data2.length);// 得到接收的数据
                        final UdpTransMsg msg = UdpTransMsg2Byte.getTramsMsg(data2);
                        if (mUdpMsgListener !=null) {
                            mCachedThreadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mUdpMsgListener.onNewUdpMsg(msg);
                                }
                            });

                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    finally {
                        if (ds != null)
                            if (!ds.isClosed()) ds.close();
                    }
                }

            }
        });
    }
}

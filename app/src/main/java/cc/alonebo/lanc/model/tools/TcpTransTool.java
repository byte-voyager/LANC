package cc.alonebo.lanc.model.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.R;
import cc.alonebo.lanc.model.bean.TcpTransMsg;
import cc.alonebo.lanc.model.listener.TcpMsgListener;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.Utils;

/**
 * Created by alonebo on 17-5-16.
 */

public class TcpTransTool {
    private ServerSocket mSs;
    private static TcpTransTool sTcpTransTool;
    private TcpMsgListener mListener;


    private ExecutorService sendExecutor  = Executors.newSingleThreadExecutor();
    private ExecutorService receiveExecutor = Executors.newSingleThreadExecutor();
    private String TAG = TcpTransTool.class.getName();
    private ExecutorService mCachedThreadPool = Executors.newCachedThreadPool();

    /**
     * set a mListener when received a TcpTransMsg will call TcpMsgListener.onNewTcpMsg
     * @param listener the TcpMsg Message mListener
     */
    public void setTcpTransMsgListener(TcpMsgListener listener) {
        this.mListener = listener;
    }

    /**
     * TcpTransTool base Tcp socket,this method will get a instance to
     * send tcp message or receive tcp message
     * @return return instance for TcpTransTool
     *
     */
    public static TcpTransTool getInstance() {
        if (sTcpTransTool == null) {
            synchronized (TcpTransTool.class) {
                if (sTcpTransTool == null) {
                    sTcpTransTool = new TcpTransTool();
                }
            }
        }
        return sTcpTransTool;
    }

    private TcpTransTool() {

        try {
            mSs = new ServerSocket(Constants.PORT_TCP_AVATAR_PORT);
            LogUtils.i(TAG,"create tcp client success!");
        } catch (IOException e) {
            Utils.showToast(Utils.getContext(),Utils.getString(R.string.toast_bind_tcp_faile));
            e.printStackTrace();
        }
    }


    /**
     * start receive Tcpmsg,will callback onNewTcpMsg method;
     * @return
     */
    public void startReceiveTcpMsg() {
        Runnable receiveRunnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Socket socket = null;
                    ObjectInputStream is = null;
                    try {
                        socket = mSs.accept();
                        is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                        Object obj = is.readObject();
                        final TcpTransMsg msg = (TcpTransMsg)obj;
                        if (mListener !=null) {
                            mCachedThreadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mListener.onNewTcpMsg(msg);
                                }
                            });

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (socket!=null) socket.close();
                            if (is!=null) is.close();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        };

        receiveExecutor.execute(receiveRunnable);
    }

    /**
     *  to send a TcpTransMsg whit tcp protocol
     * @param msg what object will send
     */
    public boolean sendTcpMsg(final TcpTransMsg msg) {

        Runnable sendRunnable = new Runnable() {
            @Override
            public void run() {
                Socket s = null;
                try {
                    ObjectOutputStream os = null;
                    LogUtils.e(TAG,"发送到-->"+msg.getReceiverIp());
                    s = new Socket(msg.getReceiverIp(),Constants.PORT_TCP_AVATAR_PORT);
                    os = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
                    os.writeObject(msg);
                    os.flush();
                    LogUtils.e(TAG,"发送到-->"+msg.getReceiverIp());

                } catch (IOException e) {

                    e.printStackTrace();

                }finally {
                    if (s!=null) {
                        try {
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        sendExecutor.execute(sendRunnable);
        return true;
    }
}

package cc.alonebo.lanc.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import cc.alonebo.lanc.model.bean.TcpTransMsg;

/**
 * Created by alonebo on 17-4-15.
 */

public class TcpTransMsg2ByteUtils {


        public static byte[] getBytes(TcpTransMsg msg) {
            byte[] bytes = null;
            ByteArrayOutputStream bos = null;
            ObjectOutputStream oos = null;
            try {
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(msg);
                oos.flush();
                bytes = bos.toByteArray();

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return bytes;
        }

        public static TcpTransMsg getTcpTramsMsg(byte[] bytes) {
            if (bytes==null) {return null;}
            TcpTransMsg msg = null;
            ByteArrayInputStream bis = null;
            ObjectInputStream ois = null;
            try {
                bis = new ByteArrayInputStream(bytes);
                ois = new ObjectInputStream(bis);
                msg = (TcpTransMsg) ois.readObject();
                ois.close();
                bis.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (ois != null) {
                        try {
                            ois.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            return msg;
        }
}

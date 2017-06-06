package cc.alonebo.lanc;

/**
 * Created by alonebo on 17-5-16.
 */

public class Constants {
    public static final String SP_LOCAL_IP = "LOCAL_IP";
    public static final String SP_IS_SHOW_NOTIFI = "IS_OPEN_NOTI";
    public static final String SP_DEVICE_NAME = "DEVICE_NAME";
    public static final String SP_DEVICE_ID = "DEVICE_ID";
    public static final String SP_BING_LINK = "BING_LINK";

    public static final int PORT_TCP_AVATAR_PORT = 7654;//tcp端口
    public static final int PORT_UDP = 8901;//udptranstool的接收端口
    public static final int PROT_TCP_FILE_PROT = 8900;

    public static final int TRANS_TYPE_ONLINE = 0;//上线信息
    public static final int TRANS_TYPE_RESP_ONLINE = 1;
    public static final int TRANS_TYPE_MESSAGE = 3;
    public static final int TRANS_TYPE_REQUEST_RECEIVE_FILE = 6;
    public static final int TRANS_TYPE_REQUEST_TRANS_FILE_OK = 7;
    public static final int TRANS_TYPE_REQUEST_TRANS_FILE_FAILED = 8;
    public static final int MESSAGE_NEW_CONTACT = 2;
    public static final int TRANS_TYPE_TCP_REQUEST_DETAIL_MSG = 20;
    public static final int TRANS_TYPE_TCP_RESP_DETAIL_MSG = 21;
    public static final int TRANS_TYPE_REQUEST_DETAIL_MSG = 22;
    public static final int TRANS_TYPE_UPDATE_AVATAR = 23;
    public static final int TRANS_TYPE_TRANSING_FILE = 24;
    public static final int TRANS_TYPE_COMMAND = 25;
    public static final int TYPE_ONESELF = 4;
    public static final int TYPE_OTHER = 5;

    public static final int EVENT_TYPES_CHOICE_DOC = 9;
    public static final int EVENT_TYPES_CHOICE_ZIP = 11;
    public static final int EVENT_TYPES_CHOICE_APK = 12;
    public static final int EVENT_TYPES_CHOICE_VIDEO = 13;
    public static final int EVENT_TYPES_CHOICE_CUSTOM = 14;


    public static final int TYPE_NOTIFICATION_UPLOAD_FILE = 90;
    public static final int TYPE_NOTIFICATION_UPLOAD_FILE_SUCESS = 91;
    public static final int TYPE_NOTIFICATION_RECIVE_FILE = 92;
    public static final int TYPE_NOTIFICATION_RECIVE_FILE_SUCESS = 93;
    public static final int TYPE_NOTIFICATION_NEW_UPLOAD_FILE = 94;
    public static final int TYPE_NOTIFICATION_NEW_RECIVE_FILE = 95;
    public static final int TYPE_NOTIFICATION_FTP_RUNNING = 96;

    public static final String NAME_AVATAR = "avatar.png";


    public static final String SP_HAVE_CUSTOM_AVATAR = "HAVE_CUSTOM_AVATOR";

    public static final String SP_AVATAR_TIME = "AVATAR_TIME";


    public static final int PORT_FTP = 8911;

    public static final String NAME_DEFAULT_FTP_USER_NAME = "lanc";
    public static final String SP_DEVICE_TCP_PORT = "SP_DEVICE_TCP_PORT";
    public static final java.lang.String TRANS_PREFIX_CONTROL = "remote:";



    public static  boolean IS_TRANSINT_FILE = false;

    public static String CURRENT_CHAT_IDENT = "";
}


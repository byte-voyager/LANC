package cc.alonebo.lanc.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cc.alonebo.lanc.db.ContactDBHelper;
import cc.alonebo.lanc.MyContactList;
import cc.alonebo.lanc.model.bean.ContactBean;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.SPUtils;


/**
 * 联系人Contact表的操作类
 * Created by alonebo on 17-4-22.
 */

public class ContactDao {
    private String TAG = ContactDao.class.getName();
    public static int IS_ONLINE_TYPE_ONLINE = 1;
    public static int IS_ONLINE_TYPE_OFFLINE = 0;
    public static int MSG_TYPE_LAST_CHAT_MSG_NOT_READ = 1;

    public static int MSG_TYPE_NORMAL = 0;

    private String COLUM_AVATAR_TIME = "avatar_time";
    private String COLUM_IP = "ip";
    private String COLUM_NAME = "name";
    private String COLUM_DEVICE_IDENT = "device_ident";
    private String COLUM_IS_ONLINE = "is_online";
    private String COLUM_LAST_CHAT_TIME = "last_chat_time";
    private String COLUM_LAST_CHAT_MSG = "last_chat_msg";
    private String COLUM_IS_TRANSING = "is_transing";
    private String COLUM_NOT_READ_COUNT = "not_read_count";
    private String COLUM_MSG_TYPE = "msg_type";
    private String COLUM_ONLINE_TIME = "online_time";

    private String TABLE_NAME = "Contact";
    private ContactDBHelper helper;
    private static ContactDao contactDao;
    private ContactDao(Context context) {
        helper= new ContactDBHelper(context);
    }
    public static ContactDao getInstance(Context context) {
        if (contactDao==null) {
            contactDao = new ContactDao(context);
        }
       return contactDao;
    }

    /**
     * 用于添加一个联系人
     * 如果存在deviceSerial标记序列号的联系人,就会更新联系人信息
     * 而不会再次添加一个
     * @param ip 联系人ip
     * @param name 联系人名字
     * @param deviceIdent 联系人设备序列号
     *
     */
    public void insertOnlineContact(String ip, String name, String deviceIdent,long onLineTime) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (isExistContact(deviceIdent)) {
            //如果存在联系人就更新联系人,根据device_serial判断
            LogUtils.e("insertOnlineContact","存在联系人"+name+";更新联系人");
            updateOnLineContact(deviceIdent,ip,name,IS_ONLINE_TYPE_ONLINE,onLineTime);
        } else {
            LogUtils.e("insertOnlineContact","不存在联系人"+name+";插入联系人");
            ContentValues cv = new ContentValues();
            cv.put(COLUM_IP,ip);
            cv.put(COLUM_NAME,name);
            cv.put(COLUM_DEVICE_IDENT,deviceIdent);
            cv.put(COLUM_IS_ONLINE,IS_ONLINE_TYPE_ONLINE);
            cv.put(COLUM_ONLINE_TIME,onLineTime);
            db.insert(TABLE_NAME,null,cv);
        }

    }

    /**
     * 用于更新最后聊天的信息,时间,和消息类型
     * @param lastChatMsg 最后聊天的信息
     * @param lastChatTime 最后聊天的时间
     * @param msgType 消息类型
     * @param deviceIdent 传入device_serial
     */
    public void updateLastChatMsg(String deviceIdent ,String lastChatMsg, long lastChatTime, int msgType) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_LAST_CHAT_MSG,lastChatMsg);
        cv.put(COLUM_LAST_CHAT_TIME,lastChatTime);
        cv.put(COLUM_MSG_TYPE,msgType);
        db.update(TABLE_NAME,cv,"device_ident = ?",new String[]{deviceIdent});
        LogUtils.e("Tag","lastChatMsg:"+lastChatMsg);
    }

    /**
     * 用于得到联系人信息,如果没有该联系人,返回的size=0
     * 反之,例如msgType可能为空
     * @param deviceIdent 设备序列号,联系人的标识
     * @return
     */
    public ContactBean queryContact(String deviceIdent) {
        ContactBean contact = new ContactBean();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Contact where device_ident = ?", new String[]{deviceIdent});
        if (cursor.moveToFirst()) {
            do {
                contact.setIp(cursor.getString(cursor.getColumnIndex(COLUM_IP)));
                contact.setName(cursor.getString(cursor.getColumnIndex(COLUM_NAME)));
                contact.setDeviceIdent(cursor.getString(cursor.getColumnIndex(COLUM_DEVICE_IDENT)));
                contact.setIsOnline(cursor.getInt(cursor.getColumnIndex(COLUM_IS_ONLINE)));
                contact.setLastChatMsg(cursor.getString(cursor.getColumnIndex(COLUM_LAST_CHAT_MSG)));
                contact.setLastChatTime(cursor.getLong(cursor.getColumnIndex(COLUM_LAST_CHAT_TIME)));
                contact.setIsTransing(cursor.getInt(cursor.getColumnIndex(COLUM_IS_TRANSING)));
                contact.setMsgType(cursor.getInt(cursor.getColumnIndex(COLUM_MSG_TYPE)));
                contact.setNotReadCount(cursor.getInt(cursor.getColumnIndex(COLUM_NOT_READ_COUNT)));
                contact.setOnLineTime(cursor.getLong(cursor.getColumnIndex(COLUM_ONLINE_TIME)));
                contact.setAvatarTime(cursor.getLong(cursor.getColumnIndex(COLUM_AVATAR_TIME)));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return contact;
    }


    /** 用于更新联系人
     * @param deviceIdent 设备序列号
     * @param ip 设备ip
     * @param name 联系人名字
     * @param isOnline  是否在线
     */
    public void updateOnLineContact(String deviceIdent, String ip, String name, int isOnline, long onLineTime) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_IP,ip);
        cv.put(COLUM_NAME,name);
        cv.put(COLUM_IS_ONLINE,isOnline);
        cv.put(COLUM_ONLINE_TIME,onLineTime);
        db.update(TABLE_NAME,cv,COLUM_DEVICE_IDENT + " = ?",new String[]{deviceIdent});
    }

    /**
     * 用于检测是否存在该联系人 有则返回true,没有
     * 返回false
     * @param deviceIdent 设备id
     * @return
     */
    public boolean isExistContact(String deviceIdent) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where "+COLUM_DEVICE_IDENT+" = ?", new String[]{deviceIdent});
        if (cursor.getCount()==0) {
            return false;
        }else {
            return true;
        }
    }

    /**
     * 用于更新未读数据
     * @param deviceIdent 设备id
     * @param notReadCount 未读消息数
     */
    public void updateNotReadCount(String deviceIdent,int notReadCount) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_NOT_READ_COUNT,notReadCount);
        db.update(TABLE_NAME,cv,COLUM_DEVICE_IDENT+" = ?",new String[]{deviceIdent});
    }

    /**
     * 用于更新最后发送消息的时间
     * @param deviceIdent 设备系列号
     * @param lastChatTime  最后发送消息的时间
     */
    public void updateLastChatTime(String deviceIdent,long lastChatTime) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_LAST_CHAT_TIME,lastChatTime);
        db.update(TABLE_NAME,cv,COLUM_DEVICE_IDENT+" = ?",new String[]{deviceIdent});
    }

    /**
     * 用于更新最后发送的消息和消息类型
     * @param deviceIdent  设备系列号
     * @param lastChatMsg   最后发送消息的消息
     * @param msgType   消息类型
     */
    public void updateLastChatMsg(String deviceIdent, String lastChatMsg,int msgType) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_LAST_CHAT_MSG,lastChatMsg);
        cv.put(COLUM_MSG_TYPE,msgType);
        db.update(TABLE_NAME,cv,COLUM_DEVICE_IDENT+" = ?",new String[]{deviceIdent});
    }

    public void updateLastChatMsg(String deviceIdent,int msgType,int notReadCount){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_MSG_TYPE,msgType);
        cv.put(COLUM_NOT_READ_COUNT,notReadCount);
        db.update(TABLE_NAME,cv,COLUM_DEVICE_IDENT +" = ?",new String[]{deviceIdent});
    }

    public void updateLastChatMsg(String deviceIdent,String lastChatMsg, long lastChatTime,int msgType,boolean isAddNotReadCount) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_LAST_CHAT_MSG,lastChatMsg);
        cv.put(COLUM_LAST_CHAT_TIME,lastChatTime);
        cv.put(COLUM_MSG_TYPE,msgType);
        if (isAddNotReadCount) {
            int notReadCount = queryNotReadCount(deviceIdent);
            notReadCount++;
            cv.put(COLUM_NOT_READ_COUNT,notReadCount);
        }
        db.update(TABLE_NAME,cv,COLUM_DEVICE_IDENT +" = ?",new String[]{deviceIdent});
    }

    public void deleteContact(String deviceIdent) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int delete = db.delete(TABLE_NAME, COLUM_DEVICE_IDENT + " = ?", new String[]{deviceIdent});
        LogUtils.e(TAG,"删除成功!:"+delete);
    }

    public MyContactList queryAll() {
        MyContactList list = new MyContactList();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
        if (cursor.moveToFirst()) {
            do {
                ContactBean contact = new ContactBean();
                contact.setIp(cursor.getString(cursor.getColumnIndex(COLUM_IP)));
                contact.setName(cursor.getString(cursor.getColumnIndex(COLUM_NAME)));
                contact.setDeviceIdent(cursor.getString(cursor.getColumnIndex(COLUM_DEVICE_IDENT)));
                contact.setIsOnline(cursor.getInt(cursor.getColumnIndex(COLUM_IS_ONLINE)));
                contact.setLastChatMsg(cursor.getString(cursor.getColumnIndex(COLUM_LAST_CHAT_MSG)));
                contact.setLastChatTime(cursor.getLong(cursor.getColumnIndex(COLUM_LAST_CHAT_TIME)));
                contact.setIsTransing(cursor.getInt(cursor.getColumnIndex(COLUM_IS_TRANSING)));
                contact.setMsgType(cursor.getInt(cursor.getColumnIndex(COLUM_MSG_TYPE)));
                contact.setNotReadCount(cursor.getInt(cursor.getColumnIndex(COLUM_NOT_READ_COUNT)));
                contact.setOnLineTime(cursor.getLong(cursor.getColumnIndex(COLUM_ONLINE_TIME)));
                contact.setAvatarTime(cursor.getLong(cursor.getColumnIndex(COLUM_AVATAR_TIME)));
                list.add(contact);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }


    public String queryContactLastChatMsg(String deviceIdent) {
        String result = "";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select "+COLUM_LAST_CHAT_MSG+" from "+TABLE_NAME+" where "+COLUM_DEVICE_IDENT+" = ?", new String[]{deviceIdent});
        if (cursor.moveToFirst()) {
            do{
                result = cursor.getString(cursor.getColumnIndex(COLUM_LAST_CHAT_MSG));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public int queryNotReadCount(String deviceIdent) {
        int result = 0;
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("select not_read_count from Contact where device_ident = ?", new String[]{deviceIdent});
        if (cursor.moveToFirst()) {
            do{
                result = cursor.getInt(cursor.getColumnIndex(COLUM_NOT_READ_COUNT));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public void updateAvaratTime(String deviceIdent,long avatarTime) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_AVATAR_TIME,avatarTime);
        db.update(TABLE_NAME,cv,COLUM_DEVICE_IDENT +" = ?",new String[]{deviceIdent});
    }

    public void updateNotReadCountLastChatMsgTime(String deviceIdent,int notReadCount,String lastChatMsg,long lastChatTime, int msgType) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_NOT_READ_COUNT,notReadCount);
        cv.put(COLUM_LAST_CHAT_MSG,lastChatMsg);
        cv.put(COLUM_LAST_CHAT_TIME,lastChatTime);
        cv.put(COLUM_MSG_TYPE,msgType);
        db.update(TABLE_NAME,cv,COLUM_DEVICE_IDENT +" = ?",new String[]{deviceIdent});
    }

    public void updateLastChatMsgType(String deviceIdent,int msgType) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_MSG_TYPE,msgType);
        db.update(TABLE_NAME,cv,COLUM_DEVICE_IDENT +" = ?",new String[]{deviceIdent});
    }
}

package cc.alonebo.lanc.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cc.alonebo.lanc.db.ChatMessageDBHelper;
import cc.alonebo.lanc.model.bean.ChatMessageBean;


/**
 * Created by alonebo on 17-4-22.
 */

public class ChatMessageDao {
    //ip text, device_ident text, msg_type integer,msg text,msg_time integer
    private String COLUM_IP = "ip";
    private String COLUM_DEVICE_IDENT = "device_ident";
    private String COLUM_MSG_TYPE = "msg_type";
    private String COLUM_MSG = "msg";
    private String COLUM_MSG_TIME = "msg_time";
    private String TABLE_NAME = "ChatMessage";

    private ChatMessageDBHelper helper;
    private static ChatMessageDao chatMessageDao;
    private ChatMessageDao(Context context) {
                helper = new ChatMessageDBHelper(context);
    }

    public static ChatMessageDao getInstance(Context context) {
        if (chatMessageDao==null) {
            chatMessageDao = new ChatMessageDao(context);
        }
       return chatMessageDao;
    }


    public void insertChatMessage(String message,long messageTime,String ip,String deviceIdent, int messageType) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_MSG,message);
        cv.put(COLUM_MSG_TIME,messageTime);
        cv.put(COLUM_IP,ip);
        cv.put(COLUM_DEVICE_IDENT,deviceIdent);
        cv.put(COLUM_MSG_TYPE,messageType);
        db.insert(TABLE_NAME,null,cv);
    }

    public void deleteChatMessage(String deviceIdent) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_NAME,"device_ident = ?",new String[]{deviceIdent});
    }

    public ArrayList<ChatMessageBean> queryChatMessage(String deviceIdent) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<ChatMessageBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from ChatMessage where device_ident = ?", new String[]{deviceIdent});
        if (cursor.moveToFirst()) {
            do {
                ChatMessageBean message = new ChatMessageBean();
                message.setIp(cursor.getString(cursor.getColumnIndex(COLUM_IP)));
                message.setDeviceIdent(cursor.getString(cursor.getColumnIndex(COLUM_DEVICE_IDENT)));
                message.setMessage(cursor.getString(cursor.getColumnIndex(COLUM_MSG)));
                message.setMessageTime(cursor.getLong(cursor.getColumnIndex(COLUM_MSG_TIME)));
                message.setMessageType(cursor.getInt(cursor.getColumnIndex(COLUM_MSG_TYPE)));
                list.add(message);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public void deleteAllChatMessage() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
    }
}

package cc.alonebo.lanc.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cc.alonebo.lanc.db.AvatarDBHelper;
import cc.alonebo.lanc.utils.LogUtils;


/**
 * Created by alonebo on 17-4-23.
 */

public class AvatarDao {
    //create table avatar(id integer primary key autoincrement, avatar_ident_md5 text, avatar_ident text, avatar_md5 text, avatar_time integer)";
    private final String COLUM_AVATAR_IDENT = "avatar_ident";
    private final String COLUM_AVATAR_TIME = "avatar_time";
    private final String COLUM_AVATAR_SAVE_NAME = "avatar_save_name";//avatar_save_name = deviceIdent_avatarTime

    private final String TABLE_NAME = "avatar";
    private String TAG = AvatarDao.class.getName();

    private static AvatarDao avatarDao;
    private AvatarDBHelper helper;
    private AvatarDao(Context context) {
        helper = new AvatarDBHelper(context);
    }

    public static AvatarDao getIntance(Context context) {
        if (avatarDao==null) {
            avatarDao = new AvatarDao(context);
        }
        return avatarDao;
    }


    public void insertAvatar(String deviceIdent,long time) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (!isExistAvatar(deviceIdent)) {//不存在就插入
            LogUtils.e(TAG,"nit exit the avatar for:"+deviceIdent);
            ContentValues cv = new ContentValues();
            cv.put(COLUM_AVATAR_IDENT,deviceIdent);
            cv.put(COLUM_AVATAR_TIME,time);
            cv.put(COLUM_AVATAR_SAVE_NAME,deviceIdent+"_"+time);
            db.insert(TABLE_NAME,null,cv);
        }else {
            ContentValues cv = new ContentValues();
            cv.put(COLUM_AVATAR_TIME,time);
            cv.put(COLUM_AVATAR_SAVE_NAME,deviceIdent+"_"+time);
            db.update(TABLE_NAME,cv,COLUM_AVATAR_IDENT + " = ?",new String[]{deviceIdent});
        }
    }

    public boolean isExistAvatar(String deviceIdent) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where "+COLUM_AVATAR_IDENT+" = ?", new String[]{deviceIdent});
        if (cursor.getCount()==0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }


    public long queryAvatarTime(String deviceIdent) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select "+COLUM_AVATAR_TIME+" from "+TABLE_NAME+" where "+COLUM_AVATAR_IDENT+" = ?", new String[]{deviceIdent});
        long result = 0;
        if (cursor.moveToFirst()) {
            do{
                result = cursor.getLong(cursor.getColumnIndex(COLUM_AVATAR_TIME));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }


    public String queryAvatarSaveName(String deviceIdent) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select "+COLUM_AVATAR_TIME+" from "+TABLE_NAME+" where "+COLUM_AVATAR_IDENT+" = ?", new String[]{deviceIdent});
        String result = "";
        if (cursor.moveToFirst()) {
            do{
                result = cursor.getString(cursor.getColumnIndex(COLUM_AVATAR_SAVE_NAME));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public boolean isNeedUpdateAvatar(String deviceIdent,long avatarTime) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select "+COLUM_AVATAR_TIME+" from "+TABLE_NAME+" where "+COLUM_AVATAR_IDENT+" = ?", new String[]{deviceIdent});

        if (cursor.getCount()==0) {//not fond
            return true;
        }
        return false;

    }

}

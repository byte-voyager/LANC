package cc.alonebo.lanc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by alonebo on 17-4-23.
 */

public class AvatarDBHelper extends SQLiteOpenHelper {
    private static String NAME_DB_AVATAR = "avatar.db";
    private static String TABLE_NAME = "avatar";
    private String SQL_CREATE_TABLE_AVATAR = "create table avatar(id integer primary key autoincrement,avatar_ident text, avatar_time integer, avatar_save_name text)";
    public AvatarDBHelper(Context context) {
        super(context, NAME_DB_AVATAR, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_AVATAR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

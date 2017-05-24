package cc.alonebo.lanc.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alonebo on 17-3-24.
 */

public class ContactDBHelper extends SQLiteOpenHelper {
    private static final String SQL_CONTACT = "create table Contact (id integer primary key autoincrement, ip text, name text, device_ident text, is_online integer, last_chat_time integer, last_chat_msg text, is_transing integer, not_read_count integer, msg_type integer, online_time integer, avatar_time long)";
    private static final String NAME_DB_CONTACT = "contact.db";

    public ContactDBHelper(Context context) {
        super(context, NAME_DB_CONTACT, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CONTACT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Contact");
        db.execSQL(SQL_CONTACT);
    }
}

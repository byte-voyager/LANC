package cc.alonebo.lanc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alonebo on 17-4-22.
 */

public class ChatMessageDBHelper extends SQLiteOpenHelper {
    private String SQL_CHAT_MESSAGE = "create table ChatMessage(id integer primary key autoincrement, ip text, device_ident text, msg_type integer,msg text,msg_time integer)";
    private static final String NAME_DB_CONTACT = "message.db";
    public ChatMessageDBHelper(Context context) {
        super(context,NAME_DB_CONTACT,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CHAT_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

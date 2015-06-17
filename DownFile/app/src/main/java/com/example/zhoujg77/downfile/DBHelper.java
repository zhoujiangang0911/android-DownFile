package com.example.zhoujg77.downfile;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhoujg77 on 2015/6/13.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME= "download.db";
    private static final int VERSION = 1;
    private static final String SQL_CREATE = "CREATE TABLE thread_info(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "thread_id INTEGER ,url TEXT,start INTEGER,end INTEGER,finished INTEGER)";
    private static final String SQL_DROP= "DROP TABLE IF EXISTS thread_info";

    private static DBHelper dbHelper = null; //静态引用

    private  DBHelper(Context context) {

        super(context, DB_NAME, null, VERSION );
    }

    /**
     * 获得对象
     * @param db
     */

    public  static DBHelper getInstance(Context context){
        if (dbHelper==null){
            dbHelper = new DBHelper(context);

        }
            return dbHelper;

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}

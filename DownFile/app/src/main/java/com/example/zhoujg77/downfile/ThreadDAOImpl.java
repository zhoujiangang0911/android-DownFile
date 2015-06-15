package com.example.zhoujg77.downfile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoujg77 on 2015/6/15.
 */
public class ThreadDAOImpl implements ThreadDAO {
    private DBHelper mHelper = null;


    public ThreadDAOImpl(Context context) {
         mHelper = new DBHelper(context);
    }

    @Override
    public void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL("INSERT INTO thread_info (thread_id,url,start,end,finished) values(?,?,?,?,?)",new Object[]{
                threadInfo.getId(),threadInfo.getUrl(),threadInfo.getStart(),threadInfo.getEnd(),threadInfo.getFinished()
        });
        db.close();

    }

    @Override
    public void deleteThread(String url, int thread_id) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL("DELETE FROM thread_info WHERE  URL = ? AND THREAD_ID = ?",new Object[]{url,thread_id});
        db.close();



    }

    @Override
    public void updateThread(String url, int thread_id, int finished) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL("UPDATE thread_info SET finished  = ? WHERE URL = ? AND thread_id = ?",new Object[]{finished,url,thread_id});
        db.close();
    }

    @Override
    public List<ThreadInfo> getThreads(String url) {

        List<ThreadInfo>  list  = new ArrayList<>();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ?", new String[]{url});
        while (cursor.moveToNext()){
                ThreadInfo threadInfo = new ThreadInfo();
                threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
                threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
                threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
                threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
                list.add(threadInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public boolean isExists(String url, int thread_id) {
        boolean flag = false;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ?",
                new String[]{url,thread_id+""});
        flag = cursor.moveToNext();
        cursor.close();
        db.close();
        return flag;
    }
}

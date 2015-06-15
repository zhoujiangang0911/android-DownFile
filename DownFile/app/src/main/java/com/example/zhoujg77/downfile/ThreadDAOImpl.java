package com.example.zhoujg77.downfile;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by zhoujg77 on 2015/6/15.
 */
public class ThreadDAOImpl implements ThreadDAO {
    private DBHelper mHelper = null;


    public ThreadDAOImpl(DBHelper mHelper) {
        this.mHelper = mHelper;
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

    }

    @Override
    public void updateThread(String url, int thread_id, int finished) {

    }

    @Override
    public List<ThreadInfo> getThreads(String url) {
        return null;
    }

    @Override
    public boolean isExists(String url, int thread_id, int finished) {
        return false;
    }
}

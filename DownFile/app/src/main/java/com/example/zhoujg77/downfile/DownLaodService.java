package com.example.zhoujg77.downfile;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Switch;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.RandomAccess;

/**
 * Created by zhoujg77 on 2015/6/13.
 */
public class DownLaodService extends Service {
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE= "ACTION_UPDATE";

    public static final String ACTION_FINISH= "ACTION_FINISH";
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/aaaaa/";

    public static final int MSG_INIT = 0;

    //下载任务的集合
    private Map<Integer,DownLoadTask> mTasks = new LinkedHashMap<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获得ACTIVITY传递的参数
        if (ACTION_START.equals(intent.getAction())){
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("--zhoujg77","start"+fileInfo.toString());

            //初始化线程

            DownLoadTask.sExecutorService.execute(new InitThread(fileInfo));
        }else if (ACTION_STOP.equals(intent.getAction())){
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("--zhoujg77","stop"+fileInfo.toString());
            //从集合中取出下载任务
            DownLoadTask task = mTasks.get(fileInfo.getId());
            if (task != null){
                //停止下载任务
                task.isPause = true;
            }


        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_INIT:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.i("--zhoujg77","Init"+fileInfo.toString());
                    //启动下载任务
                  DownLoadTask task   = new DownLoadTask(DownLaodService.this,fileInfo,3);
                    task.downlaod();
                    //把下载任务添加到集合中
                    mTasks.put(fileInfo.getId(),task);
                    break;

                default:
                    break;
            }


        }
    };


    /**
     * 初始化线程
     */

    class  InitThread extends  Thread{
        public FileInfo fileInfo = null;

        public InitThread(FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile raf = null;
            try {
                URL url = new URL(fileInfo.getUrl());
                connection  = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                int length = -1;
                if (connection.getResponseCode()== HttpStatus.SC_OK){
                       length = connection.getContentLength();

                }
                 if(length<=0){
                        return;
                 }

                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()){
                    dir.mkdir();
                }
                File file = new File(dir,fileInfo.getFilename());
                //任意位置读写文件类型 rwd 文件模式 read write delete
                raf = new RandomAccessFile(file,"rwd");
                raf.setLength(length);
                fileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT,fileInfo).sendToTarget();


            }catch (Exception e){
                e.printStackTrace();
            }finally {
                connection.disconnect();
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }




}

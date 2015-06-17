package com.example.zhoujg77.downfile;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 下载任务类
 * Created by zhoujiangang on 2015/6/15.
 */
public class DownLoadTask {
    private Context context = null;
    private FileInfo fileInfo = null;
    private ThreadDAO dao = null;
    private int finished = 0;
    public boolean isPause = false;

    private int mThreadCount = 1;

    private List<DownLoadThread> mThreadList = null;

    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();//线程池



    public DownLoadTask(Context mcontext, FileInfo fileInfo,int mThreadCount) {
        this.context = mcontext;
        this.fileInfo = fileInfo;
        this.mThreadCount = mThreadCount;
        dao = new ThreadDAOImpl(mcontext);
    }

    public void downlaod(){
        //读取数据库的线程信息
        List<ThreadInfo> threadinfolist = dao.getThreads(fileInfo.getUrl());
        if (threadinfolist.size() == 0) {
            //获得每个线程下载长度
            int lenght = fileInfo.getLength()/ mThreadCount;
            for (int i = 0; i <mThreadCount;i++){
                //创建线程信息
                ThreadInfo threadinfo = new ThreadInfo(i,fileInfo.getUrl(),lenght*i,(i+1)*lenght-1,0);
                if (i== mThreadCount-1){
                    threadinfo.setEnd(fileInfo.getLength());
                }
                threadinfolist.add(threadinfo);
                //向数据库插入信息
                dao.insertThread(threadinfo);

            }
        }
        //启动多线程进行下载
        mThreadList = new ArrayList<>();

        for (ThreadInfo info : threadinfolist){
                DownLoadThread thread  = new DownLoadThread(info);
                //thread.start();
                DownLoadTask.sExecutorService.execute(thread);
                //添加线程到集合中
                mThreadList.add(thread);
        }


    }

    /**
     * 判断所有线程都执行完毕
     */
    private   synchronized  void checkAllThreadsFinished(){
        boolean allFinished  = true;
            //遍历线程集合，判断线程是否执行完毕
        for (DownLoadThread thread : mThreadList){
            if (!thread.isFinished){
                allFinished = false;
                break;
            }
        }
        if (allFinished){
            //删除线程信息
            dao.deleteThread(fileInfo.getUrl());
            //发送广播通知UI现在完成
            Intent intent = new Intent(DownLaodService.ACTION_FINISH);
            intent.putExtra("fileInfo",fileInfo);
            context.sendBroadcast(intent);

        }


    }


    /**
     * 下载线程
     */

    class DownLoadThread extends  Thread{
        private ThreadInfo threadInfo = null;
        public boolean isFinished = false; //线程是否执行完毕

        public DownLoadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }


        @Override
        public void run() {

            HttpURLConnection connection = null;
            RandomAccessFile raf = null;
            InputStream input = null;
            try {
                URL url = new URL(threadInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                //设置线程下载位置
                int start  = threadInfo.getStart()+threadInfo.getFinished();
                connection.setRequestProperty("Range","bytes="+start+"-"+threadInfo.getEnd());
                //文件写入位置
                File file = new File(DownLaodService.DOWNLOAD_PATH,fileInfo.getFilename());
                raf = new RandomAccessFile(file,"rwd");
                raf.seek(start);
                Intent intent = new Intent(DownLaodService.ACTION_UPDATE);
                finished += threadInfo.getFinished();
                long time  = System.currentTimeMillis();
                //开始下载
                if (connection.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                    //读取数据
                    input = connection.getInputStream();
                    byte[] buffer = new byte[1024*4];
                    int len = -1;
                    while ((len = input.read(buffer))!= -1){
                        //写入文件
                        raf.write(buffer,0,len);

                        // 把下载进度发送广播给Activity累加整个文件
                        finished += len;
                        //累加每个线程完成的进度
                        threadInfo.setFinished(threadInfo.getFinished()+len);
                        if (System.currentTimeMillis() - time > 1000) {
                            time  = System.currentTimeMillis();
                            intent.putExtra("finished",finished*100/fileInfo.getLength());
                            intent.putExtra("id",fileInfo.getId());
                            context.sendBroadcast(intent);

                        }
                        //暂停时保存下载进度
                        if (isPause){
                            dao.updateThread(fileInfo.getUrl(),fileInfo.getId(),threadInfo.getFinished());
                            Log.i("--zhoujg77", "暂停" );
                            return;
                        }


                    }
                    //标识线程执行完毕
                    isFinished = true;

                    //检查线程是否执行完毕
                    checkAllThreadsFinished();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                connection.disconnect();
                try {
                    input.close();
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

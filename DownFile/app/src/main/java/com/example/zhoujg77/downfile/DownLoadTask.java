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
import java.util.List;
import java.util.RandomAccess;

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
    public DownLoadTask(Context mcontext, FileInfo fileInfo) {
        this.context = mcontext;
        this.fileInfo = fileInfo;
        dao = new ThreadDAOImpl(mcontext);
    }

    public void downlaod(){
        //读取数据库的线程信息
        List<ThreadInfo> threadinfolist = dao.getThreads(fileInfo.getUrl());
        ThreadInfo threadinfo = null; 
        if (threadinfolist.size() == 0) {
            //初始化线程信息对象
            threadinfo = new ThreadInfo(0,fileInfo.getUrl(),0,fileInfo.getLength(),0);
        }else {
            threadinfo = threadinfolist.get(0);
        }
        //创建子线程进行下载
        new DownLoadThread(threadinfo).start();
    }

    /**
     * 下载线程
     */

    class DownLoadThread extends  Thread{
        private ThreadInfo threadInfo = null;

        public DownLoadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }


        @Override
        public void run() {
            //向数据库插入信息
            if (!dao.isExists(threadInfo.getUrl(), threadInfo.getId())) {
                dao.insertThread(threadInfo);
            }
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

                        // 把下载进度发送广播给Activity
                        finished += len;
                        if (System.currentTimeMillis() - time > 500) {
                            time  = System.currentTimeMillis();
                            intent.putExtra("finished",finished*100/fileInfo.getLength());
                            context.sendBroadcast(intent);

                        }
                        //暂停时保存下载进度
                        if (isPause){
                            dao.updateThread(fileInfo.getUrl(),fileInfo.getId(),finished);
                            Log.i("--zhoujg77", "暂停" );
                            return;
                        }


                    }
                    //删除线程信息
                    dao.deleteThread(threadInfo.getUrl(),threadInfo.getId());
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

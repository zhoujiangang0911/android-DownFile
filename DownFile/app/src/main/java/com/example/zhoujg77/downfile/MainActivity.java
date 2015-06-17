package com.example.zhoujg77.downfile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.zhoujg77.downfile.R.id.info;
import static com.example.zhoujg77.downfile.R.id.tv_downfilename;

public class MainActivity extends AppCompatActivity {
    private ListView mListView = null;
    private List<FileInfo> mFileList = null;
    private FileListAdapter mAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.lv_list);

        mFileList = new ArrayList<>();



        //常见文件信息对象
        final FileInfo fileInfo1 = new FileInfo(0,"http://www.imooc.com/mobile/imooc.apk","a.apk",0,0);
        final FileInfo fileInfo2 = new FileInfo(1,"http://v1.mukewang.com/38716012-acb2-44b9-9ccb-fa5675441444/H.mp4","a.mp4",0,0);
        final FileInfo fileInfo3 = new FileInfo(2,"http://img4.duitang.com/uploads/blog/201407/07/20140707132755_tdHCJ.thumb.700_0.jpeg","a.jpeg",0,0);
        final FileInfo fileInfo4 = new FileInfo(3,"http://v1.mukewang.com/2bede6fa-7732-4cae-a002-b269cf352b3b/L.mp4","b.mp4",0,0);
        mFileList.add(fileInfo1);
        mFileList.add(fileInfo2);
        mFileList.add(fileInfo3);
        mFileList.add(fileInfo4);

        mAdapter = new FileListAdapter(this,mFileList);

        mListView.setAdapter(mAdapter);


        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownLaodService.ACTION_UPDATE);
        filter.addAction(DownLaodService.ACTION_FINISH);
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * 跟新UI
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownLaodService.ACTION_UPDATE.equals(intent.getAction())) {
                int finished = intent.getIntExtra("finished",0);
                int id = intent.getIntExtra("id",0);
                mAdapter.updateProgress(id,finished);
            }else if (DownLaodService.ACTION_FINISH.equals(intent.getAction())){
                //更新进度条
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                mAdapter.updateProgress(fileInfo.getId(),0);
                Toast.makeText(MainActivity.this,mFileList.get(fileInfo.getId()).getFilename()+"下载完毕",Toast.LENGTH_SHORT).show();
            }
        }
    };


}

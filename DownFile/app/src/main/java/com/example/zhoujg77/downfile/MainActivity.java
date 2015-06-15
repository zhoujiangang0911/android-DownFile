package com.example.zhoujg77.downfile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.example.zhoujg77.downfile.R.id.tv_downfilename;

public class MainActivity extends AppCompatActivity {
    private Button mStartButton;
    private Button mPauseButton;
    private TextView mFileNameTextView;
    private ProgressBar mDownProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFileNameTextView = (TextView) findViewById(R.id.tv_downfilename);
        mStartButton = (Button) findViewById(R.id.bnt_start);
        mPauseButton = (Button) findViewById(R.id.btn_pause);
        mDownProgressBar = (ProgressBar) findViewById(R.id.myprogressbar);
        //常见文件信息对象
        final FileInfo fileInfo = new FileInfo(0,"http://v1.mukewang.com/22e7a6e5-9542-4fd0-9932-981e280d55d9/H.mp4","穆课网视频",0,0);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DownLaodService.class);
                intent.setAction(DownLaodService.ACTION_START);
                intent.putExtra("fileInfo",fileInfo);
                startService(intent);
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DownLaodService.class);
                intent.setAction(DownLaodService.ACTION_STOP);
                intent.putExtra("fileInfo",fileInfo);
                startService(intent);
            }
        });


    }


}

package com.example.zhoujg77.downfile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhoujg77 on 2015/6/16.
 */
public class FileListAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<FileInfo> fileList = null;


    public FileListAdapter(Context mContext, List<FileInfo> fileList) {
        this.mContext = mContext;
        this.fileList = fileList;
    }

    @Override
    public int getCount() {

        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder holder = null;
        final FileInfo fileInfo = fileList.get(position);
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.tv_downfilename);
            holder.btstart = (Button) convertView.findViewById(R.id.bnt_start);
            holder.btstop = (Button) convertView.findViewById(R.id.btn_pause);
            holder.pbFile = (ProgressBar) convertView.findViewById(R.id.myprogressbar);
            holder.tv.setText(fileInfo.getFilename());
            holder.pbFile.setMax(100);
            holder.btstart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,DownLaodService.class);
                    intent.setAction(DownLaodService.ACTION_START);
                    intent.putExtra("fileInfo",fileInfo);
                    mContext.startService(intent);
                }
            });
            holder.btstop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,DownLaodService.class);
                    intent.setAction(DownLaodService.ACTION_STOP);
                    intent.putExtra("fileInfo",fileInfo);
                    mContext.startService(intent);
                }
            });
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.pbFile.setProgress(fileInfo.getFinished());


        return convertView;
    }

    /**
     * 更新列表中的进度条
     */

    public void updateProgress(int id,int progress){
        FileInfo fileInfo = fileList.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();
    }


    static class ViewHolder {
        TextView tv;
        Button btstart;
        Button btstop;
        ProgressBar pbFile;
    }




}

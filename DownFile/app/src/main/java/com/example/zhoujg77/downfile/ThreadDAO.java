package com.example.zhoujg77.downfile;

import java.util.List;

/**
 * 数据访问接口
 * Created by zhoujg77 on 2015/6/13.
 */
public interface ThreadDAO {

    /**
     *
     *插入线程
     * @param threadInfo
     */
    public void insertThread(ThreadInfo threadInfo);

    /**
     * 删除线程
     * @param url
     *
     */

    public void deleteThread(String url);

    /**
     * 更新线程下载进度
     * @param url
     * @param thread_id
     * @param finished
     */
    public void updateThread(String url,int thread_id,int finished);


    /**
     * 查询文件的线程信息
     * @param url
     * @return
     */
    public List<ThreadInfo> getThreads(String url);

    /**
     * 查询线程信息是否存在
     * @param url
     *
     * @return
     */
    public boolean isExists(String url,int thread_id);









}

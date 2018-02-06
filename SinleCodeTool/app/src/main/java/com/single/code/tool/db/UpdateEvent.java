package com.single.code.tool.db;

/**
 * Created by Administrator on 2017/12/5.
 */
public interface UpdateEvent extends DBEvent {
    void onUpdateComplete(long result);
}
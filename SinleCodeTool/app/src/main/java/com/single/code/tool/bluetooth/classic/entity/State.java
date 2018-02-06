package com.single.code.tool.bluetooth.classic.entity;

/**
 * @Description: 状态
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/18 20:37.
 */
public enum State {

    STATE_NONE(0),//断开连接
    STATE_LISTEN(1),//未连接
    STATE_CONNECTING(2),//连接中
    STATE_CONNECTED(3);//连接成功

    private int code;

    State(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

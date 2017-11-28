package com.example.designmode.BridgeMode;

/**
 * Created by LiuLei on 2017/11/28.
 */

public interface MessageImplementor {
    /**
     * 发送消息
     *
     * @param message
     *            要发送消息的内容
     * @param toUser
     *            消息的接受者
     */
    public void send(String message, String toUser);
}

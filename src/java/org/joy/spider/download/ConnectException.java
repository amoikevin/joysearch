package org.joy.spider.download;
/**
 * @author LamFeeling
 * 连接失败触发的异常
 */
public class ConnectException extends Exception {
    public ConnectException(){}
    public ConnectException(String msg) {
        super(msg);
    }
}

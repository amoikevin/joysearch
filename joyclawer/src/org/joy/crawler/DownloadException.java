package org.joy.crawler;
/**
 * @author LamFeeling 
 * 下载失败触发的异常
 */
public class DownloadException extends Exception {

    public DownloadException(){}
    public DownloadException(String msg)
    {
        super(msg);
    }

}

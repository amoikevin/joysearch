/*
 * WalkerArgs.java
 *
 * Created on 2007年5月7日, 上午11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.spider.download;

import org.joy.spider.service.DownloadJob;

public class DownloadCompletedArgs {

    private String text;
    private DownloadJob job;
    private DownloadException error;

    public DownloadJob getJob() {
        return job;
    }

    public String getText() {
        return text;
    }

    public DownloadCompletedArgs(DownloadJob job, String text, DownloadException error) {
        this.text = text;
        this.job = job;
        this.error = error;
    }

    public DownloadException getError() {
        return error;
    }
}

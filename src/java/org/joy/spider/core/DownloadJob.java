/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.spider.core;

import org.joy.group.core.Job;

/**
 *
 * @author Lamfeeling
 */
public class DownloadJob extends Job{

    private String URL;
    private int level;
    public DownloadJob(String URL,int level) {
        this.URL = URL;
        this.level = level;
    }

    public String getURL() {
        return URL;
    }

    public int getLevel() {
        return level;
    }
}

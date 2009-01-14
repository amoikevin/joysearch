/*
 * WalkerListener.java
 *
 * Created on 2007年5月6日, 上午10:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.spider.download;

import org.joy.spider.download.DownloadCompletedArgs;
import java.util.EventListener;

/**
 *
 * @author AC
 */

public interface DownloadCompletedListener  {
    public void onTaskCompleted(Object sender,DownloadCompletedArgs args);
}
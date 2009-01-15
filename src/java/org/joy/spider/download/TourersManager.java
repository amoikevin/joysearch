/*
 * WebWalker.java
 *
 * Created on 2007年5月6日, 上午10:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.spider.download;

import java.util.Vector;
import org.joy.spider.core.DownloadJob;

/**
 *
 * @author AC
 */
public class TourersManager {

    private Vector<Tourer> taskList = new Vector<Tourer>();
    private Object startWaiter = new Object();
    private int maxTaskNum;
    private boolean cancelled;

    public TourersManager(int maxTaskNum) {
        this.maxTaskNum = maxTaskNum;
    }

    public boolean start(DownloadJob job, DownloadCompletedListener dcl) {
        Tourer tourer;
        //如果线程数超过了允许的最大值，那么就等现有线程结束。
        if (taskList.size() == maxTaskNum) {
            try {
                synchronized (startWaiter) {
                    startWaiter.wait();
                    if (cancelled) {
                        return false;
                    }
                    tourer = new Tourer();
                    taskList.add(tourer);
                }
            } catch (InterruptedException ex) {
                return false;
            }
        } else {
            tourer = new Tourer();
            taskList.add(tourer);
        }
        //开始一个新的线程，去下载。
        Thread t = new Thread(new DownloadThread(job, tourer, dcl));
        t.setDaemon(true);
        t.start();
        return true;
    }

    public void stop() {
        cancelled = true;
        synchronized (startWaiter) {
            startWaiter.notify();
        }
        synchronized (taskList) {
            for (Tourer t : taskList) {
                t.close();
            }
        }
        cancelled = false;
    }

    private class DownloadThread implements Runnable {

        private Tourer tourer;
        private DownloadJob job;
        private DownloadCompletedListener taskListener;

        public DownloadThread(DownloadJob job,
                Tourer tourer,
                DownloadCompletedListener taskListener) {
            super();
            this.tourer = tourer;
            this.job = job;
            this.taskListener = taskListener;
        }

        public void run() {
            DownloadCompletedArgs args = null;
            try {
                String text = tourer.download(job.getURL());
                args = new DownloadCompletedArgs(job, text, null);
            } catch (DownloadException ex) {
                args = new DownloadCompletedArgs(job, null, ex);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                taskListener.onTaskCompleted(this, args);
                synchronized (startWaiter) {
                    taskList.remove(tourer);
                    startWaiter.notify();
                }
            }
        }
    }
}



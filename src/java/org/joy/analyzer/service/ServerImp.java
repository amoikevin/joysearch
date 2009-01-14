/*
 * Main.java
 *
 * Created on 2007年10月19日, 下午7:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.analyzer.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import org.joy.db.DBException;
import org.joy.dblookup.service.DBGroup;
import org.joy.lookup.service.Job;
import org.joy.rank.service.LinkSystemAnalyzer;
import org.joy.scan.ParseException;
import org.joy.scan.HTMLParser;
import org.joy.spider.service.SpiderGroup;

/**
 *
 * @author suda1
 */
public class ServerImp extends UnicastRemoteObject implements Analyzer {

    private JobSet jobSet;
    private Thread taskThread;
    private Deliverer deliver;
    private Object waiter = new Object();
    private boolean cancelled;

    public ServerImp(DBGroup dbG,
            LinkSystemAnalyzer linksAnalyzer, SpiderGroup spiderGroup)
            throws RemoteException, DBException {
        super();
        jobSet = new JobSet();
        deliver = new Deliverer(dbG, linksAnalyzer, spiderGroup);
    }

    private boolean waitJob() {
        synchronized (waiter) {
            try {
                waiter.wait();
                return !cancelled;
            } catch (InterruptedException ex) {
                return false;
            }
        }
    }

    void jobNotify() {
        synchronized (waiter) {
            waiter.notify();
        }
    }

    public void putJob(Job job) throws RemoteException {
        try {
            DocumentJob j = (DocumentJob) job;
            jobSet.put(j.getLevel(), j.toBytes());
            //只有等到Buffer，满的时候才通知分析

            jobNotify();
        } catch (DBException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start() throws RemoteException {

        taskThread = new Thread(new Runnable() {

            public void run() {
                try {
                    while (!cancelled) {
                        DocumentJob res = jobSet.pop();
                        if (res == null) {
                            System.out.println("开始等待");
                            waitJob();
                            continue;
                        }
                        //分词，加分析
                        analyze(res);
                    }
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(ServerImp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }

            private void analyze(DocumentJob job) throws ParseException, MalformedURLException {
                System.out.println("正在扫描" + job.getURL());
                HTMLParser parser = new HTMLParser();
                String text = job.getText().replaceAll("[\n|\r]", "");
                parser.parse(text, job.getURL());
                System.out.println("扫描结束");
                deliver.deliver(job, parser);
                System.out.println("分发结束");
            }
        });
        taskThread.start();
    }

    public void stop() throws RemoteException {
        try {
            cancelled = true;
            jobNotify();
            taskThread.join();
          //  jobSet.clear();
            cancelled = false;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ServerImp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public void close() throws DBException {
        try {
            stop();
            deliver.shutdown();
            jobSet.close();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public HashSet<String> getAllowed() {
        return deliver.getAllowed();
    }

    public HashSet<String> getDenied() {
        return deliver.getDenied();
    }
}

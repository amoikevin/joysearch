package org.joy.spider.service;

import org.joy.spider.service.JobSet;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.analyzer.service.AnalyzerGroupProxy;
import org.joy.analyzer.service.DocumentJob;
import org.joy.db.DBException;
import org.joy.deployer.Deployer;
import org.joy.lookup.service.Group;
import org.joy.lookup.service.Job;
import org.joy.spider.download.DownloadCompletedArgs;
import org.joy.spider.download.DownloadCompletedListener;
import org.joy.spider.download.TourersManager;

/**
 *
 * @author Mirabel
 */
public class ServerImp extends UnicastRemoteObject
        implements Spider, DownloadCompletedListener, Runnable {

    private String hostName;
    private Object waiter = new Object();
    private TourersManager manager = new TourersManager(5);
    private boolean cancelled;
    private Thread t;
    private JobSet jobSet;
    private AnalyzerGroupProxy anaG;
    private SpiderGroup spiderG;

    /**
     * 初始化蜘蛛服务器
     * @param anaG 分析服务器集群的组服务器
     * @param spiderG 蜘蛛服务器集群的组服务器
     * @throws java.rmi.RemoteException
     * @throws org.joy.db.DBException
     */
    public ServerImp(Group anaG, SpiderGroup spiderG) throws RemoteException, DBException {
        super();
        this.jobSet = new JobSet();
        this.anaG = new AnalyzerGroupProxy(anaG);
        this.spiderG = spiderG;
    }

    /**
     * 讓蜘蛛服務器進入等待
     * @return 是否被用戶所取消，false表示被用戶阻斷
     */
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
            if (t.getState() == Thread.State.WAITING) {
                waiter.notify();
            }
        }
    }

    /**
     * 推入作业
     * @param job
     * @throws java.rmi.RemoteException
     */
    public void putJob(Job job) throws RemoteException {
        try {
            DownloadJob URL = (DownloadJob) job;
            jobSet.put(URL.getLevel(), URL.getURL());
            jobNotify();
        } catch (DBException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
            throw new RemoteException();
        }
    }

    /**
     * 当任务完成时自动调用
     * @param sender
     * @param args
     */
    public void onTaskCompleted(Object sender, DownloadCompletedArgs args) {
        if (args != null && args.getError() == null && !cancelled) {
            String text = args.getText();
            DownloadJob job = args.getJob();
            System.out.println("成功下了 " + job.getURL());
            try {
                // 添加URL,text
                anaG.putJob(new DocumentJob(job, text, "HTML"));
                //通知集群组服务器，已经完成任务
                spiderG.finish(job.getURL());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    /**
     * 负责下载的函数
     */
    public void run() {
        try {
            while (!cancelled) {
                DownloadJob job = null;
                job = jobSet.pop();
                // 没有了等SpiderPool
                if (job == null) {
                    System.out.println("开始等待");
                    waitJob();
                    continue;
                }
                Deployer.logger.info("正在下载" + job.getURL());
                // 下载之
                manager.start(job, this);

            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }

    public void start() {
        t = new Thread(this);
        t.start();
    }

    public void stop() throws RemoteException {
        try {
            cancelled = true;
            jobNotify();
            try {
                t.join();
            } catch (InterruptedException ex) {

            }
            System.out.println("下载线程关闭结束");
            synchronized (this) {
                System.out.println("同步开始");
                //platform.syncAndClose();
                jobSet.close();
            }
        } catch (DBException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
            throw new RemoteException();
        }
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
//~ Formatted by Jindent --- http://www.jindent.com


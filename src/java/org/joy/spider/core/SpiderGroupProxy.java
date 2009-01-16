/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.spider.core;

import java.rmi.RemoteException;
import java.util.Random;
import org.joy.deployer.Deployer;
import org.joy.group.core.Job;
import org.joy.group.core.Locator;

/**
 *
 * @author Lamfeeling
 */
public class SpiderGroupProxy implements Spider {

    private SpiderGroup group;

    public SpiderGroupProxy(SpiderGroup group) {
        this.group = group;
    }

    private Spider getSpider() throws RemoteException {
        Locator[] rs = group.lookup(Spider.class);
        return (Spider) rs[new Random().nextInt(rs.length)].getRemoteInterface();
    }

    public void putJob(Job job) throws RemoteException {
        //验证URL是否可下载
        if (group.request(((DownloadJob) job).getURL())) {
            getSpider().putJob(job);
        } else;
    //System.out.println("不可下载的链接"+((DownloadJob)job).getURL());
    }

    public void reset() throws RemoteException {
        group.reset();
        System.out.println("网页链接库清空完成");
    }
}

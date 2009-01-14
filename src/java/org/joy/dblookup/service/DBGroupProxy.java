/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.dblookup.service;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.db.DBException;
import org.joy.lookup.service.Job;
import org.joy.lookup.service.Locator;

/**
 *
 * @author Lamfeeling
 */
public class DBGroupProxy implements Database {

    private DBGroup group;

    public DBGroupProxy(DBGroup group) {
        this.group = group;
    }

    private Database getDatabase(String URL) throws RemoteException {
        return (Database) group.lookUp(URL).getRemoteInterface();
    }

    public void putJob(Job job) throws RemoteException {
        if (job instanceof IndexJob) {
            //index job
            IndexJob j = (IndexJob) job;
            getDatabase(j.getURL()).putJob(job);
        } else if (job instanceof RankUpdateJob) {
            //rank update job
            RankUpdateJob j = (RankUpdateJob) job;
            getDatabase(j.getURL()).putJob(job);
        }
    }

    public String Query(String URL) throws RemoteException {
        return group.lookUp(URL).getHandle();
    }

    public void index() throws DBException, RemoteException {
        group.clear();
        //同步信号量
        final Semaphore s = new Semaphore(-group.getLocators().size()+1);
        for (final Locator l : group.getLocators()) {
            //同时开启n个线程简历倒排索引
            new Thread(new Runnable() {

                public void run() {
                    try {
                        Database db = (Database) l.getRemoteInterface();
                        db.index();
                        s.release();
                        System.out.println(l.getShortName() + "索引建立成功!");
                    } catch (Exception ex) {
                        System.out.println(l.getShortName() + "索引建立失败，检查索引計算器");
                    }
                }
            }).start();
        }
        try {
            s.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBGroupProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[] getDBHandles() throws RemoteException {
        Set<Locator> locators = group.getLocators();
        ArrayList<String> handles = new ArrayList();
        for (Locator l : locators) {
            handles.add(l.getHandle());
        }
        return handles.toArray(new String[0]);
    }

    public void clear() throws DBException, RemoteException {
        
        //同步信号量
        final Semaphore s = new Semaphore(-group.getLocators().size()+1);
        for (final Locator l : group.getLocators()) {
            //同时开启n个线程简历倒排索引
            new Thread(new Runnable() {

                public void run() {
                    try {
                        Database db = (Database) l.getRemoteInterface();
                        db.clear();
                        s.release();
                        System.out.println(l.getShortName() + "数据库清空成功!");
                    } catch (Exception ex) {
                        System.out.println(l.getShortName() + "数据库清空失败");
                    }
                }
            }).start();
        }
        try {
            s.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBGroupProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

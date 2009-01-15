/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.group.service;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.deployer.Deployer;

/**
 *
 * @author Lamfeeling
 */
public abstract class GroupImp extends UnicastRemoteObject implements Group {

    protected Hashtable<Locator, WorkerState> workers = new Hashtable<Locator, WorkerState>();
    private final Object waiter = new Object();

    public GroupImp() throws RemoteException {
        //每1分钟验证一下所有的Locator是否有效
        new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                verify();
            }
        }, 1000 * 60, 1000 * 60);
    }

    public void register(Locator locator) {
        workers.put(locator, WorkerState.READY);
        synchronized (waiter) {
            waiter.notify();
        }
        Deployer.logger.info("注册一个" + locator);
    }

    public void unregister(Locator locator) {
        workers.remove(locator);
        Deployer.logger.info("解除一个" + locator);
    }

    public Locator[] lookup(Class serviceClass) {
        return lookup(serviceClass, Long.MAX_VALUE);
    }

    public void setBusy(Locator l) {
        if (workers.get(l) != null) {
            workers.put(l, WorkerState.BUSY);
        }
    }

    public void setReady(Locator l) {
        if (workers.get(l) != null) {
            workers.put(l, WorkerState.READY);
            synchronized (waiter) {
                waiter.notify();
            }
        }
    }

    /**
     * 验证本Group当中的每一个Loator是否还是有效
     */
    private void verify() {
        for (Locator l : workers.keySet()) {
            try {
                Naming.lookup(l.getHandle());
            } catch (Exception ex) {
                ex.printStackTrace();
                workers.remove(l);
            }
        }
    }

    public Locator[] lookup(Class serviceClass, long timeout) {
        if (workers.size() == 0) {
            synchronized (waiter) {
                try {
                    //如果暂时没有服务，等待一段时间
                    waiter.wait(timeout);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GroupImp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        Vector<Locator> ls = new Vector<Locator>();
        //找一个在线的合适的服务
        for (Locator locator : workers.keySet()) {
            if (workers.get(locator) == WorkerState.READY &&
                    locator.getServiceClass().toString().equals(serviceClass.toString())) {
                ls.add(locator);
            }
        }
        return ls.toArray(new Locator[0]);
    }

    public HashSet<Locator> getLocators() throws RemoteException {
        return new HashSet<Locator>(workers.keySet());
    }
}

/*
 * Watcher.java
 *
 * Created on 2007年10月3日, 下午8:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.db;

/**
 *
 * @author 海
 */
public abstract class Deamon{
    private static final int HIGH_INTERVAL = 10000;
    private static final int MEDIUM_INTERVAL = 1000;
    private static final int LOW_INTERVAL = 500;
    
    private Thread deamonThread;
    private int interval=HIGH_INTERVAL;
    private boolean cancelled;
    
    private final Object waiter = new Object();
    
    public void Notify(){
        synchronized (waiter){
            waiter.notify();
        }
    }
    protected void Wait(){
        synchronized (waiter){
            try {
                if(!cancelled)
                    waiter.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    protected void Wait(long time){
        synchronized (waiter){
            try {
                if(!cancelled)
                    waiter.wait(time);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    protected abstract void doOperation();
    protected abstract void doCleanUp();
    public abstract String getName();
    public void setPriority(int level){
        switch (level){
            case 0:
                interval = LOW_INTERVAL;
                break;
            case 1:
                interval = MEDIUM_INTERVAL;
                break;
            case 2:
                interval = HIGH_INTERVAL;
                break;
        }
    }
    public void start(){
        cancelled=false;
        deamonThread= new Thread(new Runnable() {
            public void run() {
                while(!cancelled) {
                    doOperation();
                    Wait(interval);
                }
            }
        });
        deamonThread.setDaemon(true);
        deamonThread.start();
    }
    
    public void cancel(){
        cancelled=true;
        try {
            Notify();
            deamonThread.join();
            System.out.println(getName()+"终止");
            doCleanUp();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
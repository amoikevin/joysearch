/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.joy.spider.core;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.db.DBException;
import org.joy.group.core.GroupImp;

/**
 *
 * @author Lamfeeling
 */
public class SpiderGroupImp extends GroupImp implements SpiderGroup{
    private URLTable urlTable;
    
    private SpiderGroupImp outter = this;
    public SpiderGroupImp() throws RemoteException, DBException {
        super();
        urlTable = new URLTable();
        
        //每隔5分钟重新把DOWNLOADING的URL发送给Spider一次
        new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    String[] URLs = urlTable.export();
                    for (String URL : URLs) {
                        SpiderGroupProxy p = new SpiderGroupProxy(outter);
                        //把level设定为1，以便尽快下载
                        p.putJob(new DownloadJob(URL, 1));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpiderGroupImp.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }, 1000*60*5, 1000*60*5);
    }

    public void finish(String URL) throws RemoteException {
        try {
            urlTable.finish(URL);
        } catch (DBException ex) {
            Logger.getLogger(SpiderGroupImp.class.getName()).log(Level.SEVERE, null, ex);
            throw new RemoteException();
        }
    }

    public boolean request(String URL) throws RemoteException{
        try {
            return urlTable.request(URL);
        } catch (DBException ex) {
            Logger.getLogger(SpiderGroupImp.class.getName()).log(Level.SEVERE, null, ex);
            throw new RemoteException();
        }
    }
    
    public void shutdown(){
        try {
            urlTable.close();
        } catch (DBException ex) {
            Logger.getLogger(SpiderGroupImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reset() throws RemoteException {
        try {
            urlTable.reset();
        } catch (DBException ex) {
            Logger.getLogger(SpiderGroupImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

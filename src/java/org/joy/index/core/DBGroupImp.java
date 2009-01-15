/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.core;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.db.Command;
import org.joy.db.DBException;
import org.joy.index.db.DBSelectionPlatform;
import org.joy.group.core.GroupImp;
import org.joy.group.core.Locator;

/**
 *
 * @author Lamfeeling
 */
public class DBGroupImp extends GroupImp implements DBGroup {

    private DBSelectionPlatform platform;
    private Command cmd;

    private Hashtable<String, Locator> nameTable = new Hashtable<String, Locator>();

    public DBGroupImp() throws RemoteException, DBException {
        platform = new DBSelectionPlatform();
        cmd = new Command(platform.open("DBSelection"));
    }

    @Override
    public void register(Locator locator) {
        super.register(locator);
        nameTable.put(locator.getShortName(), locator);
    }

    @Override
    public void unregister(Locator locator) {
        super.unregister(locator);
        nameTable.remove(locator.getShortName());
    }


    /**
     * 按URL查找存储计算机
     * @param URL 需要查询的计算机
     * @return 返回查询返回的存储计算机
     * @throws java.rmi.RemoteException
     */
    public Locator lookUp(String URL) throws RemoteException {
        try {
            //首先查找以前有没有呢？
            String shortName = (String) cmd.getEntry( UID.from(URL) );
            if (shortName == null) {
                //采取哈希轮转法
                Locator[] rs = lookup(Database.class);
                int i = Math.abs(URL.hashCode() % rs.length);
                //写入Selection
                cmd.setEntry( UID.from(URL),
                        rs[i].getShortName());
                return rs[i];
            } else {
                //找到了，直接返回
                return nameTable.get(shortName);
            }
        } catch (Exception ex) {
            Logger.getLogger(DBGroupImp.class.getName()).log(Level.SEVERE, null, ex);
            throw new RemoteException(ex.toString());
        }
    }

    public void clear() throws RemoteException {
        try {
            cmd.clear();
        } catch (DBException ex) {
            Logger.getLogger(DBGroupImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 关闭DBGroup。
     * @throws org.joy.db.DBException
     */
    public void shutdown() throws DBException {
        platform.syncAndClose();
    }
}

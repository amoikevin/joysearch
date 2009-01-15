/*
 * SearchServer.java
 *
 * Created on 2007骞?0鏈?9鏃? 涓嬪崍6:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.query.core;

import org.joy.query.core.Cursor;
import org.joy.db.DBException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.joy.query.core.SnipperEntry;


/**
 *
 * @author suda1
 */
public interface SearchServer extends Remote {
   
    Cursor search(String[] keywords) throws DBException, RemoteException;

    SnipperEntry getSnipper(String[] keywords, String URL) throws DBException, RemoteException;

    String getName() throws RemoteException;
}

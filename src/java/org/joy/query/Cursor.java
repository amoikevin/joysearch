/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.query;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.joy.db.DBException;

/**
 *
 * @author Lamfeeling
 */
public interface  Cursor extends Remote{

    void close() throws RemoteException;

    ResultEntry[] getNext(int count) throws DBException, RemoteException;

    ResultEntry[] getNext(int count, Filter filter) throws DBException, RemoteException;
}

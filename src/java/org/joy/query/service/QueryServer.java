/*
 * QureyServer.java
 *
 * Created on 2007年6月12日, 下午6:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.query.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.joy.query.Result;

/**
 *
 * @author AC
 */
public interface QueryServer extends Remote {
    
    Result[] getPage(String searchString, int pageNo) throws RemoteException;
    void clear() throws RemoteException;
}

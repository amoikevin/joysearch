/*
 * PageRank.java
 *
 * Created on 2007年11月26日, 上午11:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.rank.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.joy.db.DBException;

/**
 *
 * @author 海
 */
public interface LinkSystemAnalyzer extends Remote {
    
    void addLink(String from, String to) throws DBException, RemoteException;
    
    void calPageRank() throws DBException, RemoteException;
    
    float getRank(String URL) throws RemoteException;
    
    void removeLink(String URL) throws DBException, RemoteException;
    
    void clear() throws DBException, RemoteException;
    
    String[] getRefs(String URL) throws DBException, RemoteException;
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.joy.lookup.service.Worker;

/**
 *
 * @author Lamfeeling
 */
public interface Analyzer extends Remote, Worker {
    public void start() throws RemoteException;
    public void stop() throws RemoteException;
}

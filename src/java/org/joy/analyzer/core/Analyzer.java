/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.joy.group.core.Worker;

/**
 *
 * @author Lamfeeling
 */
public interface Analyzer extends Remote, Worker {
    public void start() throws RemoteException;
    public void stop() throws RemoteException;
}

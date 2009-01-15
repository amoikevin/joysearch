/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.group.core;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Lamfeeling
 */
public interface Worker extends Remote {
    void putJob(Job job) throws RemoteException;
}

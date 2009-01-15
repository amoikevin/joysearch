/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.joy.db.DBException;
import org.joy.group.service.Worker;

/**
 *
 * @author Lamfeeling
 */
public interface Database extends Remote, Worker {

    void clear() throws DBException, RemoteException;

    void index() throws DBException, RemoteException;
}

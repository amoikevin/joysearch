/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.service;

import java.rmi.RemoteException;
import org.joy.group.service.Group;
import org.joy.group.service.Locator;

/**
 *
 * @author Lamfeeling
 */
public interface DBGroup extends Group {
    public void clear() throws RemoteException;
    public Locator lookUp(String URL) throws RemoteException;
}   

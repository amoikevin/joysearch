/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.dblookup.service;

import java.rmi.RemoteException;
import org.joy.lookup.service.Group;
import org.joy.lookup.service.Locator;

/**
 *
 * @author Lamfeeling
 */
public interface DBGroup extends Group {
    public void clear() throws RemoteException;
    public Locator lookUp(String URL) throws RemoteException;
}   

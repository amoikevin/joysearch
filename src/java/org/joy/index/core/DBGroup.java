/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.core;

import java.rmi.RemoteException;
import org.joy.group.core.Group;
import org.joy.group.core.Locator;

/**
 *
 * @author Lamfeeling
 */
public interface DBGroup extends Group {
    public void clear() throws RemoteException;
    public Locator lookUp(String URL) throws RemoteException;
}   

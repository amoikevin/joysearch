/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.group.service;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lamfeeling
 */
public class Locator implements Serializable {

    private String handle;
    private Class typeOfService;

    @Override
    public int hashCode() {
        return handle.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            return ((Locator) obj).getHandle().equals(this.getHandle());
        } else {
            return false;
        }
    }

    public Class getServiceClass() {
        return typeOfService;
    }

    public Locator(Class typeOfService, String handle) {
        this.typeOfService = typeOfService;
        this.handle = handle;
    }

    public Remote getRemoteInterface() throws RemoteException {
        try {
            return Naming.lookup(handle);
        } catch (Exception ex) {
            Logger.getLogger(Locator.class.getName()).log(Level.SEVERE, null, ex);
            throw new RemoteException(ex.toString());
        }
    }

    public String getHandle() {
        return handle;
    }

    public String getShortName() {
        int index = getHandle().lastIndexOf("/")+1;
        return getHandle().substring(index);
    }

    @Override
    public String toString() {
        return typeOfService.toString() + "@" + handle;
    }
}   

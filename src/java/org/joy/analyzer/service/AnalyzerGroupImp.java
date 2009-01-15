/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.service;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import org.joy.lookup.service.GroupImp;

/**
 *
 * @author Lamfeeling
 */
public class AnalyzerGroupImp extends GroupImp implements AnalyzerGroup {

    private HashSet<String> hostsAllowed;
    private HashSet<String> hostsDenied;

    public AnalyzerGroupImp(HashSet<String> hostsAllowed,
            HashSet<String> hostsDenied) throws RemoteException {
        this.hostsAllowed = hostsAllowed;
        this.hostsDenied = hostsDenied;
    }

    public AnalyzerGroupImp(Properties p) throws RemoteException {
        String[] allowed = p.getProperty("host.allowed").split(";");
        String[] denied = p.getProperty("host.denied").split(";");
        hostsAllowed = new HashSet<String>(Arrays.asList(allowed));
        hostsDenied = new HashSet<String>(Arrays.asList(denied));
    }

    public HashSet<String> getHostsAllowed() throws RemoteException {
        return hostsAllowed;
    }

    public HashSet<String> getHostsDenied() throws RemoteException {
        return hostsDenied;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.service;

import java.rmi.RemoteException;
import java.util.HashSet;
import org.joy.group.service.Group;

/**
 *
 * @author Lamfeeling
 */
public interface AnalyzerGroup extends Group {

    HashSet<String> getHostsAllowed() throws RemoteException;

    HashSet<String> getHostsDenied() throws RemoteException;
}

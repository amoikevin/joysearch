/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.core;

import java.rmi.RemoteException;
import java.util.Random;
import org.joy.group.core.Group;
import org.joy.group.core.Job;
import org.joy.group.core.Locator;

/**
 *
 * @author Lamfeeling
 */
public class AnalyzerGroupProxy implements Analyzer {

    private Group group;

    public AnalyzerGroupProxy(Group group) {
        this.group = group;
    }

    private Analyzer getAnalyzer() throws RemoteException {
        Locator[] rs = group.lookup(Analyzer.class);
        return (Analyzer) rs[new Random().nextInt(rs.length)].getRemoteInterface();
    }

    public void putJob(Job job) throws RemoteException {
        getAnalyzer().putJob(job);
    }

    public void stop() throws RemoteException {
        for (Locator l : group.getLocators()) {
            Analyzer a = (Analyzer) l.getRemoteInterface();
            a.stop();
        }
    }

    public void start() throws RemoteException {
        for (Locator l : group.getLocators()) {
            Analyzer a = (Analyzer) l.getRemoteInterface();
            a.start();
        }
    }
}

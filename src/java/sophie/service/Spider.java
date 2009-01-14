package sophie.service;
/*
 * SpiderPoolListener.java
 *
 * Created on 2007年10月21日, 上午12:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Mirabel
 */
public interface Spider extends Remote{
    public void push(String[] URLs) throws RemoteException;
}

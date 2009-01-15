/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.group.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;

/**
 *
 * @author Lamfeeling
 */
public interface Group extends Remote {

    /**
     * 查找相应的服务
     * @param serviceClass 要查找的服务的类型名称
     * @return 返回服务的定位符
     * @throws java.rmi.RemoteException
     */
    Locator[] lookup(Class serviceClass) throws RemoteException;

    /**
     * 查找相应的服务
     * @param serviceClass serviceClass 要查找的服务的类型名称
     * @param timeout 超時時間
     * @return 返回服务的定位符
     * @throws java.rmi.RemoteException
     */
    Locator[] lookup(Class serviceClass, long timeout) throws RemoteException;

    /**
     * 注冊一個服務
     * @param locator 這個服務的資源定位符
     * @throws java.rmi.RemoteException
     */
    void register(Locator locator) throws RemoteException;

    /**
     * 设置这个服务的状态为忙
     * @param l 要设置的服务定位符
     * @throws java.rmi.RemoteException
     */
    void setBusy(Locator l) throws RemoteException;

    /**
     * 设置这个服务的状态为就绪
     * @param l 要设置的服务定位符
     * @throws java.rmi.RemoteException
     */
    void setReady(Locator l) throws RemoteException;

    /**
     * 解除对服务的注册
     * @param l 要解除注册的资源定位符
     * @throws java.rmi.RemoteException
     */
    void unregister(Locator l) throws RemoteException;

    HashSet<Locator> getLocators() throws RemoteException;
}

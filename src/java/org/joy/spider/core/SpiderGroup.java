/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.spider.core;

import java.rmi.RemoteException;
import org.joy.group.core.Group;

/**
 *
 * @author Lamfeeling
 */
public interface SpiderGroup extends Group {

    /**
     * Spider向Group服务器表明自己已经成功结束了对网页的下载时调用
     * @param URL
     * @throws java.rmi.RemoteException
     */
    public void finish(String URL) throws RemoteException;

    /**
     * 向Group申请要下载的URL,
     * @param URL
     * @return 返回true表示批准，否则返回false
     * @throws java.rmi.RemoteException
     */
    boolean request(String URL) throws RemoteException;

    /**
     * 重置URL连接表
     * @throws java.rmi.RemoteException
     */
    public void reset() throws RemoteException;
}

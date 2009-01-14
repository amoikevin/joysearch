/*
 * Exception.java
 *
 * Created on 2007年5月6日, 下午7:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.db;

/**
 * 所有在数据库操作当中出现的错误都有这个类来表达
 * @author AC
 */
public class DBException extends Exception {
    
    /**
     * Creates a new instance of DBException
     */
    public DBException() {
    }
    /**
     * 创建一个数据库异常类
     * @param msg 描述该异常的字符串
     */
    public DBException(String msg){
        super(msg);
    }
}

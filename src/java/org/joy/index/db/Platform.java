/*
 * Platform.java
 *
 * Created on 2007年12月11日, 上午11:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.index.db;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseException;
import org.joy.db.AbstractPlatform;
import org.joy.db.Connection;
import org.joy.db.DBException;
import org.joy.db.DefaultDBConfig;

/**
 *
 * @author 海
 */
public class Platform extends AbstractPlatform {

    private DocumentConnection docConn;
    private RIndexConnection rIndexConn;
    private StoredClassCatalog classCatalog;
    private Database classDb;

    public Platform() throws DBException {
        super("DB2",512*1024*1024);
        try {
            //读取类信息
            classDb = env.openDatabase(null, "classDb", "classDb", new DefaultDBConfig());
            classCatalog = new StoredClassCatalog(classDb);

            //打开各个数据库。
            docConn = new DocumentConnection(env);
            rIndexConn = new RIndexConnection(env);
        //全部预读取
//            webConn.preload();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException("打开数据库错误！");
        }
    }

    protected Connection connectionFactory(String DBName) throws DBException {
        if (DBName.equals("RIndex")) {
            return new RIndexConnection(env);
        }
        if (DBName.equals("Doc")) {
            return new DocumentConnection(env);
        }
        return null;
    }

    protected void closeUserDB() throws DBException {
        docConn.close();
        rIndexConn.close();
        try {
            classDb.close();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }

    protected void syncUserDB() throws DBException {
        docConn.sync();
        rIndexConn.sync();
        try {
            classDb.sync();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
}

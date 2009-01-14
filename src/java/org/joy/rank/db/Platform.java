/*
 * Platform.java
 *
 * Created on 2007年10月19日, 下午8:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.rank.db;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseException;
import org.joy.db.DBException;
import org.joy.db.Connection;
import org.joy.db.AbstractPlatform;
import org.joy.db.DefaultDBConfig;

/**
 *
 * @author suda1
 */
public class Platform extends AbstractPlatform {

    private StoredClassCatalog classCatalog;
    private Database classDb;
    private LinkConnection linkConn;
    private RefTableConnection refConn;

    /** Creates a new instance of Platform */
    public Platform(long cacheSize) throws DBException {
        super("links", cacheSize);
        try {
            //读取类信息
            classDb = env.openDatabase(null, "classDb", "classDb", new DefaultDBConfig());
            classCatalog = new StoredClassCatalog(classDb);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        //打开数据库
        linkConn = new LinkConnection(env, classCatalog);
        refConn = new RefTableConnection(env, classCatalog);
    }

    protected Connection connectionFactory(String DBName) throws DBException {
        if (DBName.equals("link")) {
            Connection conn = new LinkConnection(env, classCatalog);
            return conn;
        }
        if (DBName.equals("Ref")) {
            Connection conn = new RefTableConnection(env, classCatalog);
            return conn;
        }
        return null;
    }

    protected void closeUserDB() throws DBException {
        linkConn.close();
        refConn.close();
        try {
            classDb.close();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }

    protected void syncUserDB() throws DBException {
        linkConn.sync();
        refConn.sync();
        try {
            classDb.sync();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
}

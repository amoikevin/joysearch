/*
 * Platform.java
 *
 * Created on 2007��10��19��, ����8:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.index.db;

import org.joy.db.Connection;
import org.joy.db.DBException;

/**
 *
 * @author suda1
 */
public class DBSelectionPlatform extends org.joy.db.AbstractPlatform{
    private DBSelectionConnection indexConn;
    /** Creates a new instance of Platform */
    public DBSelectionPlatform() throws DBException {
        super("DBSelection");
        //����ݿ�
        indexConn = new DBSelectionConnection(env);
    }
    
    protected Connection connectionFactory(String DBName) throws DBException {
        if (DBName.equals("DBSelection")){
            Connection conn = new DBSelectionConnection(env);
            return conn;
        }
        return null;
    }
    
    protected void closeUserDB() throws DBException {
        indexConn.close();
    }
    
    protected void syncUserDB() throws DBException {
        indexConn.sync();
    }
    
}

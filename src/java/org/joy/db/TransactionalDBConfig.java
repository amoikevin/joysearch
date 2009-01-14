/*
 * TransactionalDBConfig.java
 *
 * Created on 2007年8月3日, 下午12:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.db;

import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseType;


/**
 *
 * @author 海
 */
public class TransactionalDBConfig extends DatabaseConfig{
    
    /** Creates a new instance of TransactionalDBConfig */
    public TransactionalDBConfig() {
        setType(DatabaseType.BTREE);
        setAllowCreate(true);
        setTransactional(true);
    }
    
}

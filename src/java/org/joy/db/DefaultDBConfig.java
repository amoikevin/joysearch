/*
 * WebBaseConfiguration.java
 *
 * Created on 2007年8月2日, 下午11:40
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
public class DefaultDBConfig extends DatabaseConfig{
    
    /** Creates a new instance of WebBaseConfiguration */
    public DefaultDBConfig() {
        setType(DatabaseType.BTREE);
        setAllowCreate(true);
    }
    
}

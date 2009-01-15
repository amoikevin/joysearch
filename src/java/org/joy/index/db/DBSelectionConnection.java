/*
 * IndexConnection.java
 *
 * Created on 2007��10��19��, ����8:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.index.db;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.Environment;
import com.sleepycat.db.SecondaryConfig;
import com.sleepycat.db.SecondaryDatabase;
import com.sleepycat.db.SecondaryKeyCreator;
import org.joy.db.Connection;
import org.joy.db.DBException;
import org.joy.db.DefaultDBConfig;
class ShortNameKeyCreator implements SecondaryKeyCreator {
    
    public boolean createSecondaryKey(SecondaryDatabase secondaryDatabase,
            
            DatabaseEntry key, DatabaseEntry value, DatabaseEntry resultEntry) throws DatabaseException {
            resultEntry = value;
        return true;
    }
}
/**
 *
 * @author suda1
 */
public class DBSelectionConnection extends Connection{
    private SecondaryDatabase shortNameIndex;
    /** 
     * 建立一个DB选择表
     * @param env 所采用的DBPlatform环境
     * @throws org.joy.db.DBException
     */
    DBSelectionConnection(Environment env) throws DBException {
        super(env,"DBSelection",new DefaultDBConfig());
        //添加相应的索引
        SecondaryConfig secConfig = new SecondaryConfig();
        secConfig.setType(DatabaseType.BTREE);
        secConfig.setSortedDuplicates(true);
        secConfig.setAllowCreate(true);
        secConfig.setKeyCreator(new ShortNameKeyCreator());
        
        try {
            shortNameIndex = env.openSecondaryDatabase(null, "short name",  "short name",db, secConfig);
            addSecDb("short name",shortNameIndex);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    public SecondaryDatabase getShortNameIndex(){
        return shortNameIndex;
    }
    public String getDbName() {
        return "DBSelection";
    }
    
    public EntryBinding getKeyBinding() {
        return new LongBinding();
    }
    
    public EntryBinding getValueBinding() {
        return new StringBinding();
    }
}

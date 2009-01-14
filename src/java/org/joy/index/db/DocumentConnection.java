/*
 * DocumentConnection.java
 *
 * Created on 2007年12月12日, 上午10:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.index.db;

import org.joy.index.db.entry.DocumentBinding;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.Environment;
import com.sleepycat.db.SecondaryConfig;
import com.sleepycat.db.SecondaryDatabase;
import com.sleepycat.db.SecondaryKeyCreator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.db.Connection;
import org.joy.db.DBException;
import org.joy.db.DefaultDBConfig;

class HashKeyCreator implements SecondaryKeyCreator {
    
    public boolean createSecondaryKey(SecondaryDatabase secondaryDatabase,
            DatabaseEntry key, DatabaseEntry value, DatabaseEntry resultEntry) throws DatabaseException {
        String URL = StringBinding.entryToString(key);
        IntegerBinding.intToEntry(URL.hashCode(), resultEntry);
        return true;
    }
}

/**
 *
 * @author 海
 */
public class DocumentConnection extends Connection {
    
    private EntryBinding valueBinding;
    private SecondaryDatabase hashIndex;
    
    /** Creates a new instance of DocumentConnection */
    public DocumentConnection(Environment env) throws DBException {
        super(env, "Doc", new DefaultDBConfig());
        //打开索引
        //按状态索引
        SecondaryConfig secConfig = new SecondaryConfig();
        secConfig.setType(DatabaseType.BTREE);
        //secConfig.setSortedDuplicates(true);
        secConfig.setAllowCreate(true);
        secConfig.setKeyCreator(new HashKeyCreator());
        
        try {
            hashIndex = env.openSecondaryDatabase(null, "ID index", "ID index", db, secConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        valueBinding = new DocumentBinding();
    }
    
    public String getDbName() {
        return "Doc";
    }
    
    public EntryBinding getKeyBinding() {
        return new StringBinding();
    }
    
    public EntryBinding getValueBinding() {
        return valueBinding;
    }
    
    public SecondaryDatabase getHashIndex() {
        return hashIndex;
    }
    
    @Override
    public void close() throws DBException {
        super.close();
        try {
            hashIndex.close();
        } catch (DatabaseException ex) {
            Logger.getLogger(DocumentConnection.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        }
    }
    
    @Override
    public void sync() throws DBException {
        super.sync();
        try {
            hashIndex.sync();
        } catch (DatabaseException ex) {
            Logger.getLogger(DocumentConnection.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        }
    }
}

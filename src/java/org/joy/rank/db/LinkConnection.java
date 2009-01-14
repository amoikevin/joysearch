/*
 * LinkConnection.java
 *
 * Created on 2007年11月26日, 上午12:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.rank.db;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.Environment;
import com.sleepycat.db.SecondaryConfig;
import com.sleepycat.db.SecondaryDatabase;
import com.sleepycat.db.SecondaryMultiKeyCreator;
import java.util.Set;
import org.joy.db.DBException;
import org.joy.db.Connection;
import org.joy.db.DefaultDBConfig;

/**
 * 二级索引生成类。利用数据库中网页最后更新日期生成数据库的二级索引
 * @author AC
 */
class KeyCreator implements SecondaryMultiKeyCreator {
    
    StoredClassCatalog classCatalog;
    
    public KeyCreator(StoredClassCatalog classCatalog) {
        this.classCatalog = classCatalog;
    }
    
    
    public void createSecondaryKeys(SecondaryDatabase secondaryDatabase,
            DatabaseEntry key,
            DatabaseEntry value,
            Set results) throws DatabaseException {
        EntryBinding dataBinding = new SerialBinding(classCatalog, LinkEntry.class);
        LinkEntry entry = (LinkEntry) dataBinding.entryToObject(value);
        for(Long l:entry.getLinks()){
            DatabaseEntry result = new DatabaseEntry();
            LongBinding.longToEntry(l,result);
            results.add(result);
        }
    }
}
/**
 *
 * @author 海
 */
public class LinkConnection extends Connection{
    private SecondaryDatabase linkIndex;
    private EntryBinding valueBinding;
    /** Creates a new instance of LinkConnection */
    public LinkConnection(Environment env,StoredClassCatalog classCatalog) throws DBException {
        super(env,"link",new DefaultDBConfig());
        try {
            //按被指向的网页索引
            SecondaryConfig secConfig = new SecondaryConfig();
            secConfig.setType(DatabaseType.BTREE);
            secConfig.setSortedDuplicates(true);
            secConfig.setAllowCreate(true);
            secConfig.setMultiKeyCreator(new KeyCreator(classCatalog));
            
            linkIndex = env.openSecondaryDatabase(null, "link index", "link index",db, secConfig);
            addSecDb("link index",linkIndex);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        valueBinding = new SerialBinding(classCatalog,LinkEntry.class);
    }
    
    public String getDbName() {
        return "link";
    }
    
    public EntryBinding getKeyBinding() {
        return new LongBinding();
    }
    
    public EntryBinding getValueBinding() {
        return valueBinding;
    }
    
    public SecondaryDatabase getLinkIndex() {
        return linkIndex;
    }
}

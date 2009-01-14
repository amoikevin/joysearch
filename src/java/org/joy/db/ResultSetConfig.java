/*
 * ResultSetConfig.java
 *
 * Created on 2008年2月28日, 下午8:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.db;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.DatabaseType;
import java.util.Comparator;

/**
 *
 * @author 海
 */
public class ResultSetConfig {
    private EntryBinding valBinding = new StringBinding();
    private EntryBinding keyBinding = new StringBinding();
    private boolean supportDup = false;
    private Comparator comparator;
    private DatabaseType type = DatabaseType.BTREE;
    private long bufferSize=1024*1024*5;

    public ResultSetConfig(){
        
    }
    
    public EntryBinding getValBinding() {
        return valBinding;
    }

    public void setValBinding(EntryBinding valBinding) {
        this.valBinding = valBinding;
    }

    public EntryBinding getKeyBinding() {
        return keyBinding;
    }

    public void setKeyBinding(EntryBinding keyBinding) {
        this.keyBinding = keyBinding;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public long getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(long bufferSize) {
        this.bufferSize = bufferSize;
    }

    public DatabaseType getType() {
        return type;
    }

    public void setType(DatabaseType type) {
        this.type = type;
    }

    public boolean isSupportDup() {
        return supportDup;
    }

    public void setSupportDup(boolean supportDup) {
        this.supportDup = supportDup;
    }

    
}

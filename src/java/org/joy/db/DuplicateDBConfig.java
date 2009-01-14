/*
 * DuplicateDatabaseConfig.java
 *
 * Created on 2007年8月2日, 下午11:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.db;

import java.util.Comparator;

/**
 *
 * @author 海
 */
public class DuplicateDBConfig extends DefaultDBConfig{
    
    /** Creates a new instance of DuplicateDatabaseConfig */
    public DuplicateDBConfig() {
        setSortedDuplicates(true);
    }
    public DuplicateDBConfig(Comparator comparator) {
        setSortedDuplicates(true);
        setDuplicateComparator(comparator);
    }
}

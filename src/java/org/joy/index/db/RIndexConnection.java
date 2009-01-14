/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.db;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.Environment;
import java.util.Comparator;
import org.joy.db.Connection;
import org.joy.db.DBException;
import org.joy.db.DuplicateDBConfig;
import org.joy.index.db.entry.CompactHit;
import org.joy.index.db.entry.CompactHitBinding;

class WeightComparator implements Comparator {
    private CompactHitBinding chb = new CompactHitBinding();
   @Override
    public int compare(Object d1, Object d2) {
        byte[] b1 = (byte[]) d1;
        byte[] b2 = (byte[]) d2;
        DatabaseEntry de1 = new DatabaseEntry(b1);
        DatabaseEntry de2 = new DatabaseEntry(b2);
        CompactHit cp1  = (CompactHit) chb.entryToObject(de1);
        CompactHit cp2  = (CompactHit) chb.entryToObject(de2);


        int cmp = cp2.getWeight() - cp1.getWeight();
        if (cmp == 0) {
            return cp2.getHash() - cp1.getHash();
        }
        return cmp;
    }
}

/**
 *
 * @author 海
 */
public class RIndexConnection extends Connection {

    private EntryBinding keyBinding;
    private EntryBinding valBinding;

    public RIndexConnection(Environment env) throws DBException {
        super(env, "ReverseIndex", new DuplicateDBConfig(new WeightComparator()));
        keyBinding = new StringBinding();
        valBinding = new CompactHitBinding();
    }

    @Override
    public String getDbName() {
        return "ReverseIndex";
    }

    @Override
    public EntryBinding getKeyBinding() {
        return keyBinding;
    }

    @Override
    public EntryBinding getValueBinding() {
        return valBinding;
    }
}

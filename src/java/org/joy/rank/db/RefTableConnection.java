/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.rank.db;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.Environment;
import org.joy.db.Connection;
import org.joy.db.DBException;
import org.joy.db.DefaultDBConfig;

/**
 *
 * @author Lamfeeling
 */
public class RefTableConnection extends Connection {

    public RefTableConnection(Environment env, StoredClassCatalog classCatalog) throws DBException {
        super(env, "Ref", new DefaultDBConfig());
    }

    @Override
    public String getDbName() {
        return "Ref";
    }

    @Override
    public EntryBinding getKeyBinding() {
        return new LongBinding();
    }

    @Override
    public EntryBinding getValueBinding() {
        return new StringBinding();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.db;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.db.Environment;
import com.sleepycat.db.SecondaryDatabase;

/**
 *
 * @author 海
 */
public abstract class SecondaryConnection extends Connection {

    public SecondaryConnection(Environment env, SecondaryDatabase sDb) {
        super(env, sDb);
    }

    public abstract EntryBinding getPrimaryKeyBinding();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.db;

import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.LockDetectMode;
import java.io.File;

/**
 *
 * @author 海
 */
public abstract class Table extends Connection {

    private static Environment env = null;

    private static Environment getTempEnv() throws DBException {
        try {
            if (env == null) {
                EnvironmentConfig envConfig = new EnvironmentConfig();
                envConfig.setPrivate(true);

                envConfig.setAllowCreate(true);
                envConfig.setInitializeCDB(true);
                envConfig.setInitializeCache(true);
                envConfig.setLockDetectMode(LockDetectMode.MINWRITE);
                envConfig.setCacheSize(10 * 1024 * 1024);
                File path = new File("temp");
                if (!path.exists()) {
                    path.mkdir();
                }
                env = new Environment(path, envConfig);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        return env;
    }

    public Table(String dbName, DatabaseConfig dbConfig) throws DBException {
        super(getTempEnv(), dbName, dbConfig);
    }
}

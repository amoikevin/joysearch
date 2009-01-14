/*
 * AbstractPlatform.java
 *
 * Created on 2007年10月19日, 下午7:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.db;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.LockDetectMode;
import com.sleepycat.db.StatsConfig;
import java.io.File;
import java.util.Vector;

/**
 * 抽象的数据库平台类，如果要重用该方法，可继承该类，并且重写方法
 * @author 柳松
 */
public abstract class AbstractPlatform {

    /**
     * 底层的数据库环境，一个Environment对象
     */
    protected Environment env;
    /**
     * 数据库最大可以提供的抽象层数
     */
    public static int Max_Table = 50;
    /**
     * 数据库的连接池，集中了所有的数据库抽象层
     */
    protected Vector<Connection> connPool = new Vector<Connection>();
    private String path;

    /**
     * 创建一个新的数据库平台
     * @param path 数据库平台路径
     * @throws daphne.db.DBException 如果出现数据库错误则返回该错误
     */
    public AbstractPlatform(String path, long cacheSize) throws DBException {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setPrivate(true);

        envConfig.setAllowCreate(true);
        envConfig.setPrivate(true);
        envConfig.setInitializeCDB(true);
        envConfig.setInitializeCache(true);
        envConfig.setLockDetectMode(LockDetectMode.MINWRITE);
        envConfig.setCacheSize(cacheSize);
        try {
            File envFolder = new File("c:\\joydk\\db\\");
            if (!envFolder.exists()) {
                envFolder.mkdir();
            }
            File workFolder = new File("c:\\joydk\\db\\" + path);
            if (!workFolder.exists()) {
                workFolder.mkdir();
            }
            env = new Environment(workFolder, envConfig);
            System.out.println(env.getCacheStats(new StatsConfig()).getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException("打开数据库错误！");
        }
    }

    public AbstractPlatform(String path) throws DBException {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setPrivate(true);

        envConfig.setAllowCreate(true);
        envConfig.setPrivate(true);
        envConfig.setInitializeCDB(true);
        envConfig.setInitializeCache(true);
        envConfig.setLockDetectMode(LockDetectMode.MINWRITE);
        envConfig.setCacheSize(1024 * 1024 * 32);
        try {
            File envFolder = new File("c:\\JoyDK\\db\\");
            if (!envFolder.exists()) {
                envFolder.mkdir();
            }
            File workFolder = new File(envFolder+"\\"+ path);
            if (!workFolder.exists()) {
                workFolder.mkdir();
            }
            env = new Environment(workFolder, envConfig);
            System.out.println(env.getCacheStats(new StatsConfig()).getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException("打开数据库错误！");
        }
    }

    /**
     * 返回服务器数据库平台的Environment对象实例
     * @return 返回服务器数据库平台的Environment对象实例
     */
    public Environment getEnv() {
        return env;
    }

    /**
     * 返回数据库平台的路径名称
     * @return 返回数据库平台的路径名称
     * @throws daphne.db.DBException 如果数据库错误则抛出该异常
     */
    public String getPath() throws DBException {
        try {
            return env.getHome().getPath();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }

    /**
     * 设定数据库平台使用的内存占整个jvm的百分之几
     * @param percent 百分数
     * @throws daphne.db.DBException 如果发生数据库错误，则抛出该异常
     */
    public void setBufferSize(int percent) throws DBException {
        try {
            EnvironmentConfig config = env.getConfig();
            config.setCacheSize(Runtime.getRuntime().maxMemory() * percent / 100);
            env.setConfig(config);
            System.out.println(env.getCacheStats(new StatsConfig()).getBytes());
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }

    /**
     * 抽象层工厂。集中了所有抽象层。
     * @param DBName 要打开的数据库名称
     * @throws daphne.db.DBException 如果发生数据库错误，则抛出该异常
     * @return 返回一个数据库抽象层
     */
    protected abstract Connection connectionFactory(String DBName) throws DBException;

    /**
     * 关闭用户数据库。必须被实现的类重写，在这里关闭所有的子类打开的数据库
     * @throws daphne.db.DBException 如果发生数据库错误，则抛出该异常
     */
    protected abstract void closeUserDB() throws DBException;

    /**
     * 同步用户数据库，必须为继承的子类重写，同步所有子类中打开的用户数据库
     * @throws daphne.db.DBException 如果发生数据库错误，则抛出该异常
     */
    protected abstract void syncUserDB() throws DBException;

    /**
     * 同步所有数据库
     * @throws daphne.db.DBException 如果发生数据库错误，则抛出该异常
     */
    public void sync() throws DBException {
        syncUserDB();
    }

    /**
     * 同步所有数据库，然后关闭它们
     * @throws daphne.db.DBException 如果发生数据库错误，则抛出该异常
     */
    public void syncAndClose() throws DBException {
        sync();
        for (Connection conn : connPool) {
            conn.close();
        }
        closeUserDB();
        try {
            env.close();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }

    /**
     * 打开一个基于某个数据库的数据库连接抽象层。如果现在的连接数已经大于最大可以提供的连接，则返回Null
     * @return 数据库抽象连接层
     * @param DBName 要打开的数据库名
     * @throws daphne.db.DBException 如果发生数据库错误，则抛出该异常
     */
    public synchronized Connection open(String DBName) throws DBException {
        //找找缓冲当中有没有没有用的这个数据库连接
        for (Connection conn : connPool) {
            if (conn.isFreed() && conn.getDbName().equals(DBName)) {
                conn.setBusy();
                return conn;
            }
        }
        //如果找不到就新建一个
        if (connPool.size() == Max_Table) {
            //找找看有没有没有用的,删除一个
            Connection free = null;
            for (Connection conn : connPool) {
                if (conn.isFreed()) {
                    //关掉这个连接
                    conn.close();
                    free = conn;
                    break;
                }
            }
            //把这个连接赶出缓冲区
            if (free != null) {
                connPool.remove(free);
            } //如果没有空闲的
            else {
                return null;
            }
        }
        Connection conn = connectionFactory(DBName);
        connPool.add(conn);
        return conn;
    }

    public Txn beginTransaction(Txn parent) throws DBException {
        try {
            if (parent != null) {
                return new Txn(env.beginTransaction(parent.getTxn(), null));
            } else {
                return new Txn(env.beginTransaction(null, null));
            }
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
}

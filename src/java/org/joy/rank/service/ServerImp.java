/*
 * ServerImp.java
 *
 * Created on 2007年11月26日, 上午12:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.rank.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;
import org.joy.db.DBException;
import org.joy.db.Connection;
import org.joy.db.Command;
import org.joy.db.ResultReader;
import org.joy.dblookup.service.DBGroup;
import org.joy.dblookup.service.DBGroupProxy;
import org.joy.dblookup.service.RankUpdateJob;
import org.joy.dblookup.service.UID;
import org.joy.rank.db.LinkCommand;
import org.joy.rank.db.LinkConnection;
import org.joy.rank.db.LinkEntry;
import org.joy.rank.db.Platform;
import org.joy.deployer.Deployer;

/**
 *
 * @author 海
 */
public class ServerImp extends UnicastRemoteObject implements LinkSystemAnalyzer {

    Platform platform;
    private DBGroupProxy dbG;
    Hashtable<Long, Float> rank = new Hashtable<Long, Float>();
    Command refCmd;
    LinkCommand linkCmd;

    public ServerImp(DBGroup group) throws DBException, RemoteException {
        super();
        checkMem();
        platform = new Platform(1024 * 1024 * 5);
        this.refCmd = new Command(platform.open("Ref"));
        this.linkCmd = new LinkCommand((LinkConnection) platform.open("link"));
        this.dbG = new DBGroupProxy(group);
    }

    public void addLink(String from, String to) throws DBException {
        ResultReader reader = linkCmd.search(UID.from(from), true);
        LinkEntry entry;
        if (!reader.next()) {
            entry = new LinkEntry();
            entry.addLink(to);
            reader.put(UID.from(from), entry);
        } else {
            entry = (LinkEntry) reader.getValue();
            entry.addLink(to);
            reader.put(UID.from(from), entry);
        }
        reader.close();
        entry = (LinkEntry) linkCmd.getEntry(UID.from(to));
        if (entry == null) {
            linkCmd.setEntry(UID.from(to), new LinkEntry());
        }
        //添加到对照表
        refCmd.setEntry(UID.from(from), from);
        refCmd.setEntry(UID.from(to), to);
        System.out.println(from + "to" + to);
    }

    public static void checkMem() {
        Deployer.logger.info("检查内存情况");
        Deployer.logger.info("最大内存" + Runtime.getRuntime().maxMemory());
        Deployer.logger.info("总共内存" + Runtime.getRuntime().totalMemory());
        Deployer.logger.info("可用内存" + Runtime.getRuntime().freeMemory());
        Deployer.logger.info("可用内存占" +
                Runtime.getRuntime().freeMemory() / (float) Runtime.getRuntime().totalMemory() * 100 + "%");
        Deployer.logger.info("\n");
    }

    public void calPageRank() throws DBException {
        Deployer.logger.info("开始计算");
        checkMem();
        synchronized (this) {
            rank.clear();
            Runtime.getRuntime().gc();
            //计算矩阵
            rank = linkCmd.calPageRank();
            //分发到各各分数据库
            Enumeration<Long> keys = rank.keys();
            while (keys.hasMoreElements()) {
                Long hash = keys.nextElement();
                try {
                    String URL = (String) refCmd.getEntry(hash);
                    dbG.putJob(new RankUpdateJob(URL, rank.get(hash)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            Deployer.logger.info("计算完成");
        }
    }

    public void removeLink(String URL) throws DBException {
        linkCmd.removeEntry(UID.from(URL));
    }

    public float getRank(String URL) {
        return rank.get(UID.from(URL));
    }

    public void shutdown() throws DBException {
        platform.syncAndClose();
    }

    public void clear() throws DBException, RemoteException {
        Deployer.logger.info("清理前内存使用情况");
        checkMem();
        linkCmd.clear();
        rank.clear();
        Runtime.getRuntime().gc();
        Deployer.logger.info("清理之后。。。");
        checkMem();
    }

    public String[] getRefs(String URL) throws DBException, RemoteException {
        Connection conn = platform.open("link");
        LinkCommand cmd = new LinkCommand((LinkConnection) conn);
        Long[] refs = cmd.getRefs(URL);
        String[] res = new String[refs.length];
        int i = 0;
        for (long ref : refs) {
            res[i] = (String) refCmd.getEntry(ref);
            i++;
        }
        conn.setFree();
        return res;
    }
}

/*
 * Main.java
 *
 * Created on 2007年10月25日, 下午4:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.analyzer.service;

import org.joy.deployer.Deployer;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.dblookup.service.DBGroup;
import org.joy.lookup.service.Locator;
import org.joy.rank.service.LinkSystemAnalyzer;
import org.joy.spider.service.SpiderGroup;

/**
 *
 * @author suda1
 */
public class Main extends Deployer {

    ServerImp server;
    AnalyzerGroup g;
    Locator locator;

    /** Creates a new instance of Main */
    public Main() {
        super("AnalysisNode");
        String sID = UUID.randomUUID().toString();
        String serviceName = "rmi://" + IPAddress + "/" + sID;
        locator = new Locator(Analyzer.class, serviceName);
    }

    private void syncHosts(AnalyzerGroup aG) throws RemoteException {
        server.getAllowed().addAll(aG.getHostsAllowed());
        System.out.println(Arrays.toString(aG.getHostsAllowed().toArray(new String[0])));
        server.getDenied().addAll(aG.getHostsDenied());
    }

    public void run() {
        try {
            Deployer.logger.info("正则扫描服务器初始化开始...");
            //加入计算组
            g = (AnalyzerGroup) lookUp("AnaGroup");
            //获取蜘蛛组
            server = new ServerImp((DBGroup) lookUp("DBGroup"),
                    (LinkSystemAnalyzer) lookUp("RankServer"),
                    (SpiderGroup) lookUp("SpiderGroup"));
            //同步允许和禁止主机名
            syncHosts(g);
            Deployer.logger.info("Analyzer Started! with name" + locator.getShortName());
            server.start();
            register(server, locator.getShortName());
            g.register(locator);

            Deployer.logger.info("正则扫描服务器准备就绪！");
            done();
            try {
                while (buf.readLine().equals("q")) {
                    System.exit(0);
                }
            } catch (Exception ex) {
                System.exit(0);
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    public static void main(String[] args) throws Exception {
        (new Main()).run();
    }

    @Override
    public void cleanup() {
        try {
            g.unregister(locator);
            server.close();
            Deployer.logger.info("服务器退出");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

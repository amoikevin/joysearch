/*
 * Main.java
 *
 * Created on 2007年11月26日, 上午12:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.rank.service;

import org.joy.deployer.Deployer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.dblookup.service.DBGroup;

/**
 *
 * @author 海
 */
public class Main extends Deployer {

    ServerImp server;

    /** Creates a new instance of Main */
    public Main() {
        super("RankServer");
    }

    public void run() {
        try {
            Deployer.logger.info("PageRank服务器初始化开始...");
            server = new ServerImp((DBGroup)lookUp("DBGroup"));
            register(server);
            Deployer.logger.info("PageRank服务器准备就绪！");
            done();
            try {
                while (buf.readLine().equals("q")) {
                    System.exit(0);
                }
            } catch (Exception ex) {
                System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        (new Main()).run();
    }

    @Override
    public void cleanup() {
        try {
            server.shutdown();
            registry.unbind("Odessay");
            Deployer.logger.info("服务器退出");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

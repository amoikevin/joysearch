/*
 * Main.java
 *
 * Created on 2007年12月11日, 上午11:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.index.service;

import org.joy.deployer.Deployer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.joy.dblookup.service.DBGroup;
import org.joy.dblookup.service.Database;
import org.joy.lookup.service.Locator;

/**
 *
 * @author 海
 */
public class Main extends Deployer {

    private ServerImp server;
    private DBGroup dbG;
    private Locator locator;

    /** Creates a new instance of Main */
    public Main() {
        super("DataNode");
        String sID = "rmi://" + IPAddress + "/" + "DataNode";
        locator = new Locator(Database.class, sID);
    }

    public static void main(String[] args) {
        (new Main()).run();
    }

    @Override
    public void run() {
        try {
            server = new ServerImp();
            register(server);
            Deployer.logger.info("Barton初始化开始...");
            dbG = (DBGroup) lookUp("DBGroup");
            dbG.register(locator);
            Deployer.logger.info("Barton服务器准备就绪！");
            done();
            try {
                while (buf.readLine().equals("q")) {
                    System.exit(0);
                }
            } catch (Exception ex) {
                System.exit(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "发现错误在" + this.getClass().toString() + "aaa" + "\n" + ex);
            ex.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void cleanup() {
        try {
            server.shutdown();
            dbG.unregister(locator);
            registry.unbind("Barton");
        // Deployer.logger.info("服务器退出");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

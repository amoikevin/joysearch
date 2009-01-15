/*
 * Main.java
 *
 * Created on 2007年5月13日, 下午12:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.spider.service;

import java.rmi.RemoteException;
import java.util.UUID;
import org.joy.deployer.Deployer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.group.service.Group;
import org.joy.group.service.Locator;

/**
 *
 * @author AC
 */
public class Main extends Deployer {

    private ServerImp spider;
    private SpiderGroup group;
    private Locator locator;

    public Main() {
        super("SpiderNode");
        try {
            String sID = UUID.randomUUID().toString();
            String serviceName = "rmi://" + IPAddress + "/" + sID;
            locator = new Locator(Spider.class, serviceName);
            Deployer.logger.info("寻找组...");
            group = (SpiderGroup) lookUp("SpiderGroup");
        } catch (RemoteException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void run() {
        try {
            Deployer.logger.info("下载服务器初始化开始...");
            Deployer.logger.info("注册分析器");

            spider = new ServerImp((Group) lookUp("AnaGroup"),
                    (SpiderGroup) lookUp("SpiderGroup"));
            Deployer.logger.info("启动成功，开始配置...");
            register(spider, locator.getShortName());
            spider.start();
            group.register(locator);
            Deployer.logger.info("成功！");
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        (new Main()).run();
    }

    @Override
    public void cleanup() {
        try {
            spider.stop();
            group.unregister(locator);
            Deployer.logger.info("服务器退出！");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.spider.service;

import org.joy.deployer.Deployer;

/**
 *
 * @author Lamfeeling
 */
public class SpiderGroupStarter extends Deployer {
    private SpiderGroupImp server;
    public SpiderGroupStarter() {
        super("SpiderGroup","dist/Group.jar");
    }

    @Override
    public void run() {
        try {
            logger.info("蜘蛛服务器组注册器开始启动");
             server = new SpiderGroupImp();
            register(server);
            logger.info("蜘蛛服务器组注册器成功启动");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new SpiderGroupStarter().run();
    }

    @Override
    public void cleanup() {
        server.shutdown();
    }
}

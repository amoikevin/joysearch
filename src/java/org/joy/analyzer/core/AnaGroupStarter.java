/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.core;

import org.joy.deployer.Deployer;

/**
 * 分析集群挂载器节点启动器
 * @author Lamfeeling
 */
public class AnaGroupStarter extends Deployer {
    /**
     * 挂载器构造函数
     */
    public AnaGroupStarter() {
        super("AnaGroup");
    }

    @Override
    public void run() {
        try {
            logger.info("分析服务器组注册器开始启动");
            AnalyzerGroupImp server = new AnalyzerGroupImp(properties);
            register(server);
            logger.info("分析服务器组注册器成功启动");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new AnaGroupStarter().run();
    }

    @Override
    public void cleanup() {
    }
}

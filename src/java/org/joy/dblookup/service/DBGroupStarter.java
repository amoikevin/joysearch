/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.dblookup.service;


import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.db.DBException;
import org.joy.deployer.Deployer;
/**
 *
 * @author Lamfeeling
 */
public class DBGroupStarter extends Deployer {
    private DBGroupImp server;
    public DBGroupStarter() {
        super("DBGroup");
    }

    @Override
    public void run() {
        try {
            logger.info("DB服务器组注册器开始启动");
            server = new DBGroupImp();
            register(server);
            logger.info("DB服务器组注册器成功启动");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
            System.exit(0);
        }
    }
    public static void main(String []args){
        new DBGroupStarter().run();
    }
    @Override
    public void cleanup() {
        try {
            server.shutdown();
        } catch (DBException ex) {
            Logger.getLogger(DBGroupStarter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

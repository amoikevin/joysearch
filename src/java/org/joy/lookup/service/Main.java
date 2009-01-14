/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.lookup.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Arrays;
import org.joy.analyzer.service.AnaGroupStarter;
import org.joy.dblookup.service.DBGroupStarter;
import org.joy.spider.service.SpiderGroupStarter;

/**
 *
 * @author Lamfeeling
 */
public class Main {

    public Main() {
    }

    private static void done() throws IOException {
        File f = new File("c:/Group.done");
        f.createNewFile();
        f.deleteOnExit();
    }

    public static void main(String[] args) throws Exception {
        new SpiderGroupStarter().run();
        new DBGroupStarter().run();
        new AnaGroupStarter().run();
        done();

        BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        System.setSecurityManager(new RMISecurityManager());
        try {
            while (buf.readLine().equals("q")) {
                System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        System.out.println("集群启动成功");
    }
}

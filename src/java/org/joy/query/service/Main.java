/*
 * Main.java
 *
 * Created on 2007年6月7日, 下午7:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.query.service;

import java.rmi.server.UnicastRemoteObject;
import org.joy.deployer.Deployer;
import java.util.Calendar;
import javax.swing.JOptionPane;
import org.joy.index.service.DBGroup;
import org.joy.query.Result;
/**
 *
 * @author AC
 */
import org.joy.index.service.DBGroupProxy;

public class Main extends Deployer {

    ServerImp server;

    public Main() {
        super("QueryServer");
    }

    /**
     * @param args the command line arguments
     */
    @Override
    public void run() {
        try {
            DBGroupProxy indexer = new DBGroupProxy((DBGroup) lookUp("DBGroup"));
            server = new ServerImp(indexer);

            for (String s : indexer.getDBHandles()) {
                server.addServer(s);
            }
            //绑定服务器
            register(server);
            long start = Calendar.getInstance().getTimeInMillis();

            Result[] ies = server.getPage("苏州大学", 0);
            long end = Calendar.getInstance().getTimeInMillis();
            System.out.println("搜索功耗时" + (end - start) + "毫秒");
            System.out.println("找到:" + ies.length + "个结果");
            for (Result ie : ies) {
                System.out.println(ie.getURL() + " " + ie.getRate());
                System.out.println(ie.getTitle());
            }
            done();
            try {
                while (buf.readLine().equals("q")) {
                    System.exit(0);
                }
            } catch (Exception ex) {
                System.exit(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        (new Main()).run();
    }

    @Override
    public void cleanup() {
        server.shutdown();
    }
}

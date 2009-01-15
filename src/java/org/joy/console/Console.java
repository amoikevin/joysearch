/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.joy.analyzer.service.AnalyzerGroup;
import org.joy.analyzer.service.AnalyzerGroupProxy;
import org.joy.db.DBException;
import org.joy.dblookup.service.DBGroup;
import org.joy.dblookup.service.DBGroupProxy;
import org.joy.rank.service.LinkSystemAnalyzer;
import org.joy.spider.service.SpiderGroup;
import org.joy.spider.service.SpiderGroupProxy;
import org.joy.spider.service.DownloadJob;

/**
 * 控制台类，负责对整个系统的调试和启动以及设置相应的参数
 * @author 柳松*/
public class Console {

    /**
     * 检索服务查找远程接口
     */
    private DBGroupProxy indexer;
    /**
     * PageRank计算器的远程接口
     */
    private LinkSystemAnalyzer rank;
    /**
     * 分析服务查找远程接口
     */
    private AnalyzerGroupProxy pool;
    private SpiderGroupProxy sG;
    /**
     * 刷新延时。意味着所有的网页索引自上次索引之后多久会被重新建立。
     */
    private long refreshDelay;

    /**
     * 构造函数，输入刷新时延和输入的配置文件之后控制台将被启动。
     * @param refreshDelay 刷新延时。意味着所有的网页索引自上次索引之后多久会被重新建立
     * @param confFile 配置文件名
     * @throws org.joy.console.ConsoleException 如果启动不成功，则抛出这个异常
     */
    public Console(long refreshDelay, String confFile) throws ConsoleException {
        try {
            System.setProperty("java.security.policy", "conf/policy.txt");
            System.setSecurityManager(new RMISecurityManager());
            //查找远程接口
            indexer = new DBGroupProxy((DBGroup) Naming.lookup("DBGroup"));
            pool = new AnalyzerGroupProxy((AnalyzerGroup) Naming.lookup("AnaGroup"));
            sG = new SpiderGroupProxy((SpiderGroup) Naming.lookup("SpiderGroup"));
            //对于PageRank服务器，如果在本机上查找不到远程接口，可以允许用户在其他机器上寻找。
            while (true) {
                try {
                    rank = (LinkSystemAnalyzer) Naming.lookup("RankServer");
                    break;
                } catch (NotBoundException e) {
                    String address = "rmi://" + JOptionPane.showInputDialog("请输入PageRank计算器的地址") + "/Odessay";
                    rank = (LinkSystemAnalyzer) Naming.lookup(address);
                }
            }
            //设定刷新时延
            this.refreshDelay = refreshDelay;
        } catch (Exception ex) {
            throw new ConsoleException(ex);
        }
    }

    public LinkSystemAnalyzer getRank() {
        return rank;
    }

    public long getRefreshDelay() {
        return refreshDelay;
    }

    public void setRefreshDelay(long refreshDelay) {
        this.refreshDelay = refreshDelay;
    }

    public void stopAnas() {
        try {
            pool.stop();
            System.out.println("所有分析器已经关闭");
        } catch (Exception ex) {
            Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 命令停止所有的分析服务器
     * @throws org.joy.db.DBException
     */
    public void startAnas() {
        try {
            pool.start();
            System.out.println("所有分析器开启");
        } catch (Exception ex) {
            Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 立即建立索引
     */
    public void index() {
        System.out.println("开始建立索引..");
        try {
            rank.calPageRank();
            System.out.println("pageRank done");
            indexer.index();
            System.out.println("index done");
        } catch (Exception ex) {
            Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 注册一个入口地址
     * @param URL 要注册的入口地址
     */
    public void regURL(String URL) {
        try {
            sG.putJob(new DownloadJob(URL, 1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 向索引域当中添加一个主机名
     * @param host 要添加的主机名
     */
    public void addHost(String host) {
        try {
            ((AnalyzerGroup) Naming.lookup("AnaGroup")).getHostsAllowed().add(host);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 向排除索引域当中添加一个主机名
     * @param host 要添加的主机名
     */
    public void addHostE(String host) {
        try {
            ((AnalyzerGroup) Naming.lookup("AnaGroup")).getHostsDenied().add(host);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 启动Joysearch
     * @param URL
     * @param indexDelay
     */
    public void startWith(final String URL, final long indexDelay) {
        System.out.println("启动Joysearch中");
        //启动Timer,设定刷新时延
        Timer refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    //清空当前索引
                    indexer.clear();
                    rank.clear();
                    sG.reset();
                    //注册一个入口地址
                    regURL(URL);
                    //设置定时器定时索引
                    new Timer().schedule(new TimerTask() {

                        @Override
                        public void run() {
                            try {
                                indexer.index();
                                System.out.println("建立索引完毕");
                            } catch (Exception ex) {
                                Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }, indexDelay);

                    //设定定时器定是计算PageRank

                    new Timer().schedule(new TimerTask() {

                        @Override
                        public void run() {
                            try {
                                rank.calPageRank();
                                System.out.println("PageRank计算完毕");
                            } catch (Exception ex) {
                                Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }, indexDelay);
                } catch (Exception ex) {
                    Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 0, getRefreshDelay());
        System.out.println("Joysearch Started.");
    }

    /**
     * 控制台启动入口
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("Joysearch控制台,Joysearch 2008 (R),帮助输入h+回车");

            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            Console m = new Console(1000 * 60 * 60 * 8, "Config.conf");

            while (true) {
                //命令列表
                String cmd = buf.readLine();
                if (cmd.equals("q")) {
                    System.exit(0);
                }
                if (cmd.equals("h")) {
                    System.out.println("爬行器启动-s");
                    System.out.println("注册入口地址-r");
                    System.out.println("建立索引-i");
                    System.out.println("查看链接引用情况-ref");
                    System.out.println("添加爬行服务器名-a");
                    System.out.println("添加禁止爬行服务器名-e");
                    System.out.println("查看链接的PageRank-rank");
                    System.out.println("例如，输入s之后，输入重建索引时间，索引入口地址，建立索引延迟，即可启动自动检索");
                    continue;
                }
                if (cmd.equals("i")) {
                    m.index();
                    continue;
                }
                if (cmd.equals("a")) {
                    System.out.println("Host:");
                    m.addHost(buf.readLine());
                    continue;
                }
                if (cmd.equals("e")) {
                    System.out.println("ExHost:");
                    m.addHostE(buf.readLine());
                    continue;
                }
                if (cmd.equals("s")) {
                    System.out.println("请设置索引时间间隔(小时)：");
                    m.setRefreshDelay((long) (Float.parseFloat(buf.readLine()) * 1000) * 3600);
                    System.out.println("请设置入口地址1");
                    String URL = buf.readLine();
                    System.out.println("請設置索引延迟（小时）：");
                    m.startWith(URL, (long) (Float.parseFloat(buf.readLine()) * 1000) * 3600);
                    continue;
                }
                if (cmd.equals("sa")) {
                    m.stopAnas();
                    continue;
                }
                if (cmd.equals("ss")) {
                    m.startAnas();
                    continue;
                }
                if (cmd.equals("r")) {
                    System.out.println("入口地址：");
                    m.regURL(buf.readLine());
                    continue;
                }
                if (cmd.equals("ref")) {
                    try {
                        String[] res = m.getRank().getRefs(buf.readLine());
                        for (String ref : res) {
                            System.out.println(ref);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    continue;
                }
                if (cmd.equals("rank")) {
                    System.out.println(m.getRank().getRank(buf.readLine()));
                    continue;
                }
                System.out.println("不认识这个命令");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

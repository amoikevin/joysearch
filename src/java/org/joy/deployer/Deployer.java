/*
 * Main.java
 *
 * Created on 2007年5月6日, 上午10:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.deployer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * All the services run in Joysearch is called "module"
 * All the modules need some os-level support from local machine
 * Deployer help all the projects in Joysearch configure some basic(foundamental) settings, e.g. codebase, local rmiregistry
 * @author AC
 */
public abstract class Deployer {

    /**
     * The name of the project to be deployed
     */
    private String moduleName;
    /**
     * The location of ur log files.
     */
    protected static final String LOG_PATH = "logs/";
    /**
     * The location of ur configuration files.
     */
    protected static final String CONF_PATH = "conf/";
    /**
     * Keyboard inputstream reader
     */
    protected InputStreamReader reader;
    /**
     * keyboard reader
     */
    protected BufferedReader buf;
    /**
     * local machine's IPAdress
     */
    protected String IPAddress;
    /**
     * properties of this projected
     */
    protected Properties properties = new Properties();
    /**
     * local rmi registry
     */
    protected Registry registry;
    /**
     * current project's logger
     */
    public static Logger logger;

    /**
     * 初始化远程调用注册表
     */
    private void initRegistry() {
        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException ex) {
            try {
                registry = LocateRegistry.getRegistry();
            } catch (RemoteException ex2) {
                logger.info(ex2.getMessage());
            }
        }
    }

    /**
     * 配置Logger
     * @throws java.net.UnknownHostException
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    private void initLogger() throws UnknownHostException, FileNotFoundException, IOException {
//        System.setOut(new PrintStream("logs/" + moduleName + ".log"));
        System.setErr(System.out);
        IPAddress = Inet4Address.getLocalHost().getHostAddress();
        loadProperties(moduleName);
        //删除已经存在的Log文件
        (new File(LOG_PATH + moduleName + ".log")).delete();

        logger = Logger.getLogger(moduleName);
        File logPath = new File(LOG_PATH);
        if (!logPath.exists()) {
            logPath.mkdir();
        }

        logger.addAppender(new FileAppender(new SimpleLayout(), LOG_PATH + moduleName + ".log"));
        logger.addAppender(new ConsoleAppender(new SimpleLayout()));
    }

    /**
     * deploy this module
     * @param moduleName module name
     */
    public Deployer(String moduleName) {
        this.moduleName = moduleName;
        try {
            initLogger();
            initRegistry();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }
        //控制台输入控制类
        reader = new InputStreamReader(System.in);
        buf = new BufferedReader(reader);
        System.setProperty("java.rmi.server.codebase", "http://" + IPAddress + ":8080/joysearch/joysearch.jar");
        System.setProperty("java.security.policy", CONF_PATH + "policy.txt");
        System.setSecurityManager(new RMISecurityManager());

        //注册结束时的Hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                cleanup();
            }
        }));
    }

     /**
     * load properties from propertie file
     * @param moduleName current project name
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    private void loadProperties(String moduleName) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(CONF_PATH + moduleName + ".txt");
        properties.load(fis);
    }

    /**
     * After finish its starting, send an singal to starter process
     */
    protected void done() {
        try {
            File tempFile;
            tempFile = new File("/", moduleName + ".done");
            tempFile.createNewFile();
            tempFile.deleteOnExit();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Deployer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private String lastAddress = "localhost";

    /**
     * find the service, otherwise, provide a input dialogue for service path.
     */
    protected Remote lookUp(String serviceName) throws RemoteException {
        try {
            if (properties.getProperty(serviceName) == null) {
                throw new NullPointerException();
            }
            Remote obj = Naming.lookup("rmi://" + properties.getProperty(serviceName) + "/" + serviceName);
            lastAddress = properties.getProperty(serviceName);
            return obj;
        } catch (Exception ex) {
            //try last configed server
            try {
                return Naming.lookup("rmi://" + lastAddress + "/" + serviceName);
            } catch (Exception ex3) {
                while (true) {
                    String address = JOptionPane.showInputDialog("请输入包含" + serviceName + "的服务器");
                    if (address == null) {
                        System.exit(0);
                    }
                    try {
                        Remote obj = Naming.lookup("rmi://" + address + "/" + serviceName);
                        lastAddress = address;
                        return obj;
                    } catch (Exception ex2) {
                    }
                }
            }
        }
    }

    /**
     * Helper for registering a rmiService
     */
    protected void register(UnicastRemoteObject serverImp) throws RemoteException {
        try {
            registry.rebind(moduleName, serverImp);
        } catch (Exception ex) {
            throw new RemoteException(ex.toString());
        }
    }

    /**
     * Helper for registering a rmiService
     */
    protected void register(UnicastRemoteObject serverImp, String serviceName) throws RemoteException {
        try {
            registry.rebind(serviceName, serverImp);
        } catch (Exception ex) {
            throw new RemoteException(ex.toString());
        }
    }

    /**
     * run project's init configuration
     */
    public abstract void run();

    /**
     * release project's resource
     */
    public abstract void cleanup();
}

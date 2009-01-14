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
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
 * All the services run in Joysearch is called "project"
 * All the projects need some os-level support from local machine
 * Deployer help all the projects in Joysearch configure some basic(foundamental) settings, e.g. IIS, local rmiregistry
 * @author AC
 */
public abstract class Deployer {

    /**
     * The name of the project to be deployed
     */
    private String projName;
    /**
     * The location of the project's Jar file
     */
    protected static final String JAR_PATH = "C:/JoyDK/Java/bin/";
    /**
     * IIS location
     */
    protected static final String WEBAPP_PATH = "C:/JoyDK/Java/bin/Tomcat/webapps/ROOT/";
    /**
     * The location of ur log files.
     */
    protected static final String LOG_PATH = "Log/";
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
     * init local rmi registry
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

    private void initLogger() throws UnknownHostException, FileNotFoundException, IOException {
        System.setOut(new PrintStream("C:/JoyDK/ScreenLog/" + projName + ".log"));
        System.setErr(System.out);
        IPAddress = Inet4Address.getLocalHost().getHostAddress();
        loadProperties(projName);

        (new File(LOG_PATH + projName + "_log" + ".txt")).delete();

        logger = Logger.getLogger(LOG_PATH + projName + "_log" + ".txt");
        File logPath = new File(LOG_PATH);
        if (!logPath.exists()) {
            logPath.mkdir();
        }

        logger.addAppender(new FileAppender(new SimpleLayout(), LOG_PATH + projName + "_log" + ".txt"));
        logger.addAppender(new ConsoleAppender(new SimpleLayout()));
    //启动远程注册表
    }

    /**
     * deploy this project
     * @param projName project name
     */
    public Deployer(String projName) {
        this.projName = projName;
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
       // deployIIS();
        System.setProperty("java.rmi.server.codebase", "http://" + IPAddress + ":8080/joysearch.jar");
        System.setProperty("java.security.policy", "policy.txt");
        System.setSecurityManager(new RMISecurityManager());

        //注册结束时的Hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                cleanup();
            }
        }));
    }

    /**
     * deploy this project
     * @param projName project name
     */
    public Deployer(String projName, String jarFile) {
        this.projName = projName;
        try {
            initLogger();
            //启动远程注册表
            initRegistry();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }

        reader = new InputStreamReader(System.in);
        buf = new BufferedReader(reader);
        //deployWebApp(new File(jarFile));
        System.setProperty("java.rmi.server.codebase", "http://" + IPAddress + ":8080/joysearch.jar");
        System.setProperty("java.security.policy", "policy.txt");
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
     * @param projName current project name
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    private void loadProperties(String projName) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(projName + ".txt");
        properties.load(fis);
    }

    private void copyFile(File in, File out) throws IOException {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1) {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }

    /**
     * deploy IIS
     * @param projectName current project name
     */
    private void deployIIS() {
        logger.info("正在配置IIS...");
        File folder = new File(WEBAPP_PATH + projName + "\\");
        folder.mkdir();
        File dest = new File(WEBAPP_PATH + projName + "\\" + projName + ".jar");
        File jar = new File(JAR_PATH + projName + ".jar");
        if (dest.length() != jar.length()) {
            logger.info("需要配置IIS");
            try {
                copyFile(jar, dest);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * deploy IIS
     * @param projectName current project name
     */
    private void deployWebApp(File jar) {
        logger.info("正在配置IIS...");
        File folder = new File(WEBAPP_PATH + projName + "\\");
        folder.mkdir();
        File dest = new File(WEBAPP_PATH + projName + "\\" + projName + ".jar");
        if (dest.length() != jar.length()) {
            logger.info("需要配置IIS");
            try {
                copyFile(jar, dest);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * After finish its starting, send an singal to starter process
     */
    protected void done() {
        try {
            File tempFile;
            tempFile = new File("c:\\", projName + ".done");
            tempFile.createNewFile();
            tempFile.deleteOnExit();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Deployer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private String lastAddress = "localhost";

    /**
     * validate the address of rmi service
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
            registry.rebind(projName, serverImp);
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.starter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lamfeeling
 */
public class Runner {

    static ProcessBuilder pb;
    static Process p;
    static File f;

    static boolean isRunning(String proj) {
        File f = new File("c:\\" + proj + ".done");
        return f.exists();
    }

    static Process run(String moduleName, int maxMem, List<String> args) throws RunnerException, AlreadyRunException {
        try {
            if (moduleName.equals("Venus") || moduleName.equals("Spartan")) {
                File f = new File("c:\\" + moduleName + ".done");
                if (f.exists()) {
                    f.delete();
                }
            }

            if (isRunning(moduleName)) {
                throw new AlreadyRunException();
            }
            ArrayList<String> commands = new ArrayList<String>();
            commands.add("c:/joydk/java/bin/jre/bin/java.exe");
            commands.add("-Xmx" + maxMem + "m");
            commands.add("-jar");
            commands.add("C:\\JoyDK\\Java\\bin\\" + moduleName + ".jar");
            commands.addAll(args);

            pb = new ProcessBuilder(commands);
            pb.directory(new File("C:\\JoyDK\\Java\\bin\\"));
            p = pb.start();
            f = new File("c:\\" + moduleName + ".done");

            int count = 0;
            while (!f.exists()) {
                if (count > 100) {
                    throw new RunnerException();
                }
                Thread.sleep(100);
                count++;
            }
            System.out.println(moduleName + "OK");
            return p;
        } catch (IOException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
        }
        return null;
    }

    static Process run(String moduleName) throws RunnerException, AlreadyRunException {
        try {
            Properties pro = new Properties();
            pro.load(new FileInputStream("c:\\joydk\\java\\bin\\" + moduleName + ".txt"));
            if (pro.get("MAX_MEM") == null) {
                return run(moduleName, 32, new ArrayList<String>());
            }
            return run(moduleName, Integer.parseInt(pro.getProperty("MAX_MEM")), new ArrayList<String>());
        } catch (Exception ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    static Process run(String moduleName, int maxMem) throws RunnerException, AlreadyRunException {
        return run(moduleName, maxMem, new Stack<String>());
    }

    static Process run(String moduleName, List<String> args) throws RunnerException, AlreadyRunException {
        try {
            Properties pro = new Properties();
            pro.load(new FileInputStream("c:\\joydk\\java\\bin\\" + moduleName + ".txt"));
            if (pro.get("MAX_MEM") == null) {
                return run(moduleName, 32, new ArrayList<String>());
            }
            return run(moduleName, Integer.parseInt(pro.getProperty("MAX_MEM")), args);
        } catch (Exception ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    static void runConsole() {
        try {
            pb = new ProcessBuilder("C:\\JoyDK\\Java\\bin\\Console.bat");
            p = pb.start();
        } catch (IOException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
    //runConsole();
    }
}

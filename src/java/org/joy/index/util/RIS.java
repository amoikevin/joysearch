/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.joy.index.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.index.db.RIStatistics;
import org.joy.index.service.ServerImp;

/**
 *
 * @author Lamfeeling
 */
public class RIS {
    public static void save(RIStatistics stat){
        try {
            FileOutputStream fos = new FileOutputStream("stat.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(stat);
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static RIStatistics load(){
        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream("stat.txt");
            ois = new ObjectInputStream(fis);
            RIStatistics newStat = (RIStatistics) ois.readObject();
            ois.close();
            return newStat;
        } catch (Exception ex) {
            return new RIStatistics(0, new Hashtable<String, Integer>());
        } 
    }
}

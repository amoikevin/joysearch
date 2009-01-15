/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.group.core.Job;
import org.joy.spider.core.DownloadJob;

/**
 *
 * @author Lamfeeling
 */
public class DocumentJob extends Job {

    private String URL;
    private String text;
    private String type;
    private int level;

    public DocumentJob(String URL, String HTML, String type, int level) {
        this.URL = URL;
        this.text = HTML;
        this.type = type;
        this.level = level;
    }

    public DocumentJob(DownloadJob job, String HTML, String type) {
        this.URL = job.getURL();
        this.level = job.getLevel();
        this.text = HTML;
        this.type = type;
    }

    public DocumentJob(byte[] b) {
        DataInputStream ois = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            ois = new DataInputStream(bais);
            URL = ois.readUTF();
            int length = ois.readInt();
            
            char[] c = new char[length];
            for (int i = 0; i < length; i++) {
                c[i] = ois.readChar();
            }
            
            text = new String(c);
            type = ois.readUTF();
            level = ois.readInt();
        } catch (IOException ex) {
            Logger.getLogger(DocumentJob.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(DocumentJob.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public static void main(String []args){
        DocumentJob j = new DocumentJob("ab", "cde", "def", 1);
        System.out.println((new DocumentJob(j.toBytes())).getURL());
    }
    public String getText() {
        return text;
    }

    public String getURL() {
        return URL;
    }

    public int getLevel() {
        return level;
    }

    public byte[] toBytes() {
        DataOutputStream oos = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new DataOutputStream(baos);
            oos.writeUTF(URL);
            oos.writeInt(text.length());
            oos.writeChars(text);
            oos.writeUTF(type);
            oos.writeInt(level);
            return baos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(DocumentJob.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(DocumentJob.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

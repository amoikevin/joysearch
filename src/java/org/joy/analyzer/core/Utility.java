/*
 * Utility.java
 *
 * Created on 2007年10月21日, 下午12:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.analyzer.core;

import java.net.URL;

/**
 *
 * @author Administrator
 */
public class Utility {

    public static boolean isValidLink(String link) {
        if (link.trim().equals("")) {
            return false;
        }
        if (link.length() <= 0) {
            return false;
        }

        if (link.indexOf('#') != -1) {
            return false;
        }

        if (link.indexOf("mailto:") != -1) {
            return false;
        }

        if (link.indexOf("../") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf("javascript") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".mpg") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".mp3") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".pdf") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".doc") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".jpg") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".wma") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".wmv") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".exe") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".zip") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".rar") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".mpeg") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".bmp") != -1) {
            return false;
        }

        if (link.toLowerCase().indexOf(".xls") != -1) {
            return false;
        }

        if ((link.toLowerCase().indexOf("sitemap") != -1) && (link.toLowerCase().indexOf("book") != -1)) {
            return false;
        }

        return true;
    }

    public static boolean checkHost(URL url, String host) {
        return url.getHost().equals(host);
    }
    
}


//~ Formatted by Jindent --- http://www.jindent.com


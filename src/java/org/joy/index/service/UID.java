/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.joy.index.service;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * URL ID Generator
 * @author Lamfeeling
 */
public class UID{
    public static long from(String URL){
        try {
            return UUID.nameUUIDFromBytes(URL.getBytes("utf-8")).getLeastSignificantBits();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UID.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
}

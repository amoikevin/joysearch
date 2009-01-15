/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.joy.query.core;

import java.io.Serializable;
/**
 *
 * @author Lamfeeling
 */
public abstract class Filter implements Serializable{
    public abstract boolean filter(String URL);
}

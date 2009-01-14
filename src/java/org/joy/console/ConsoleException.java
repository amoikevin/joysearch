/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.joy.console;


/**
 * 當控制臺錯誤的時候被拋出。
 * @author Lamfeeling
 */
public class ConsoleException extends Exception{
    
    public ConsoleException(Throwable cause) {
        super(cause);
    }

    public ConsoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsoleException(String message) {
        super(message);
    }

    public ConsoleException() {
    }

}

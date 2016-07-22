/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.Serializable;

/**
 *
 * @author timi
 */
public class SerialSample implements Serializable{
    
    String inf;
    
    public SerialSample(String inf) {
        this.inf = inf;
    }
    
    public String shout() {
        return "HEEEEEEEYY";
    }
    
    @Override
    public String toString() {
        return "Info is: " + inf;
    }
}

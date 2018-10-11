/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

/**
 *
 * @author diivanov
 */
public interface GraphHeapInterface {

    

    /**
     * Method to add key and value.
     *
     * @param value
     * @param key
     */
    public void add(int value, int key);

    /**
     * Method to pop value of element with minimum key.
     *
     * @return (int) value.
     */
    public int getValueOfMinKeyElement();
}

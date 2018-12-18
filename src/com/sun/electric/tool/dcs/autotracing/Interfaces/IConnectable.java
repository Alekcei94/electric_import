/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing.Interfaces;

import java.util.List;

/**
 *
 * @author diivanov
 */
public interface IConnectable {
    
    public void deleteKeyFromGraph(String key);
    
    public String getName();
    
    public int getWeight(String elemFrom, String elemTo);
    
    public List<String> getConfigurationPath(String elemFrom, String elemTo, boolean doDelete);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Pair;
import java.util.List;

/**
 *
 * @author diivanov
 */
public interface ConnectionGraphInterface {
    
    public List<Pair<String, String>> deleteKeyFromCBGraph(String key);
    
    public String getLabel();
    
    public int getWeight(String elemFrom, String elemTo);
    
    public void getConfigurationPath(String elemFrom, String elemTo);
    
    public List<Pair<String, String>> doDeleteUsedVerts();
}

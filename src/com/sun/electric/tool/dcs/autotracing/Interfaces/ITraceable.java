/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing.Interfaces;

import java.util.List;

/**
 * Interface for global graphs.
 */
public interface ITraceable {
    
    public List<String> getConfigurationPath(String vertexFrom, String pattern,
            boolean doDelete);
    
    public String getName();
}

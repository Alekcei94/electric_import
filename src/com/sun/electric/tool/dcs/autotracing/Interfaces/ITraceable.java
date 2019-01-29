/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing.Interfaces;

import com.sun.electric.tool.dcs.Data.BlockMap;
import com.sun.electric.tool.dcs.Exceptions.NoPathFoundException;
import java.util.List;

/**
 * Interface for global graphs.
 */
public interface ITraceable {
    
    public List<String> getConfigurationPath(BlockMap.BlockPattern vertexFrom,
            BlockMap.BlockPattern pattern, boolean doDelete) throws NoPathFoundException;
    
    public String getName();
}

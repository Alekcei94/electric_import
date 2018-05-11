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
public interface ConnectionGraphFactory {
    
    public ConnectionGraphInterface createConnectionGraph(String graphName, String[] globVerts, int VERTEX_MAX);
    
    public ConnectionGraphInterface createConnectionGraphCBLarge(String graphName);
}

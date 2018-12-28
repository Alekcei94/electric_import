/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing.Interfaces;

import java.util.List;

/**
 *
 */
public interface IConnectable {

    /**
     * Interface to delete one of verteces from graph.
     *
     * @param key
     */
    public void deleteKeyFromGraph(String key);

    /**
     * Interface to get the main name of graph.
     *
     * @return
     */
    public String getName();

    /**
     * Interface to get distance or weight between verteces with names elemFrom
     * and elemTo.
     *
     * @param elemFrom
     * @param elemTo
     * @return
     */
    public int getWeight(String elemFrom, String elemTo);

    /**
     * Interface to get configuration sequence for path between verteces with
     * names elemFrom and elemTo. Delete path if doDelete parameter.
     *
     * @param elemFrom
     * @param elemTo
     * @param doDelete
     * @return
     */
    public List<String> getConfigurationPath(String elemFrom, String elemTo, boolean doDelete);
}

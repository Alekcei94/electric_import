/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.Scripts;

import com.sun.electric.database.hierarchy.Cell;

/**
 * Interface to consolidate the logic of exports. Contract: ConfigExport classes
 * must return String with list of keys.
 */
public interface Exportable {

    /**
     * Method to get config as StringBuilder
     *
     * @param mainCell
     * @return
     */
    public StringBuilder getConfigExport(Cell mainCell);
    
    public StringBuilder getExistingConfigExport();

}

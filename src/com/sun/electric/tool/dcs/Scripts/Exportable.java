/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.Scripts;

/**
 * Interface to consolidate the logic of exports. Contract: ConfigExport classes
 * must return String with list of keys.
 */
public interface Exportable {

    public StringBuilder getConfigExport();

}

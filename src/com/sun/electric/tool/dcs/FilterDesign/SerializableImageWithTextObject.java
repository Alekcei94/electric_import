/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.FilterDesign;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.accessibility.Accessible;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author diivanov
 */
public class SerializableImageWithTextObject extends ImageIcon implements Icon, Serializable, Accessible {

    private Map<String, String> infoMap = new HashMap<>();
    private List<String> configList = new ArrayList<>();

    /**
     * Constructor to combine image with two data lists.
     * @param str
     * @param infoMap
     * @param configList
     */
    public SerializableImageWithTextObject(String str, Map<String, String> infoMap, List<String> configList) {
        super(str);
        this.infoMap = infoMap;
        this.configList = configList;
    }

    /**
     * Typical constructor is used only in more powerful ones.
     * @param str
     */
    private SerializableImageWithTextObject(String str) {
        super(str);
    }
    /**
     * Info is held in map: (filterType -> Elliptic, order -> 6, ....)
     * @return 
     */
    public Map<String, String> getInfo() {
        return infoMap;
    }
    /**
     * Config consists of configuration strings: (15062, 30607 ...).
     * @return 
     */
    public List<String> getConfig() {
        return configList;
    }
}

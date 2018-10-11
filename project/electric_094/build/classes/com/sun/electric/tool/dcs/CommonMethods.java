/* Electric(tm) VLSI Design System
 *
 * File: CommonMethods.java
 *
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
 *
 * Electric(tm) is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Electric(tm) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sun.electric.tool.dcs;

import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.Connection;
import com.sun.electric.database.topology.PortInst;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Class is here to implement some common methods using in autotracing proccess
 * like parsing (Node/Port)Inst into a strings with information about their base
 * name and address or getting all ports that are connected to given point.
 * 
 * @author diivanov
 */
public class CommonMethods {
    
    private CommonMethods() {
        throw new AssertionError();
    }
    
    /**
     * Method to get all connected ports using one PortInst (all except ports that were used already).
     *
     * @param pi
     * @param usedPortList
     * @return
     */
    public static PortInst[] getNearByPortInsts(PortInst pi, HashSet<String> usedPortList) {
        PortInst[] a = new PortInst[0];
        ArrayList<PortInst> portList = new ArrayList<>();
        Iterator<Connection> itr = pi.getConnections();
        
        if (!itr.hasNext()) {
            return a;
        }
        Connection cntn = itr.next();
        ArcInst ai = cntn.getArc();
        if ((!ai.getPortInst(0).toString().contains("Wire_Pin")) && (!ai.getPortInst(1).toString().contains("Wire_Pin"))) {
            if (ai.getPortInst(0).toString().equals(pi.toString())) {
                portList.add(ai.getPortInst(1));
            } else {
                portList.add(ai.getPortInst(0));
            }
            a = portList.toArray(a);
            return a;
        }
        if (ai.getPortInst(0).toString().equals(pi.toString())) {
            if (!ai.getPortInst(1).toString().contains("Wire_Pin")) {
                portList.add(ai.getPortInst(1));
                a = portList.toArray(a);
                return a;
            }
            findNext(ai, ai.getPortInst(1), portList, usedPortList);
        } else {
            if (!ai.getPortInst(0).toString().contains("Wire_Pin")) {
                portList.add(ai.getPortInst(1));
                a = portList.toArray(a);
                return a;
            }
            findNext(ai, ai.getPortInst(0), portList, usedPortList);
        }

        a = portList.toArray(a);
        return a;
    }

    /**
     * Internal method to implement getNearByPortInsts method.
     */
    private static void findNext(ArcInst ais, PortInst pis, ArrayList<PortInst> portList, HashSet<String> usedPortList) {
        Iterator<Connection> itr = pis.getConnections();
        while (itr.hasNext()) {
            int head = 0;
            ArcInst ai = itr.next().getArc();
            if (ai.toString().equals(ais.toString())) {
                continue;
            }
            PortInst thisPort = ai.getPortInst(0);
            PortInst thisPortNot = ai.getPortInst(1);
            if (pis.toString().equals(thisPort.toString())) {
                head = 1;
            }

            if (head == 1) {
                if (!thisPortNot.toString().contains("Wire_Pin")) {
                    // why not string here?
                    if (!usedPortList.contains(thisPortNot)) {
                        portList.add(thisPortNot);
                    }

                } else {
                    findNext(ai, thisPortNot, portList, usedPortList);
                }
            } else {
                if (!thisPort.toString().contains("Wire_Pin")) {
                    // why not string here?
                    if (!usedPortList.contains(thisPort)) {
                        portList.add(thisPort);
                    }
                } else {
                    findNext(ai, thisPort, portList, usedPortList);
                }
            }
        }
    }
    

    /**
     * method implemets parsing of Port String to get Block Name.
     *
     * @param port
     * @return
     */
    public static String parsePortToBlock(String port) {
        assert port != null;
        return port.substring(port.indexOf(":")+1, port.indexOf("{"));
        // port '5400TP035:ION{ic}[ION<1].ION'
    }
 

    /**
     * method implemets parsing of Port String to get Port Name.
     *
     * @param port
     * @return
     */
    public static String parsePortToPort(String port) {
        assert port != null;
        return port.substring(port.indexOf(".") + 1, port.lastIndexOf("'")); // name smth like CB<7454
        // port '5400TP035:ION{ic}[ION<1].ION'
    }
}

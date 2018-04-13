/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.Connection;
import com.sun.electric.database.topology.PortInst;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
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
                    if (!usedPortList.contains(thisPortNot)) {
                        portList.add(thisPortNot);
                    }

                } else {
                    findNext(ai, thisPortNot, portList, usedPortList);
                }
            } else {
                if (!thisPort.toString().contains("Wire_Pin")) {
                    if (!usedPortList.contains(thisPort)) {
                        portList.add(thisPort);
                    }
                } else {
                    findNext(ai, thisPort, portList, usedPortList);
                }
            }
        }
    }
}

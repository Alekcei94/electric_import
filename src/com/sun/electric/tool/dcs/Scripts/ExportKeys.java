/* Electric(tm) VLSI Design System
 *
 * File: ExportKeys.java
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
package com.sun.electric.tool.dcs.Scripts;

import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.hierarchy.Export;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.Connection;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.variable.ElectricObject;
import com.sun.electric.database.variable.Variable;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.dcs.CommonMethods;
import com.sun.electric.tool.dcs.FunctionalException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author diivanov
 */
public class ExportKeys {

    private static ExportKeys exportKeys;

    /*
    * Instantination of exportKeys singleton.
     */
    public static ExportKeys getInstance() {
        if (exportKeys == null) {
            exportKeys = new ExportKeys();
        }
        return exportKeys;
    }

    /*
    * Method to create configuration file (software -> programming -> FPAA)
     */
    public void formConfigFromScheme() throws FunctionalException {
        Cell curcell = Job.getUserInterface().getCurrentCell();
        Iterator<NodeInst> niItr = curcell.getNodes();
        while (niItr.hasNext()) {
            NodeInst ni = niItr.next();
            getConfigFromOneNodeInst(ni);
        }
    }

    private ArrayList<String> getConfigFromOneNodeInst(NodeInst ni) throws FunctionalException {
        String parameterOfBlock = getOnlyParamOfNodeInst(ni);

        ArrayList<String> partConfigList = new ArrayList<>();

        Cell equiv = ni.getProtoEquivalent();
        Iterator<NodeInst> niItr = equiv.getNodes();
        while (niItr.hasNext()) {
            NodeInst key = niItr.next();
            if (key.getProto().getName().equals("key")) {
                if (isClosedKey(key)) {
                    partConfigList.add(getConfigForKey(key, parameterOfBlock));
                }
            }
        }
        return partConfigList;
    }

    /*
    * Method to show if the key is closed or not.
    * @Param key SHOULD BE ONLY KEY, ONLY WITH PORTS X,Y,M1,M2.
    */
    private boolean isClosedKey(NodeInst key) throws FunctionalException {
        Iterator<PortInst> itrPorts = key.getPortInsts();
        while (itrPorts.hasNext()) {
            PortInst pi = itrPorts.next();
            String port = CommonMethods.parsePortToPort(pi.toString());
            if ((port.equals("M1") || (port.equals("M2")))) {
                Iterator<Connection> ctnItr = pi.getConnections();
                Connection ctn = getOnlyIteratorObject(ctnItr);
                ArcInst ai = ctn.getArc();
                
                Connection ctnTail = ai.getConnection(0);
                Connection ctnHead = ai.getConnection(1);
                Connection ctnNext;
                if(ctn.toString().equals(ctnTail.toString())) {
                    ctnNext = ctnHead;
                } else {
                    ctnNext = ctnTail;
                }
                
                PortInst outPort = ctnNext.getPortInst();
                Export outExport = getOnlyIteratorObject(outPort.getExports());
                PortInst outsidePort = outExport.getOriginalPort();
                
                if(!outsidePort.hasConnections()) {
                    return false;
                }
                Connection outsideCtn = getOnlyIteratorObject(outsidePort.getConnections());
                
                ArcInst outsideArc = outsideCtn.getArc();
                
                ctnTail = outsideArc.getConnection(0);
                ctnHead = outsideArc.getConnection(1);
                if(outsideCtn.toString().equals(ctnTail.toString())) {
                    ctnNext = ctnHead;
                } else {
                    ctnNext = ctnTail;
                }
                PortInst secondPort = ctnNext.getPortInst();
                if(CommonMethods.parsePortToPort(secondPort.toString()).contains("mAd")) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getConfigForKey(NodeInst key, String parameterOfBlock) throws FunctionalException {
        return parameterOfBlock + getOnlyParamOfNodeInst(key);
    }

    private String getOnlyParamOfNodeInst(NodeInst ni) throws FunctionalException {
        ArrayList<String> paramList = new ArrayList<>();
        Iterator<Variable> varItr = ni.getParameters();
        while (varItr.hasNext()) {
            Variable var = varItr.next();
            paramList.add((String) var.getObject());
        }
        if (paramList.size() != 1) {
            throw new FunctionalException("There shouldn't be more than one parameters for global blocks");
        }
        return paramList.get(0);
    }
    
    private <A, B extends Iterator<A>> A getOnlyIteratorObject(B iterator) throws FunctionalException {
        ArrayList<A> objectsList = new ArrayList<>();
        while(iterator.hasNext()) {
            objectsList.add(iterator.next());
        }
        if(objectsList.size() != 1) {
            throw new FunctionalException("More than one object in iterator");
        }
        return objectsList.get(0);
    }
}

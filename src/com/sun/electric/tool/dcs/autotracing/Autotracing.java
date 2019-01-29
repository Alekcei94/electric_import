/* Electric(tm) VLSI Design System
 *
 * File: Autotracing.java
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
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.network.Netlist;
import com.sun.electric.database.network.Network;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.Data.Constants;
import com.sun.electric.tool.dcs.Exceptions.FunctionalException;
import com.sun.electric.tool.dcs.SpecificStructures.Pair;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Class to implement autotracing mechanism.
 */
public class Autotracing {

    /**
     * Possible instantiation.
     */
    public Autotracing() {

    }

    public void testStructure() {
        AutotracingStructure struct = new AutotracingStructure();
        try {
            Queue<Pair<PortInst, PortInst>> queue = struct.createQueue();
            for (Pair<PortInst, PortInst> pair : queue) {
                System.out.println("first: " + pair.getFirstObject().toString());
                System.out.println("second: " + pair.getSecondObject().toString());
            }
        } catch(FunctionalException fe) {
            Accessory.showMessage(fe.getMessage());
            return;
        }

    }

    private class AutotracingStructure {

        private final Cell cell = Job.getUserInterface().getCurrentCell();
        private final Netlist netlist = cell.getNetlist();

        private final List<NodeInst> niList = new ArrayList<>();
        // map that matches list of connected ports to the current port.toString()
        private final Map<String, List<PortInst>> connectionMap = new HashMap<>();

        private AutotracingStructure() {
            init();
        }

        /**
         * Method to create queue with all traces in current cell.
         * @return
         * @throws FunctionalException 
         */
        private Queue<Pair<PortInst, PortInst>> createQueue() throws FunctionalException {
            Queue<Pair<PortInst, PortInst>> queue = new ArrayDeque();
            Set<NodeInst> usedNodeInstSet = new HashSet<>();
            NodeInst startingNode = getStartingNodeInst(niList);
            
            Queue<NodeInst> nodeQueue = new ArrayDeque<>();
            
            usedNodeInstSet.add(startingNode);
            nodeQueue.add(startingNode);
            NodeInst current;
            while ((current = nodeQueue.poll()) != null) {
                List<PortInst> ports = getPortsOfNodeInst(current);
                for (PortInst port : ports) {
                    List<PortInst> connectedPorts = getClosestPortInsts(port);
                    for (PortInst conPort : connectedPorts) {
                        if (!usedNodeInstSet.contains(conPort.getNodeInst())) {
                            queue.add(new Pair<>(port, conPort));
                            nodeQueue.add(conPort.getNodeInst());
                            usedNodeInstSet.add(conPort.getNodeInst());
                        }
                    }
                }
            }
            return queue;
        }

        /**
         * Initialize general structures.
         */
        private void init() {
            // fill niList
            Iterator<NodeInst> niItr = cell.getNodes();
            while (niItr.hasNext()) {
                niList.add(niItr.next());
            }
            // fill connectionMap
            for (NodeInst ni : niList) {
                Iterator<PortInst> piItr = ni.getPortInsts();
                while (piItr.hasNext()) {
                    PortInst pi = piItr.next();
                    connectionMap.put(pi.toString(), getClosestPortInsts(pi));
                }
            }
        }

        /**
         * Method to get initial nodeInst, we can choose any of them but
         * it's more preferable to start
         * @param nodeList
         * @return
         * @throws FunctionalException 
         */
        private NodeInst getStartingNodeInst(List<NodeInst> nodeList) throws FunctionalException {
            String[] possibleStartingNodeInsts = Constants.getPossibleStartingNodeInsts();
            for (NodeInst ni : nodeList) {
                for (String startingNode : possibleStartingNodeInsts) {
                    System.out.println(ni.getName());
                    if (ni.toString().contains(startingNode)) {
                        return ni;
                    }
                }
            }
            throw new FunctionalException("There is no in/out pin in this scheme");
        }

        /**
         * Get ports of given nodeInst.
         *
         * @param node
         * @return
         */
        private List<PortInst> getPortsOfNodeInst(NodeInst node) {
            List<PortInst> portList = new ArrayList<>();
            Iterator<PortInst> itr = node.getPortInsts();
            while (itr.hasNext()) {
                portList.add(itr.next());
            }
            return portList;
        }

        /**
         * Method to get all other portInsts of same network.
         *
         * @param pi
         * @return
         */
        private List<PortInst> getClosestPortInsts(PortInst pi) {
            Network network = netlist.getNetwork(pi);
            if(network == null) {
                return new ArrayList<>();
            }
            List<PortInst> portList = network.getPortsList();
            
            Iterator<PortInst> portItr = portList.iterator();
            while(portItr.hasNext()) {
                PortInst port = portItr.next();
                for(String str : Constants.getAvailableInvisibleNodeInstsInScheme()) {
                    if(port.toString().contains(str)) {
                        portItr.remove();
                    }
                }
            }
            assert portList != null; // not possible?
            portList.remove(pi);
            return portList;
        }
    }
}

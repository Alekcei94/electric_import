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
import com.sun.electric.tool.dcs.CommonMethods;
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

    public void invokeAutotracing() {
        AutotracingStructure struct = new AutotracingStructure();
        try {
            Queue<Pair<PortInst, PortInst>> queue = struct.createQueue();
            for (Pair<PortInst, PortInst> pair : queue) {
                System.out.println("first: " + pair.getFirstObject().toString());
                System.out.println("second: " + pair.getSecondObject().toString());
            }
        } catch (FunctionalException fe) {
            Accessory.showMessage(fe.getMessage());
            return;
        }

    }

    private class AutotracingStructure {

        private final Cell cell = Job.getUserInterface().getCurrentCell();
        private final Netlist netlist = cell.getNetlist();

        private final List<NodeInst> inputList = new ArrayList<>();
        private final List<NodeInst> outputList = new ArrayList<>();
        private final List<NodeInst> clockList = new ArrayList<>();
        private final List<NodeInst> blockList = new ArrayList<>();

        // map that matches list of connected ports to the current port.toString()
        private final Map<String, List<PortInst>> connectionMap = new HashMap<>();

        private AutotracingStructure() {
            init();
        }
        
        /**
         * Method to create queue with all traces in current cell.
         *
         * @return
         * @throws FunctionalException
         */
        private Queue<Pair<PortInst, PortInst>> createQueue() throws FunctionalException {
            Queue<Pair<PortInst, PortInst>> queue = new ArrayDeque();
            Set<NodeInst> usedNodeInstSet = new HashSet<>();
            NodeInst startingNode = getStartingNodeInst(blockList);

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
                            usedNodeInstSet.add(conPort.getNodeInst());
                            queue.add(new Pair<>(port, conPort));
                            nodeQueue.add(conPort.getNodeInst());
                        }
                    }
                }
            }
            return queue;
        }

        /**
         * Method to fill .tcl file using this autotracing structure.
         *
         * @param file
         */
        private StringBuilder getTclStructure() {
            //set input
            StringBuilder str = new StringBuilder("set xc_input { ");
            for (NodeInst block : inputList) {
                str.append(block.getName()).append(" ");
            }
            str.append("}\n\n");
            //set output
            str = new StringBuilder("set xc_output { ");
            for (NodeInst block : outputList) {
                str.append(block.getName()).append(" ");
            }
            str.append("}\n\n");
            //set clock
            str = new StringBuilder("set xc_clock { ");
            for (NodeInst block : clockList) {
                str.append(block.getName()).append(" ");
            }
            str.append("}\n\n");
            //#INPUT IOs
            for(NodeInst block : inputList) {
                str.append("xc_inst ").append(block.getName()).append(" xa_ib ")
                        .append(" {");
                str.append(block.getName()).append(" ");
                for(PortInst port : getPortsOfNodeInst(block)) {
                    str.append(netlist.getNetwork(port).getName()).append(" ");
                }
                str.append("} {inp_io=1}\n");
            }
            str.append("\n");
            //#OUTPUT IOs
            for(NodeInst block : outputList) {
                str.append("xc_inst ").append(block.getName()).append(" xa_ob ")
                        .append(" {");
                for(PortInst port : getPortsOfNodeInst(block)) {
                    str.append(netlist.getNetwork(port).getName()).append(" ");
                }
                str.append(block.getName());
                str.append("} {out_io=1}\n");
            }
            str.append("\n");
            //#CLOCK IOs
            for(NodeInst block : outputList) {
                str.append("xc_inst ").append(block.getName()).append(" xa_ob ")
                        .append(" {");
                str.append(block.getName()).append(" ");
                for(PortInst port : getPortsOfNodeInst(block)) {
                    str.append(netlist.getNetwork(port).getName()).append(" ");
                }
                str.append("} {out_io=1}\n");
            }
            str.append("\n");
            //#LUTs
            for(NodeInst block : blockList) {
                str.append("xc_inst ").append(block.getName()).append(" ")
                        .append(block.getProto().getName()).append(" ")
                        .append(" { ");
                for(PortInst port : getPortsOfNodeInst(block)) {
                    String portName = CommonMethods.getOnlyIteratorObject(port.getExports()).getName();
                    str.append(portName).append("=").append(netlist.getNetwork(port).getName());
                }
                str.append("\n");
            }
            return str;
        }

        /**
         * Initialize general structures.
         */
        private void init() {
            // fill blockList
            Iterator<NodeInst> niItr = cell.getNodes();
            while (niItr.hasNext()) {
                NodeInst next = niItr.next();
                if (next.getProto().getName().contains("input")) {
                    inputList.add(next);
                } else if (next.getProto().getName().contains("output")) {
                    outputList.add(next);
                } else if (next.getProto().getName().contains("clock")) {
                   clockList.add(next);
                } else {
                    blockList.add(niItr.next());
                }
            }
            // fill connectionMap
            fillConnectionMap(blockList);
            fillConnectionMap(inputList);
            fillConnectionMap(outputList);
        }

        /**
         * Method to analyse all ports of nodeInsts from list. Added all links
         * to connectionMap.
         *
         * @param list
         */
        private void fillConnectionMap(List<NodeInst> list) {
            for (NodeInst ni : list) {
                Iterator<PortInst> piItr = ni.getPortInsts();
                while (piItr.hasNext()) {
                    PortInst pi = piItr.next();
                    connectionMap.put(pi.toString(), getClosestPortInsts(pi));
                }
            }
        }

        /**
         * Method to get initial nodeInst, we can choose any of them but it's
         * more preferable to start
         *
         * @param nodeList
         * @return
         * @throws FunctionalException
         */
        private NodeInst getStartingNodeInst(List<NodeInst> nodeList) throws FunctionalException {
            String[] possibleStartingNodeInsts = Constants.getPossibleStartingNodeInsts();
            for (NodeInst ni : nodeList) {
                for (String startingNode : possibleStartingNodeInsts) {
                    if (ni.getProto().getName().equals(startingNode)) {
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
            if (network == null) {
                return new ArrayList<>();
            }
            List<PortInst> portList = network.getPortsList();

            Iterator<PortInst> portItr = portList.iterator();
            while (portItr.hasNext()) {
                PortInst port = portItr.next();
                for (String str : Constants.getAvailableInvisibleNodeInstsInScheme()) {
                    if (port.toString().contains(str)) {
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

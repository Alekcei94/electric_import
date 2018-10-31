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
import com.sun.electric.database.hierarchy.View;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.Connection;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.variable.Variable;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.CommonMethods;
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.Exceptions.FunctionalException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        ArrayList<String> configurationList = new ArrayList<>();
        Iterator<NodeInst> niItr = curcell.getNodes();
        while (niItr.hasNext()) {
            NodeInst ni = niItr.next();
            System.out.println(ni.toString());
            // HERE WE SHOULD AVOID OTHER BLOCKS WHICH MAYBE HAVE PARAMETERS
            configurationList.addAll(getConfigFromOneNodeInst(ni));
        }

        for (String str : configurationList) {
            System.out.println("mainResult " + str);
        }
    }

    private ArrayList<String> getConfigFromOneNodeInst(NodeInst ni) throws FunctionalException {
        // There are some unintersting blocks like "node generic:Facet-Center['art@0']" 
        ArrayList<String> listOfBlocks = new ArrayList<>();
        listOfBlocks.add("CB");
        listOfBlocks.add("SPM");
        listOfBlocks.add("CAPA");
        listOfBlocks.add("RESA");
        listOfBlocks.add("PPC");
        listOfBlocks.add("PAM");
        boolean isBlock = false;
        /*if(listOfBlocks.contains(ni.toString())) {
            isBlock = true;
        }*/
        // node basic:key{ic}['key@437']
        for (String block : listOfBlocks) {
            if (ni.toString().contains(block)) {
                isBlock = true;
            }
        }
        // return if this block is not block with keys
        if (!isBlock) {
            return new ArrayList<>();
        }

        ArrayList<String> partConfigList = new ArrayList<>();

        Cell equiv = ni.getProtoEquivalent();
        Iterator<NodeInst> niItr = equiv.getNodes();
        while (niItr.hasNext()) {
            NodeInst key = niItr.next();
            if (key.getProto().getName().equals("key")) {
                if (isClosedKey(ni, key)) {
                    partConfigList.add(getConfigForKey(key, ni));
                }
            }
        }
        return partConfigList;
    }

    /*
    * Method to show if the key is closed or not.
    * THERE SHOULDN'T BE MORE THAN 1 
    * @Param key SHOULD BE ONLY KEY, ONLY WITH PORTS X,Y,M1,M2.
    * Method finds the M1 outside export, checks it for connection and get 2nd port by connection.
     */
    private boolean isClosedKey(NodeInst ni, NodeInst key) throws FunctionalException {
        Iterator<PortInst> itrPorts = key.getPortInsts();
        while (itrPorts.hasNext()) {
            PortInst pi = itrPorts.next();
            String port = CommonMethods.parsePortToPort(pi.toString());
            System.out.println("port " + port);
            if (port.equals("M1")) {
                System.out.println(pi.getNodeInst().toString());
                Iterator<Connection> ctnItr = pi.getConnections();
                Connection ctn = getOnlyIteratorObject(ctnItr);
                ArcInst ai = ctn.getArc();
                System.out.println("port " + ai.toString());

                Connection ctnTail = ai.getConnection(0);
                Connection ctnHead = ai.getConnection(1);
                Connection ctnNext;
                if (ctn.toString().equals(ctnTail.toString())) {
                    ctnNext = ctnHead;
                } else {
                    ctnNext = ctnTail;
                }

                PortInst outPort = ctnNext.getPortInst();

                System.out.println("outPort " + outPort.toString());

                Export outExport = getOnlyIteratorObject(outPort.getExports());
                PortInst outsidePort = ni.findPortInstFromEquivalentProto(outExport);

                System.out.println("outsidePort " + outsidePort.toString());

                if (!CommonMethods.parsePortToPort(outsidePort.toString()).equals("mAd" + getOnlyParamOfNodeInst(key) + "_1")) {
                    throw new FunctionalException("Incorrect block map");
                }

                if (!outsidePort.hasConnections()) {
                    return false;
                }
                Connection outsideCtn = getOnlyIteratorObject(outsidePort.getConnections());

                ArcInst outsideArc = outsideCtn.getArc();
                //System.out.println("outsideArc " + outsideArc.toString());

                ctnTail = outsideArc.getConnection(0);
                ctnHead = outsideArc.getConnection(1);
                if (outsideCtn.toString().equals(ctnTail.toString())) {
                    ctnNext = ctnHead;
                } else {
                    ctnNext = ctnTail;
                }
                PortInst secondPort = ctnNext.getPortInst();
                if (CommonMethods.parsePortToPort(secondPort.toString()).equals("mAd" + getOnlyParamOfNodeInst(key) + "_2")) {
                    //System.out.println("mAd" + getOnlyParamOfNodeInst(key) + " is connected");
                    return true;
                }
            }
        }
        return false;
    }

    private String getConfigForKey(NodeInst key, NodeInst ni) throws FunctionalException {
        String parameterOfBlock = getOnlyParamOfNodeInst(ni);
        String parameterOfKey = getOnlyParamOfNodeInst(key);
        return parameterOfBlock + parameterOfKey;
    }

    /*
    * Method to get ONE parameter of nodeInst if there are no more parameters
     */
    private String getOnlyParamOfNodeInst(NodeInst ni) throws FunctionalException {
        ArrayList<String> paramList = new ArrayList<>();
        Iterator<Variable> varItr = ni.getParameters();
        while (varItr.hasNext()) {
            Variable var = varItr.next();
            paramList.add(var.getObject().toString());
            System.out.println("var " + var.getObject().toString());
        }
        if (paramList.size() != 1) {
            throw new FunctionalException("There shouldn't be more than one parameters for global blocks");
        }
        return paramList.get(0);
    }

    /*
    * Method to get ONE object from any iterator if there are no more objects there.
     */
    private <A, B extends Iterator<A>> A getOnlyIteratorObject(B iterator) throws FunctionalException {
        ArrayList<A> objectsList = new ArrayList<>();
        while (iterator.hasNext()) {
            objectsList.add(iterator.next());
        }
        if (objectsList.size() != 1) {
            throw new FunctionalException("More than one object in iterator");
        }
        return objectsList.get(0);
    }

    public class DigitalConfigExport {

        public DigitalConfigExport(String simLibName, String simCellName, String FPGAnodeInstName) {
            NodeInst FPGAcell = getFPGACell(simLibName, simCellName, FPGAnodeInstName);
            String pathToVerilog = LinksHolder.getProjectSimulationPath();
            writeVerilogToFile(FPGAcell, pathToVerilog);
            try {
                formDigitalConfig(pathToVerilog);
            } catch (IOException ex) {
                Logger.getLogger(ExportKeys.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        private DigitalConfigExport() {
            throw new IllegalStateException("DigitalConfigExport constructor must be used with <cell> parameter");
        }
        
        /**
         * Method to get FPGA verilog/icon cell.
         * Method finds cell with given name.
         * @param cell
         * @return 
         */
        private NodeInst getFPGACell(String simLibName, String simCellName,
                String FPGAnodeInstName) {
            Cell simCell = CommonMethods.getCellFromName(simLibName, simCellName);
            Iterator<NodeInst> niItr = simCell.getNodes();
            while(niItr.hasNext()) {
                NodeInst ni = niItr.next();
                if(ni.getName().contains(FPGAnodeInstName)) {
                    return ni;
                }
            }
            return null;
            //return simCell.findNode(FPGAnodeInstName);
        }
        
        private void writeVerilogToFile(NodeInst FPGAcell, String pathToVerilog) {
            Cell verilogCell = (Cell) FPGAcell.getProto();
            Cell verEquiv = verilogCell.getEquivalent();
            if( (verEquiv == null) || (verEquiv.getView() != View.VERILOG) ) {
                throw new IllegalStateException("Corrupted path to simulation folder. Best solution is to reinstall application");
            }
            String[] content = verEquiv.getTextViewContents();
            String verilogFilePath = pathToVerilog + File.separator + "SPI.v";
            Accessory.cleanFile(verilogFilePath);
            for(String str : content) {
                Accessory.write(verilogFilePath, str);
            }
        }
        
        private void formDigitalConfig(String pathToVerilog) throws IOException {
            String verilogFilePath = pathToVerilog + File.separator + "SPI.v";
            String xcadPath = LinksHolder.getXCADPath();
            String shPath = xcadPath + File.separator + "xa_sh.exe";
            String shxaPath = xcadPath + File.separator + "xa";
            System.out.println(shPath +
                " " +
                shxaPath +
                " -i " +
                verilogFilePath +
                " -y++");
            
            ProcessBuilder pb = new ProcessBuilder(shPath,
                shxaPath,
                "-i",
                verilogFilePath,
                "-y++");
            pb.directory(new File(LinksHolder.getProjectSimulationPath()));
            pb.inheritIO();
            try {
               Process p = pb.start(); 
               int exitCode = p.waitFor();
               System.out.println("exit code: " + exitCode);
            } catch(IOException ioe) {
                ioe.printStackTrace();
            } catch (InterruptedException ex) {
                Logger.getLogger(ExportKeys.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
    }
}

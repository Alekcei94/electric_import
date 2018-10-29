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
import com.sun.electric.database.variable.Variable;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.CommonMethods;
import com.sun.electric.tool.dcs.Data.Constants;
import com.sun.electric.tool.dcs.Exceptions.FunctionalException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author diivanov
 */
public class ExportKeys {

    private static ExportKeys exportKeys;
    private final Map<String, Map<String, String>> fullColectionAdres = new HashMap<>();

    /**
     * Instantination of exportKeys singleton.
     */
    public static ExportKeys getInstance() {
        if (exportKeys == null) {
            exportKeys = new ExportKeys();
        }
        return exportKeys;
    }

    /*
     * This method forms Map adress from a given file. Map<real adress, adress in electric>
     */
    private Map<String, String> formCollection(String urlFile) {
        Map<String, String> list = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(urlFile))) {
            String line;
            String[] key;
            while ((line = br.readLine()) != null) {
                key = line.split(" ");
                list.put(key[1], key[0]);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

    /*
     * This method serch name all files in directory.
     */
    private List<File> searchFile(String urlFile) {
        File dir = new File(urlFile);
        File[] arrFiles = dir.listFiles();
        List<File> lst = Arrays.asList(arrFiles);
        return lst;
    }

    /*
     * This method forms Map all addresses and names block. Map<Name block, Map<adress electric, real adress>>
     */
    private void formMapFullAddress() {
        String urlFile = Constants.getPathTo("MAP");
        //String urlFile = "../MAP";
        List<File> adr = searchFile(urlFile);
        for (int i = 0; i < adr.size(); i++) {
            List<File> adrOnFile = searchFile(adr.get(i).toString());
            for (int j = 0; j < adrOnFile.size(); j++) {
                String[] splitKey;
                String gAdr = adrOnFile.get(j).toString();
                gAdr = gAdr.replace('\\', '/');
                splitKey = gAdr.split("/");
                splitKey = splitKey[3].split("\\.");
                fullColectionAdres.put(splitKey[0], formCollection(gAdr));
            }
        }
    }

    /*
     * This method reads file at a given address and displays all information without processing.
     */
    private String getConfigurationFilters(String urlFile) {
        String informKeyInFileOfFilters = null;
        try (BufferedReader br = new BufferedReader(new FileReader(urlFile))) {
            String line;
            informKeyInFileOfFilters = "";
            while ((line = br.readLine()) != null) {
                informKeyInFileOfFilters = informKeyInFileOfFilters + line + "\n";
            }
        } catch (IOException ex) {
            Accessory.showMessage("At this address " + urlFile + " missing file.");
        }
        return informKeyInFileOfFilters;
    }

    /*
     * This method reads the parameters NodeInst and determines the number of the closed key in Electric.
     */
    private String getRealKeyInFile(NodeInst ni, NodeInst key) {
        Iterator<Variable> paramItr = ni.getParameters();
        while (paramItr.hasNext()) {
            Variable param = paramItr.next();
            String adres = "";
            if (param.toString().contains("gAdr")) {
                try {
                    adres = getConfigForKey(key, ni) + "\n";
                } catch (FunctionalException ex) {
                    Logger.getLogger(ExportKeys.class.getName()).log(Level.SEVERE, null, ex);
                }
                return adres;
            } else if (param.toString().contains("uniq")) {
                Map<String, String> list = new HashMap<>();
                try {
                    adres = getOnlyParamOfNodeInst(key);
                } catch (FunctionalException ex) {
                    Logger.getLogger(ExportKeys.class.getName()).log(Level.SEVERE, null, ex);
                }
                list = fullColectionAdres.get(param.getObject().toString());
                if (list == null) {
                    Accessory.printLog("The file or number of the key being found could not be found - " + param.getObject().toString());
                    break;
                }
                adres = list.get(adres);//method to perform config generation once having read all the files at once.  (1)    choose 1 or 2
                //adres = readFileMap(param, adres);//method to perform config formation with reading from file.     (2)
                if (adres == null) {
                    Accessory.printLog("The file or number of the key being found could not be found.");
                    return null;
                } else {
                    adres = adres + "\n";
                }
                return adres;
            }
        }
        return null;
    }

    /*
     * This method creates the netList at ../config/config.txt  .
     */
    public void formConfig() throws FunctionalException {
        if (fullColectionAdres.isEmpty()) {
            formMapFullAddress();
        }
        String adr = "";
        try (FileWriter writer = new FileWriter(Constants.getPathTo("config"), false)) {
            String urlFile = "adres";
            //write config FPGA;
            /*writer.write("-- FPGA configuration --" + "\n");//Made to validate the formation netList.
            adr = getConfigurationFilters(urlFile);
            writer.write(adr);*/
            // writer.write("-- Scheme configuration --" + "\n");//Made to validate the formation netList.
            Cell curcell = Job.getUserInterface().getCurrentCell();
            Iterator<NodeInst> niItr = curcell.getNodes();
            String[] listNameBloc = {"CB", "AOP", "BUS14SW5V", "RESA", "CAPA", "DIOP", "DIOP_EN_key", "MUX", "LSHDIRLINE", "HVAOP", "OR", "PPC"};
            while (niItr.hasNext()) {
                NodeInst ni = niItr.next();
                boolean flag = false;
                for (String list : listNameBloc) {
                    if (ni.getName().contains(list)) {
                        flag = true;
                    }
                }
                if (flag) {
                    Cell equiv = ni.getProtoEquivalent();
                    Iterator<NodeInst> keyItr = equiv.getNodes();
                    while (keyItr.hasNext()) {
                        NodeInst key = keyItr.next();
                        if (key.getProto().getName().equals("key")) {
                            if (isClosedKey(ni, key)) {
                                String adres = "";
                                adres = getRealKeyInFile(ni, key);
                                writer.write(adres);
                            }
                        }
                    }
                }
            }
            urlFile = "filterDesign.txt";
            writer.write("-- filter configuration --" + "\n");//Made to validate the formation netList.
            adr = getConfigurationFilters(urlFile);
            writer.write(adr);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
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

    /*
    * This method delete a "h" simbol in the param to write to the file. It was gAdr = 0eh; It became gAdr = 0e
    * else return param
    */
    private String deleteACharacterInTheParam(String param) {
        if (param.charAt(param.length()-1)=='h'){
            param = param.substring​(0,param.length()-1);
            return param;
        }
        return param;
    }

    private ArrayList<String> getConfigFromOneNodeInst(NodeInst ni) throws FunctionalException {
        // There are some unintersting blocks like "node generic:Facet-Center['art@0']" 
        ArrayList<String> listOfBlocks = new ArrayList<>();
        listOfBlocks.add("CB");
        listOfBlocks.add("PPC");

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

    /**
     * Method to show if the key is closed or not. THERE SHOULDN'T BE MORE THAN
     * 1
     *
     * @Param key SHOULD BE ONLY KEY, ONLY WITH PORTS X,Y,M1,M2. Method finds
     * the M1 outisde export, checks it for connection and get 2nd port by
     * connection.
     */
    private boolean isClosedKey(NodeInst ni, NodeInst key) throws FunctionalException {
        Iterator<PortInst> itrPorts = key.getPortInsts();
        while (itrPorts.hasNext()) {
            PortInst pi = itrPorts.next();
            String port = CommonMethods.parsePortToPort(pi.toString());
            //System.out.println("port " + port);
            if (port.equals("M1")) {
                //System.out.println(pi.getNodeInst().toString());
                Iterator<Connection> ctnItr = pi.getConnections();
                Connection ctn = getOnlyIteratorObject(ctnItr);
                ArcInst ai = ctn.getArc();
                //System.out.println("port " + ai.toString());

                Connection ctnTail = ai.getConnection(0);
                Connection ctnHead = ai.getConnection(1);
                Connection ctnNext;
                if (ctn.toString().equals(ctnTail.toString())) {
                    ctnNext = ctnHead;
                } else {
                    ctnNext = ctnTail;
                }

                PortInst outPort = ctnNext.getPortInst();

                //System.out.println("outPort " + outPort.toString());
                Export outExport = getOnlyIteratorObject(outPort.getExports());
                PortInst outsidePort = ni.findPortInstFromEquivalentProto(outExport);

                //System.out.println("outsidePort " + outsidePort.toString());
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
        parameterOfBlock = deleteACharacterInTheParam(parameterOfBlock);
        String parameterOfKey = getOnlyParamOfNodeInst(key);
        return parameterOfBlock + parameterOfKey;
    }

    /**
     * Method to get ONE parameter of nodeInst if there are no more parameters
     */
    private String getOnlyParamOfNodeInst(NodeInst ni) throws FunctionalException {
        ArrayList<String> paramList = new ArrayList<>();
        Iterator<Variable> varItr = ni.getParameters();
        while (varItr.hasNext()) {
            Variable var = varItr.next();
            paramList.add(var.getObject().toString());
            //System.out.println("var " + var.getObject().toString());
        }
        if (paramList.size() != 1) {
            throw new FunctionalException("There shouldn't be more than one parameters for global blocks");
        }
        return paramList.get(0);
    }

    /**
     * Method to get ONE object from any iterator if there are no more objects
     * there.
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

    private Object Constans() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

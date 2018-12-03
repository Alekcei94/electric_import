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
import com.sun.electric.tool.dcs.Exceptions.FunctionalException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.Design.FpgaArgumentsUI;
import com.sun.electric.tool.user.dialogs.ExecDialog;
import com.sun.electric.tool.user.ui.TopLevel;
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
    private final Map<String, Map<String, String>> fullCollectionAddress = new HashMap<>();

    /**
     * Instantination of exportKeys singleton.
     *
     * @return instance.
     */
    public static ExportKeys getInstance() {
        if (exportKeys == null) {
            exportKeys = new ExportKeys();
        }
        return exportKeys;
    }

    /*
     * This method forms Map address from a given file. Map<real address, address in electric>
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
     * This method search name all files in directory.
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
        String urlFile = LinksHolder.getPathTo("MAP");
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
                fullCollectionAddress.put(splitKey[0], formCollection(gAdr));
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
            String address = "";
            if (param.toString().contains("gAdr")) {
                try {
                    address = getConfigForKey(key, ni) + "\n";
                } catch (FunctionalException ex) {
                    Logger.getLogger(ExportKeys.class.getName()).log(Level.SEVERE, null, ex);
                }
                return address;
            } else if (param.toString().contains("uniq")) {
                Map<String, String> list = new HashMap<>();
                try {
                    address = getOnlyParamOfNodeInst(key);
                } catch (FunctionalException ex) {
                    Logger.getLogger(ExportKeys.class.getName()).log(Level.SEVERE, null, ex);
                }
                list = fullCollectionAddress.get(param.getObject().toString());
                if (list == null) {
                    Accessory.printLog("The file or number of the key being found could not be found - " + param.getObject().toString());
                    break;
                }
                address = list.get(address);//method to perform config generation once having read all the files at once.  (1)    choose 1 or 2
                //adres = readFileMap(param, address);//method to perform config formation with reading from file.     (2)
                if (address == null) {
                    Accessory.printLog("The file or number of the key being found could not be found.");
                    return null;
                } else {
                    address = address + "\n";
                }
                return address;
            }
        }
        return null;
    }

    /*
     * This method creates config file at ../config/config.txt  .
     */
    public void formConfig() throws FunctionalException {
        if (fullCollectionAddress.isEmpty()) {
            formMapFullAddress();
        }
        //Accessory.showMessage("Please wait.");
        SchemeConfigExport SchemeConfigExport = new SchemeConfigExport();
        FilterConfigExport FilterConfigExport = new FilterConfigExport();

        String simLibName = "5400TP094";
        String simCellName = "5400TP094";
        String FPGAnodeInstName = "FPGA";
        DigitalConfigExport DigitalConfigExport = new DigitalConfigExport(simLibName, simCellName, FPGAnodeInstName);
        //ExportKeys ek = ExportKeys.getInstance();
        //ek.new DigitalConfigExport(simLibName, simCellName, FPGAnodeInstName);
        try (FileWriter writer = new FileWriter(LinksHolder.getPathTo("config"), false)) {
            String config = DigitalConfigExport.getConfigurationFPGA();
            config = config + SchemeConfigExport.schemeConfigExport();
            config = config + FilterConfigExport.formConfigFiltersExportFile();
            writer.write(config);
            //Accessory.showMessage("Finish.");
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
        if (param.charAt(param.length() - 1) == 'h') {
            param = param.substring​(0, param.length() - 1);
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
     * the M1 outside export, checks it for connection and get 2nd port by
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

    /*
     * In this class the config is formed. Reading the keys of the general scheme
     */
    public class SchemeConfigExport {

        public String schemeConfigExport() throws FunctionalException {
            String configSchemeExportFile;
            Cell curcell = Job.getUserInterface().getCurrentCell();
            configSchemeExportFile = "-- Scheme configuration --" + "\n";//Made to validate the formation netList.
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
                                configSchemeExportFile = configSchemeExportFile + getRealKeyInFile(ni, key);
                            }
                        }
                    }
                }
            }
            return configSchemeExportFile;
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
                    list = fullCollectionAddress.get(param.getObject().toString());
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
    }

    /**
     * In this class the config is formed. Reading the keys of the filter
     */
    public class FilterConfigExport {

        public String formConfigFiltersExportFile() {
            String configFiltersExportFile;
            String urlFile = "filterDesign.txt";
            configFiltersExportFile = "-- filter configuration --" + "\n";//Made to validate the formation netList.
            configFiltersExportFile = configFiltersExportFile + getConfigurationFilters(urlFile);
            return configFiltersExportFile;
        }

        /**
         * This method reads file at a given address and displays all
         * information without processing.
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
    }

    public class DigitalConfigExport {

        public DigitalConfigExport(String simLibName, String simCellName, String FPGAnodeInstName) {
            if(!FpgaArgumentsUI.checkForTopArgument()) {
                Accessory.showMessage("Top argument wasn't set");
                return;
            }
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
         * This method reads file at a given address and displays all
         * information without processing.
         */
        private String getConfigurationFPGA() {
            String informKeyInFileOfFPGA = null;
            try (BufferedReader br = new BufferedReader(new FileReader("../Projects/5400TP094/simulation/Verilog.bitnum"))) {
                String line;
                informKeyInFileOfFPGA = "-- FPGA configuration --" + '\n';
                while ((line = br.readLine()) != null) {
                    informKeyInFileOfFPGA = informKeyInFileOfFPGA + line + '\n';
                }
            } catch (IOException ex) {
                //Accessory.showMessage("Missing file.");
                throw new AssertionError("Missing file");
            }
            return informKeyInFileOfFPGA;
        }

        /**
         * Method to get FPGA verilog/icon cell. Method finds cell with given
         * name.
         *
         * @param cell
         * @return
         */
        private NodeInst getFPGACell(String simLibName, String simCellName,
                String FPGAnodeInstName) {
            Cell simCell = CommonMethods.getCellFromName(simLibName, simCellName);
            if(simCell == null) {
                throw new AssertionError("FPGA cell was not found");
            }
            Iterator<NodeInst> niItr = simCell.getNodes();
            while (niItr.hasNext()) {
                NodeInst ni = niItr.next();
                if (ni.getName().contains(FPGAnodeInstName)) {
                    return ni;
                }
            }
            return null;
            //return simCell.findNode(FPGAnodeInstName);
        }

        private void writeVerilogToFile(NodeInst FPGAcell, String pathToVerilog) {
            Cell verilogCell = (Cell) FPGAcell.getProto();
            Cell verEquiv = verilogCell.getVerilogEquivalent();
            if ((verEquiv == null) || (verEquiv.getView() != View.VERILOG)) {
                throw new IllegalStateException("Corrupted path to simulation folder."
                        + " You should use main scheme with FPGA Cell. Otherwuse reinstall may help.");
            }
            String[] content = verEquiv.getTextViewContents();
            String verilogFilePath = pathToVerilog + File.separator + "Verilog.v";
            Accessory.cleanFile(verilogFilePath);
            for (String str : content) {
                Accessory.write(verilogFilePath, str);
            }
        }

        private void formDigitalConfig(String pathToVerilog) throws IOException {
            String verilogFilePath = pathToVerilog + File.separator + "Verilog.v";
            String xcadPath = LinksHolder.getXCADPath();
            String shPath = xcadPath + File.separator + "xa_sh.exe";
            String shxaPath = xcadPath + File.separator + "xa";
            String command = shPath + " "
                    + shxaPath
                    + " -i ";

            command += verilogFilePath + " -y++";
            File dir = new File(pathToVerilog);
            ExecDialog dialog = new ExecDialog(TopLevel.getCurrentJFrame(), false);
            //dialog.startProcess(command, null, dir);
            //test 22.11.2018
            List<String> argumentsList = FpgaArgumentsUI.getArgumentList();
            for(String argument : argumentsList) {
                command = addParameterToCommand(command, argument);
            }
            command = addTopArgument(command);
            System.out.println(command);
            dialog.startProcess(command, null, dir);
        }
        
        private String addParameterToCommand(String before, String parameter) {
            String after = before + " " + parameter;
            return after;
        }
        
        private String addTopArgument(String arguments) {
            return arguments + " " + FpgaArgumentsUI.getTopArgument();
        }
    }
}

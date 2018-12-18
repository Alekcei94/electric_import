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
import com.sun.electric.tool.dcs.Data.Constants;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.CommonMethods;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.Data.MemoryMap;
import com.sun.electric.tool.dcs.Design.FilterDesignWindowUIFrame;
import com.sun.electric.tool.dcs.Design.FpgaArgumentsUI;
import com.sun.electric.tool.dcs.Exceptions.InvalidStructureError;
import com.sun.electric.tool.user.dialogs.ExecDialog;
import com.sun.electric.tool.user.ui.TopLevel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author diivanov
 */
public class ExportKeys implements Exportable {

    /**
     * This method creates config file at ../config/config.txt .
     */
    public static void formConfig() {
        try (FileWriter writer = new FileWriter(LinksHolder.getPathTo("config"), false)) {
            StringBuilder configAllScheme = new ExportKeys().getConfigExport();
            writer.write(configAllScheme.toString());
        } catch (IOException ex) {
            Accessory.showMessage("Unable to write data to file.");
            throw new AssertionError("Unable to write data to file.");
            //TODO: обработать ошибку (Тоже, наверно, готово)
        }
    }

    public StringBuilder getConfigExport() {
        StringBuilder configAllScheme = new StringBuilder();
        AnalogConfigExport SchemeConfigExport = new AnalogConfigExport();
        FilterConfigExport FilterConfigExport = new FilterConfigExport();

        //String simLibName = "5400TP094";
        //String simCellName = "5400TP094";
        //String FPGAnodeInstName = "FPGA";
        //DigitalConfigExport DigitalConfigExport = new DigitalConfigExport(simLibName, simCellName, FPGAnodeInstName);
        String FPGAnodeInstName = "FPGA";
        Cell mainCell = Job.getUserInterface().getCurrentCell();
        DigitalConfigExport DigitalConfigExport = new DigitalConfigExport(mainCell, FPGAnodeInstName);
        configAllScheme.append(DigitalConfigExport.getConfigExport().toString());
        configAllScheme.append(SchemeConfigExport.getConfigExport().toString());
        FilterDesignWindowUIFrame flagReadConfigFilters = FilterDesignWindowUIFrame.getFilterDesignWindowUIFrame();
        System.out.println(" test = " + flagReadConfigFilters.getEnableStatus());
        if (flagReadConfigFilters.getEnableStatus()) {
            configAllScheme.append(FilterConfigExport.getConfigExport().toString());
        }
        return configAllScheme;
    }

    /**
     * This method delete a "h" simbol in the param to write to the file. It was
     * gAdr = 0eh; It became gAdr = 0e else return param
     */
    private static String deleteACharacterInTheParam(String param) {
        if (param.charAt(param.length() - 1) == 'h') {
            param = param.substring​(0, param.length() - 1);
            // return param не нужен (Готово)
        }
        return param;
    }

    /*
    *
     */
    public static void getContructionDigitalConfigExport(Cell mainCell, String FPGAnodeInstName) {
        new DigitalConfigExport(mainCell, FPGAnodeInstName);
    }

    /**
     * Method to show if the key is closed or not. THERE SHOULDN'T BE MORE THAN
     * 1
     *
     * @Param key SHOULD BE ONLY KEY, ONLY WITH PORTS X,Y,M1,M2. Method finds
     * the M1 outside export, checks it for connection and get 2nd port by
     * connection.
     */
    private static boolean isClosedKey(NodeInst ni, NodeInst key) {
        Iterator<PortInst> itrPorts = key.getPortInsts();
        while (itrPorts.hasNext()) {
            PortInst pi = itrPorts.next();
            String port = CommonMethods.parsePortToPort(pi.toString());
            if (port.equals("M1")) {
                Iterator<Connection> ctnItr = pi.getConnections();
                Connection ctn = Accessory.getOnlyIteratorObject(ctnItr);
                ArcInst ai = ctn.getArc();

                Connection ctnTail = ai.getConnection(0);
                Connection ctnHead = ai.getConnection(1);
                Connection ctnNext;
                if (ctn.toString().equals(ctnTail.toString())) {
                    ctnNext = ctnHead;
                } else {
                    ctnNext = ctnTail;
                }

                PortInst outPort = ctnNext.getPortInst();

                Export outExport = Accessory.getOnlyIteratorObject(outPort.getExports());
                PortInst outsidePort = ni.findPortInstFromEquivalentProto(outExport);

                if (!CommonMethods.parsePortToPort(outsidePort.toString()).equals("mAd"
                        + Accessory.getOnlyParamOfNodeInst(key) + "_1")) {
                    throw new InvalidStructureError("Incorrect block map");
                }

                if (!outsidePort.hasConnections()) {
                    return false;
                }
                Connection outsideCtn = Accessory.getOnlyIteratorObject(outsidePort.getConnections());

                ArcInst outsideArc = outsideCtn.getArc();

                ctnTail = outsideArc.getConnection(0);
                ctnHead = outsideArc.getConnection(1);
                if (outsideCtn.toString().equals(ctnTail.toString())) {
                    ctnNext = ctnHead;
                } else {
                    ctnNext = ctnTail;
                }
                PortInst secondPort = ctnNext.getPortInst();
                if (CommonMethods.parsePortToPort(secondPort.toString()).equals("mAd"
                        + Accessory.getOnlyParamOfNodeInst(key) + "_2")) {
                    //System.out.println("mAd" + getOnlyParamOfNodeInst(key) + " is connected");
                    return true;
                }
            }
        }
        return false;
    }

    private static String getConfigForKey(NodeInst key, NodeInst ni) {
        String parameterOfBlock = Accessory.getOnlyParamOfNodeInst(ni);
        parameterOfBlock = deleteACharacterInTheParam(parameterOfBlock);
        String parameterOfKey = Accessory.getOnlyParamOfNodeInst(key);
        return parameterOfBlock + parameterOfKey;
    }

    /**
     * Class holds analog exporting logic.
     */
    private static class AnalogConfigExport implements Exportable {

        /*
         * This method forms the Analog configuration 
         */
        public StringBuilder getConfigExport() {
            StringBuilder configAnalogSchem;
            String configSchemeExportFile;
            Cell curcell = Job.getUserInterface().getCurrentCell();
            // TODO: нужно сделать для произвольной схемы, из определенных схем, (ВАЖНО, найти решение)(Хммм)
            configSchemeExportFile = "-- Scheme configuration --" + "\n";//Made to validate the formation netList.
            Iterator<NodeInst> niItr = curcell.getNodes();
            String[] arrayStringNameBlock = getArrayNameBlocks();
            // REV: listNameBlock - не лист, а массив. Название может ввести в заблуждение. (Готово)
            // TODO: желательно попробовать убрать привязку к конкретным блокам. (Готово)
            while (niItr.hasNext()) {
                NodeInst ni = niItr.next();
                boolean flag = false;
                for (String nameBlock : arrayStringNameBlock) {
                    // REV: list - не элемент. (Готово)
                    //System.out.print(ni.getName());
                    //System.out.print(" : ");
                    //System.out.print(nameBlock + '\n' + '\n');
                    if (ni.getName().contains(nameBlock)) {
                        // TODO: в идеале использовать equals
                        flag = true;
                        break;
                    }

                }
                // TODO: следует убрать лишние проходы в цикле (Готово)
                if (flag) {
                    Cell equiv = ni.getProtoEquivalent();
                    Iterator<NodeInst> keyItr = equiv.getNodes();
                    while (keyItr.hasNext()) {
                        NodeInst key = keyItr.next();
                        if (key.getProto().getName().equals(Constants.getKey())) {
                            // TODO: нужно будет заменить хардкод key на считывание из класса констант. (Готово)
                            if (isClosedKey(ni, key)) {
                                configSchemeExportFile += getRealKeyInFile(ni, key);
                                // REV: лучше использовать += (Готово)
                            }
                        }
                    }
                }
            }
            configAnalogSchem = new StringBuilder(configSchemeExportFile);
            return configAnalogSchem;
        }

        /*
         * Forms an array of names of used blocks. Path: electric/accessory/allUsedBlocks.acc
         */
        private String[] getArrayNameBlocks() {
            //try (FileReader reader = new FileReader(LinksHolder.getPathFileAllUsedBlocksInScheme())) {
            ArrayList<String> arraylistReadInFileNameBLocks = new ArrayList<>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(LinksHolder.getPathFileAllUsedBlocksInScheme()));
                String line;
                while ((line = br.readLine()) != null) {
                    arraylistReadInFileNameBLocks.add(line);
                }
            } catch (IOException ex) {
                //Сделать обработку ошибки(Готово)
                Accessory.showMessage("At this address " + LinksHolder.getPathFileAllUsedBlocksInScheme() + " missing file.");
                throw new AssertionError("At this address " + LinksHolder.getPathFileAllUsedBlocksInScheme() + " missing file.");

            }
            String[] arrayNameBlocks = new String[arraylistReadInFileNameBLocks.size()];
            int i = 0;
            System.out.println(arraylistReadInFileNameBLocks.size());
            for (String nameBlock : arraylistReadInFileNameBLocks) {
                arrayNameBlocks[i] = nameBlock;
                i++;
            }
            return arrayNameBlocks;
        }

        /**
         * This method reads the parameters NodeInst and determines the number
         * of the closed key in Electric.
         */
        private String getRealKeyInFile(NodeInst ni, NodeInst key) {
            Iterator<Variable> paramItr = ni.getParameters();
            while (paramItr.hasNext()) {
                Variable param = paramItr.next();
                if (param.toString().contains("gAdr")) {
                    String address = getConfigForKey(key, ni) + "\n";
                    return address;
                } else if (param.toString().contains("uniq")) {
                    String address = MemoryMap.getInstance().getTrueAddress(param.getObject().toString(),
                            Accessory.getOnlyParamOfNodeInst(key));
                    if (address == null) {
                        throw new InvalidStructureError("Local address is not correct: "
                                + param + "/" + Accessory.getOnlyParamOfNodeInst(key));
                    }
                    address += "\n";
                    return address;
                } else {
                    // else is not needed (we shouldn't handle other parameters)
                }
            }
            throw new InvalidStructureError("No parameters found");
        }
    }

    /**
     * Class holds filter exporting logic.
     */
    private static class FilterConfigExport implements Exportable {

        /*public String formConfigFiltersExportFile() {
            String urlFile = LinksHolder.getFilterConfig();
            // TODO: убрать хардкод (LinksHolder) (Готово)
            String configFiltersExportFile = "-- filter configuration --" + "\n";//Made to validate the formation netList.
            configFiltersExportFile += getConfigurationFilters(urlFile);
            return configFiltersExportFile;
        }*/
        public StringBuilder getConfigExport() {
            String urlFile = LinksHolder.getFilterConfig();
            StringBuilder configFilters;
            // TODO: убрать хардкод (LinksHolder)(Готово)
            String configFiltersExportFile = "-- filter configuration --" + "\n";//Made to validate the formation netList.
            configFiltersExportFile = configFiltersExportFile + getConfigurationFilters(urlFile);
            configFilters = new StringBuilder(configFiltersExportFile);
            return configFilters;
        }

        /**
         * This method reads file at a given address and displays all
         * information without processing.
         */
        private String getConfigurationFilters(String urlFile) {
            String informKeyInFileOfFilters = "";
            try (BufferedReader br = new BufferedReader(new FileReader(urlFile))) {
                String line;
                //informKeyInFileOfFilters;
                while ((line = br.readLine()) != null) {
                    informKeyInFileOfFilters = informKeyInFileOfFilters + line + "\n";
                }
            } catch (IOException ex) {
                Accessory.showMessage("At this address " + urlFile + " missing file.");
            }
            return informKeyInFileOfFilters;
        }
    }

    /**
     * Class holds digital exporting logic.
     */
    private static class DigitalConfigExport implements Exportable {

        /**
         * Main constructor, by creating object you're doing digital export job.
         *
         * @param mainCell
         * @param FPGAnodeInstName
         */
        public DigitalConfigExport(Cell mainCell, String FPGAnodeInstName) {
            doExport(mainCell, FPGAnodeInstName);
        }

        /**
         * Something like telescoping constructor, using strings to get cell.
         *
         * @param simLibName
         * @param simCellName
         * @param FPGAnodeInstName
         */
        public DigitalConfigExport(String simLibName,
                String simCellName, String FPGAnodeInstName) {
            doExport(CommonMethods.getCellFromLibAndCellName(simLibName, simCellName),
                    FPGAnodeInstName);
        }

        /**
         * Export job controller is incapsulated here. Actions: get fgpa
         * nodeInst from mainCell, then write Verilog code from fpga's nodeInst
         * to external file. Last step is to invoke XCAD script to synthesys,
         * place and route verilog into real fpga configuration.
         *
         * @param mainCell
         * @param FPGAnodeInstName
         */
        private void doExport(Cell mainCell, String FPGAnodeInstName) {
            if(mainCell == null) {
                Accessory.showMessage("Main cell was not found");
                return;
            }
            if (!FpgaArgumentsUI.checkForTopArgument()) {
                Accessory.showMessage("Top argument wasn't set");
                return;
            }
            NodeInst FPGAcell = getFPGACell(mainCell, FPGAnodeInstName);
            String pathToVerilog = LinksHolder.getProjectSimulationPath();
            writeVerilogToFile(FPGAcell, pathToVerilog);
            formDigitalConfig(pathToVerilog);
        }

        /**
         * This method reads file at a given address and displays all
         * information without processing.
         */
        @Override
        public StringBuilder getConfigExport() {
            StringBuilder configFPGA;
            String address = LinksHolder.getPathInFileConfigFPGABlock();
            try (BufferedReader br = new BufferedReader(new FileReader(address))) {
                String informKeyInFileOfFPGA;
                //TODO: hardcode/address (LinksHolder)(Готово)
                String line;
                informKeyInFileOfFPGA = "-- FPGA configuration --" + '\n';
                while ((line = br.readLine()) != null) {
                    informKeyInFileOfFPGA += line + '\n';
                }
                configFPGA = new StringBuilder(informKeyInFileOfFPGA);
            } catch (IOException ex) {
                Accessory.showMessage("Missing file in address" + LinksHolder.getPathInFileConfigFPGABlock());
                throw new AssertionError("Missing file in address" + LinksHolder.getPathInFileConfigFPGABlock());
                //TODO: обработка ошибки (Вроде готово)
            }
            return configFPGA;
        }

        /**
         * Method to get FPGA verilog/icon cell. Method finds cell with given
         * name.
         *
         * @param cell
         * @return
         */
        private NodeInst getFPGACell(Cell simCell, String FPGAnodeInstName) {
            if (simCell == null) {
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
        }

        /**
         * Method to write text from FPGAcell's verilog TextView to external
         * File.
         *
         * @param FPGAcell
         * @param pathToVerilog
         */
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

        /**
         * Method to invoke XCAD script for verilog file.
         *
         * @param pathToVerilog
         * @throws IOException
         */
        private void formDigitalConfig(String pathToVerilog) {
            String verilogFilePath = pathToVerilog + File.separator + "Verilog.v";
            String xcadPath = LinksHolder.getXCADPath();
            String shPath = xcadPath + File.separator + "xa_sh.exe";
            String shxaPath = xcadPath + File.separator + "xa";
            String command = shPath + " " + shxaPath + " -i";
            command = addArgument(command, verilogFilePath + " -y++");
            List<String> argumentsList = FpgaArgumentsUI.getArgumentList();
            for (String argument : argumentsList) {
                command = addArgument(command, argument);
            }
            command = addTopArgument(command);
            System.out.println(command);

            ExecDialog dialog = new ExecDialog(TopLevel.getCurrentJFrame(), false);
            File dir = new File(pathToVerilog);
            dialog.startProcess(command, null, dir);
        }

        /**
         * Supportive method to add top argument to main command.
         *
         * @param arguments
         * @return
         */
        private String addTopArgument(String arguments) {
            return addArgument(arguments, FpgaArgumentsUI.getTopArgument());
        }

        /**
         * Supportive method to add any argument to main command.
         *
         * @param command
         * @param argument
         * @return
         */
        private String addArgument(String command, String argument) {
            return command + " " + argument;
        }

    }

}

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
import com.sun.electric.tool.dcs.Data.Constants;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.CommonMethods;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import com.sun.electric.tool.dcs.Data.LinksHolder;
import com.sun.electric.tool.dcs.Data.MemoryMap;
import com.sun.electric.tool.dcs.Design.FilterDesignWindowUIFrame;
import com.sun.electric.tool.dcs.Design.FpgaArgumentsUI;
import com.sun.electric.tool.dcs.Exceptions.InvalidStructureError;
import com.sun.electric.tool.user.User;
import com.sun.electric.tool.user.dialogs.ExecDialog;
import com.sun.electric.tool.user.ui.TopLevel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author diivanov
 */
public class ExportKeys {

    private static final Logger logger = LoggerFactory.getLogger(ExportKeys.class);
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * This method creates config file at ../config/config.txt .
     *
     * @param mainCell
     */
    private static void formConfig(Cell mainCell) {
        StringBuilder config;
        try {
            config = new ExportKeys().getConfigExport(mainCell);
        } catch (InterruptedException ex) {
            logger.error("Process was interrupted", ex);
            Accessory.showMessage("Interrupted by user");
            return;
        } catch (ExecutionException ex) {
            logger.error("ExecutionException in exporting process", ex);
            Accessory.showMessage("Export process failed.");
            return;
        }
        Accessory.cleanFile(LinksHolder.getPathTo("config"));
        Accessory.write(LinksHolder.getPathTo("config"), config.toString());
        Accessory.showMessage("Export keys process completed.");
    }

    public static void startFormingConfig(Cell mainCell) {
        new ExportKeys.ExportAllKeys(mainCell);
    }

    /**
     * Method to get and unite all config files.
     *
     * @param mainCell
     * @return
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    public StringBuilder getConfigExport(Cell mainCell) throws InterruptedException, ExecutionException {
        StringBuilder analogFuture = executeConfigExport(new AnalogConfigExport(), mainCell);
        StringBuilder digitalFuture = executeConfigExport(new DigitalConfigExport(), mainCell);
        StringBuilder filterFuture = new StringBuilder();

        FilterDesignWindowUIFrame filterUI = FilterDesignWindowUIFrame.getFilterDesignWindowUIFrame();

        if (filterUI.getEnableStatus()) {
            filterFuture = executeConfigExport(new FilterConfigExport(), mainCell);
        }
        StringBuilder config = analogFuture;
        config.append(digitalFuture);
        if (filterFuture != null) {
            config.append(filterFuture);
        }

        return config;
    }

    /**
     * Method to run exportable in new thread.
     *
     * @param export
     */
    private StringBuilder executeConfigExport(Exportable export, Cell mainCell) {
        return export.getConfigExport(mainCell);
    }

    /**
     * Method to run exportable in new thread.
     *
     * @param export
     */
    private Future<StringBuilder> executeConfigExportMultiThread(Exportable export, Cell mainCell) {
        Callable<StringBuilder> callable = () -> {
            try {
                return export.getConfigExport(mainCell);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement element : e.getStackTrace()) {
                    sb.append(element.toString());
                    sb.append("\n");
                }
                logger.info(sb.toString());
                throw new Exception("Fatal error");
            }

        };
        Future<StringBuilder> future = executor.submit(callable);
        return future;
    }

    /**
     * Class holds analog exporting logic.
     */
    private static class AnalogConfigExport implements Exportable {

        private StringBuilder analogConfigExport;

        /**
         * Method to get analog config export.
         *
         * @return
         */
        @Override
        public StringBuilder getConfigExport(Cell mainCell) {
            prepareConfigExport(mainCell);
            return analogConfigExport;
        }

        @Override
        public StringBuilder getExistingConfigExport() {
            if (analogConfigExport == null) {
                String s = "Analog config was not created.";
                logger.error(s);
                //Accessory.showMessage(s);
                throw new IllegalStateException(s);
            }
            return analogConfigExport;
        }

        private void prepareConfigExport(Cell mainCell) {
            analogConfigExport = getAnalogConfig(mainCell);
        }

        /**
         * This method forms the Analog configuration
         */
        public StringBuilder getAnalogConfig(Cell mainCell) {
            StringBuilder configAnalogSchem;
            String configSchemeExportFile;
            // TODO: нужно сделать для произвольной схемы, из определенных схем, (ВАЖНО, найти решение)(Хммм)
            configSchemeExportFile = "-- Scheme configuration --" + "\n";//Made to validate the formation netList.
            Iterator<NodeInst> niItr = mainCell.getNodes();
            List<String> stringNameBlocksList = getNamesOfBlocks();
            while (niItr.hasNext()) {
                NodeInst ni = niItr.next();
                boolean flag = false;
                for (String blockName : stringNameBlocksList) {
                    if (ni.getName().contains(blockName)) {
                        // TODO: в идеале использовать equals
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    Cell equiv = ni.getProtoEquivalent();
                    Iterator<NodeInst> keyItr = equiv.getNodes();
                    while (keyItr.hasNext()) {
                        NodeInst key = keyItr.next();
                        if (key.getProto().getName().equals(Constants.getKey())) {
                            if (isClosedKey(ni, key)) {
                                configSchemeExportFile += getRealKeyInFile(ni, key);
                            }
                        }
                    }
                }
            }
            configAnalogSchem = new StringBuilder(configSchemeExportFile);
            return configAnalogSchem;
        }

        /**
         * Forms names list of used blocks. Path:
         * electric/accessory/allUsedBlocks.acc
         */
        private List<String> getNamesOfBlocks() {
            List<String> listReadInFileNameBLocks = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(LinksHolder.getPathFileAllUsedBlocksInScheme()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    listReadInFileNameBLocks.add(line);
                }
            } catch (IOException ex) {
                Accessory.showMessage("At this address " + LinksHolder.getPathFileAllUsedBlocksInScheme() + " missing file.");
                throw new AssertionError("At this address " + LinksHolder.getPathFileAllUsedBlocksInScheme() + " missing file.");
            }
            return listReadInFileNameBLocks;
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
                            CommonMethods.getOnlyParamOfNodeInst(key));
                    if (address == null) {
                        throw new InvalidStructureError("Local address is not correct: "
                                + param + "/" + CommonMethods.getOnlyParamOfNodeInst(key));
                    }
                    address += "\n";
                    return address;
                } else {
                    // else is not needed (we shouldn't handle other parameters)
                }
            }
            throw new InvalidStructureError("No parameters found");
        }

        /**
         * This method delete a "h" simbol in the param to write to the file. It
         * was gAdr = 0eh; It became gAdr = 0e else return param
         */
        private static String deleteACharacterInTheParam(String param) {
            if (param.charAt(param.length() - 1) == 'h') {
                param = param.substring​(0, param.length() - 1);
            }
            return param;
        }

        /**
         * Method to show if the key is closed or not. THERE SHOULDN'T BE MORE
         * THAN 1
         *
         * @Param key SHOULD BE ONLY KEY, ONLY WITH PORTS X,Y,M1,M2. Method
         * finds the M1 outside export, checks it for connection and get 2nd
         * port by connection.
         */
        private static boolean isClosedKey(NodeInst ni, NodeInst key) {
            Iterator<PortInst> itrPorts = key.getPortInsts();
            while (itrPorts.hasNext()) {
                PortInst pi = itrPorts.next();
                String port = CommonMethods.parsePortToPort(pi.toString());
                if (port.equals("M1")) {
                    Iterator<Connection> ctnItr = pi.getConnections();
                    Connection ctn = CommonMethods.getOnlyIteratorObject(ctnItr);
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

                    Export outExport = CommonMethods.getOnlyIteratorObject(outPort.getExports());
                    PortInst outsidePort = ni.findPortInstFromEquivalentProto(outExport);

                    if (!CommonMethods.parsePortToPort(outsidePort.toString()).equals("mAd"
                            + CommonMethods.getOnlyParamOfNodeInst(key) + "_1")) {
                        throw new InvalidStructureError("Incorrect block map");
                    }

                    if (!outsidePort.hasConnections()) {
                        return false;
                    }
                    Connection outsideCtn = CommonMethods.getOnlyIteratorObject(outsidePort.getConnections());

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
                            + CommonMethods.getOnlyParamOfNodeInst(key) + "_2")) {
                        //System.out.println("mAd" + getOnlyParamOfNodeInst(key) + " is connected");
                        return true;
                    }
                }
            }
            return false;
        }

        private static String getConfigForKey(NodeInst key, NodeInst ni) {
            String parameterOfBlock = CommonMethods.getOnlyParamOfNodeInst(ni);
            parameterOfBlock = deleteACharacterInTheParam(parameterOfBlock);
            String parameterOfKey = CommonMethods.getOnlyParamOfNodeInst(key);
            return parameterOfBlock + parameterOfKey;
        }
    }

    /**
     * Class holds filter exporting logic.
     */
    private static class FilterConfigExport implements Exportable {

        private StringBuilder filterConfigExport;

        /**
         * Method to get filter config export.
         *
         * @return
         */
        @Override
        public StringBuilder getConfigExport(Cell mainCell) {
            prepareConfigExport(mainCell);
            return filterConfigExport;
        }

        /**
         * Method to get configuration as StringBuilder.
         *
         * @param mainCell
         * @return
         */
        @Override
        public StringBuilder getExistingConfigExport() {
            if (filterConfigExport == null) {
                String s = "Filter config was not created.";
                logger.error(s);
                throw new IllegalStateException(s);
            }
            return filterConfigExport;
        }

        /**
         * Method to get all configuration of this state.
         *
         * @param mainCell
         */
        private void prepareConfigExport(Cell mainCell) {
            //TODO: invoke filter UI and process
            filterConfigExport = getDigitalConfig();
        }

        /**
         * Method to get configuration as StringBuilder.
         *
         * @param mainCell
         * @return
         */
        private StringBuilder getDigitalConfig() {
            String urlFile = LinksHolder.getFilterConfig();
            //We should validate the formation netList.
            String configFiltersExportFile = "-- filter configuration --" + "\n";
            // TODO: StringBuilder
            configFiltersExportFile += getConfigurationFilters(urlFile);
            StringBuilder configFilters = new StringBuilder(configFiltersExportFile);
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
                    informKeyInFileOfFilters += line + "\n";
                }
            } catch (IOException ex) {
                //TODO: exception handle?
            }
            return informKeyInFileOfFilters;
        }
    }

    /**
     * Class holds digital exporting logic.
     */
    private static class DigitalConfigExport implements Exportable {

        private StringBuilder digitalConfigExport;

        /**
         * Main constructor, Object keeps config information of state.
         *
         * @param mainCell
         * @param FPGAnodeInstName
         */
        private DigitalConfigExport() {
        }

        /**
         * Method to get analog config export.
         *
         * @return
         */
        @Override
        public StringBuilder getConfigExport(Cell mainCell) {
            prepareConfigExport(mainCell);
            return digitalConfigExport;
        }

        /**
         * This method reads file at a given address and displays all
         * information without processing.
         */
        @Override
        public StringBuilder getExistingConfigExport() {
            if (digitalConfigExport == null) {
                String s = "Digital config was not created.";
                logger.error(s);
                throw new IllegalStateException(s);
            }
            return digitalConfigExport;
        }

        /**
         * Method to get all configuration of this state.
         */
        private void prepareConfigExport(Cell mainCell) {
            String FPGAnodeInstName = Constants.getFpgaSchemeName();
            doExport(mainCell, FPGAnodeInstName);
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
            if (mainCell == null) {
                logger.info("Main cell was not found");
                return;
            }
            if (!FpgaArgumentsUI.checkForTopArgument()) {
                logger.info("Top argument wasn't set");
                return;
            }
            NodeInst FPGAcell = getFPGACell(mainCell, FPGAnodeInstName);
            String pathToVerilog = LinksHolder.getProjectSimulationPath();
            writeVerilogToFile(FPGAcell, pathToVerilog);
            formDigitalConfig(pathToVerilog);

            digitalConfigExport = getDigitalConfigFromFile();
        }

        /**
         * Method to save current state of digital config.
         */
        public StringBuilder getDigitalConfigFromFile() {
            StringBuilder configFPGA;
            String address = LinksHolder.getPathInFileConfigFPGABlock();
            try (BufferedReader br = new BufferedReader(new FileReader(address))) {
                String informKeyInFileOfFPGA = "-- FPGA configuration --" + '\n';
                String line;
                while ((line = br.readLine()) != null) {
                    informKeyInFileOfFPGA += line + '\n';
                }
                configFPGA = new StringBuilder(informKeyInFileOfFPGA);
            } catch (IOException ex) {
                logger.info("Digital verilog config is not found", ex);
                throw new AssertionError("Missing file in address" + LinksHolder.getPathInFileConfigFPGABlock());
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
                        + " You should use main scheme with FPGA Cell. Otherwise reinstall may help.");
            }
            String[] content = verEquiv.getTextViewContents();
            String verilogFilePath = pathToVerilog + File.separator + "Verilog.v";
            Accessory.cleanFile(verilogFilePath);
            //TODO: write big string once because it's faster.
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
            //form command for XCAD
            String command = shPath + " " + shxaPath + " -i";
            command = addArgument(command, verilogFilePath + " -y++");
            List<String> argumentsList = FpgaArgumentsUI.getArgumentList();
            for (String argument : argumentsList) {
                command = addArgument(command, argument);
            }
            command = addArgument(command, FpgaArgumentsUI.getTopArgument());

            logger.debug(command);

            ExecDialog dialog = new ExecDialog(TopLevel.getCurrentJFrame(), false);
            File dir = new File(pathToVerilog);
            dialog.startProcess(command, null, dir);
            try {
                dialog.waitForProcessToFinish();
            } catch (InterruptedException ex) {
                throw new IllegalStateException("Process was interrupted.");
            }
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

    private static class ExportAllKeys extends Job {

        private final Cell mainCell;

        public ExportAllKeys(Cell mainCell) {
            super("ExportAllKeys", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.mainCell = mainCell;
            startJob();
        }

        public boolean doIt() {
            ExportKeys.formConfig(mainCell);
            return true;
        }
    }

}

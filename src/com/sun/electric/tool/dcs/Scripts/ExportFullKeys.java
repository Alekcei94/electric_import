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
import com.sun.electric.database.topology.Connection;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.variable.Variable;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.CommonMethods;
import com.sun.electric.tool.dcs.Exceptions.FunctionalException;
import com.sun.electric.tool.dcs.Data.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Astepanov
 */
public class ExportFullKeys {

    private static ExportFullKeys exportFullKeys;
    private final Map<String, Map<String, String>> fullColectionAdres = new HashMap<>();

    public static ExportFullKeys getInstance() {
        if (exportFullKeys == null) {
            exportFullKeys = new ExportFullKeys();
        }
        return exportFullKeys;
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
        String urlFile = "../MAP/";
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
            writer.write("-- Scheme configuration --" + "\n");//Made to validate the formation netList.
            Cell curcell = Job.getUserInterface().getCurrentCell();
            Iterator<NodeInst> niItr = curcell.getNodes();
            while (niItr.hasNext()) {
                NodeInst ni = niItr.next();
                if (!ni.getName().contains("pin")) {
                    Iterator<PortInst> itrPorts = ni.getPortInsts();
                    while (itrPorts.hasNext()) {
                        PortInst pi = itrPorts.next();
                        String port = CommonMethods.parsePortToPort(pi.toString());
                        if ((port.contains("mAd")) && (port.contains("_1"))) {
                            Iterator<Connection> ctnItr = pi.getConnections();
                            while (ctnItr.hasNext()) {
                                Connection ctn = ctnItr.next();
                                System.out.println(ni.getName());
                                String adres = getRealKeyInFile(ni, port);
                                //System.out.println(fullColectionAdres.size());
                                if (adres == null) {
                                    Accessory.showMessage("The number of the required key was not found - " + ni.getName());
                                    throw new FunctionalException("The number of the required key was not found ");
                                }
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
    private String getRealKeyInFile(NodeInst ni, String port) {
        Iterator<Variable> paramItr = ni.getParameters();
        while (paramItr.hasNext()) {
            Variable param = paramItr.next();
            String adres = "";
            String[] numberKey;
            if (param.toString().contains("gAdr")) {
                numberKey = port.split("mAd");
                numberKey = numberKey[1].split("_");
                adres = param.getObject().toString() + numberKey[0] + "\n";
                return adres;
            } else if (param.toString().contains("uniq")) {
                Map<String, String> list = new HashMap<>();
                numberKey = port.split("mAd");
                numberKey = numberKey[1].split("_");
                adres = numberKey[0];
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
     * This method finds the file path and real address in the file for the specified key.
     */
    private String readFileMap(Variable key, String keyAdr) {
        String urlFile = key.getObject().toString();
        String[] fullNameBlock;
        String nameBlock = "";
        fullNameBlock = urlFile.split("");
        int endOfBlockNameRecording = 0;
        for (int i = fullNameBlock.length - 1; i >= 0; i--) {
            if (fullNameBlock[i].equals("_")) {
                endOfBlockNameRecording = i;
                break;
            }
        }
        for (int i = 0; i < endOfBlockNameRecording; i++) {
            nameBlock = nameBlock + fullNameBlock[i];
        }
        urlFile = "../MAP/" + nameBlock + "/" + urlFile + ".txt";
        String line;
        String adres;
        try (BufferedReader br = new BufferedReader(new FileReader(urlFile))) {
            String[] adrInFile;
            while ((line = br.readLine()) != null) {
                adrInFile = line.split(" ");
                if (adrInFile[1].equals(keyAdr)) {
                    adres = adrInFile[0];
                    return adres;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}

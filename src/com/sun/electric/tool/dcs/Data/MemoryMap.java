/* Electric(tm) VLSI Design System
 *
 * File: MemoryMap.java
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
package com.sun.electric.tool.dcs.Data;

import com.sun.electric.database.geometry.btree.unboxed.Pair;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.variable.Variable;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.dcs.Accessory;
import com.sun.electric.tool.dcs.Exceptions.InvalidStructureError;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class holds all data related to memory addresses.
 */
public class MemoryMap {

    private static MemoryMap memoryMap;
    /**
     * HashMap<parameter, Pair<import, export>>
     */
    private final HashMap<String, Pair<MemoryFile, MemoryFile>> nameToMemoryAddressMap = new HashMap<>();

    private MemoryMap() {
        buildMap(LinksHolder.getMemoryMapRoot());
    }

    public static MemoryMap getInstance() {
        if (memoryMap == null) {
            memoryMap = new MemoryMap();
        }
        return memoryMap;
    }

    /*
    * This method returns true address in block.
    */
    public String getTrueAddressExport(String parameter, String internalAddress) {
        Pair<MemoryFile, MemoryFile> memoryMapImportAndExport = nameToMemoryAddressMap.get(parameter);
        MemoryFile mf = memoryMapImportAndExport.getValue();
        return mf.getDependency(internalAddress);
    }

    /*
    * This method returns internal address in block.
    */
    public String getInternalAddressImport(String parameter, String trueAddress) {
        Pair<MemoryFile, MemoryFile> memoryMapImportAndExport = nameToMemoryAddressMap.get(parameter);
        if (memoryMapImportAndExport != null) {
            MemoryFile mf = memoryMapImportAndExport.getKey();
            return mf.getDependency(trueAddress);
        } else {
            //System.out.println(" null ");
            return null;
        }
    }

    /**
     * Method to get all memory files from txt files
     */
    private void buildMap(String path) {
        try {
            Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .forEach(this::buildMemoryFile);
            addsAGlobalHashMapInGAdrParameters();
        } catch (IOException ioe) {
            memoryMap = null;
            throw new InvalidStructureError("Your internal map files are corrupted. "
                    + "Reinstall would be solution.");
        }
    }

    /**
     * Method to get all files and transfer them into MemoryFile objects, add
     * dependency as map(key=internalAddress, value=trueAddress).
     *
     * @param path
     * @throws IOException
     */
    private void buildMemoryFile(Path path) {
        MemoryFile mfi = new MemoryFile();
        MemoryFile mfe = new MemoryFile();
        try (InputStream in = Files.newInputStream(path)) {
            try (BufferedReader reader
                    = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] split = line.split(" ");
                    String trueAddress = split[0];
                    String internalAddress = split[1];
                    mfe.addDependency(internalAddress, trueAddress);
                    mfi.addDependency(trueAddress, internalAddress);
                }
            }
        } catch (IOException ioe) {
            memoryMap = null;
            throw new InvalidStructureError("Map is corrupted");
        }
        String nameFile = path.getFileName().toString();
        String[] nameFileArray = nameFile.split(".txt");
        nameToMemoryAddressMap.put(nameFileArray[0], new Pair<>(mfi, mfe));
    }

    /**
     * Adds blocks with gAdr parameters to the map.
     */
    private void addsAGlobalHashMapInGAdrParameters() {
        Cell curcell = Job.getUserInterface().getCurrentCell();
        MemoryFile mfi;
        MemoryFile mfe;
        if (curcell == null) {
            //FIX: давай выкинем HardFunctionalException.
            Accessory.showMessage("No schema selected.");
            return;
        }
        Iterator<NodeInst> iteratorNodeInst = curcell.getNodes();
        while (iteratorNodeInst.hasNext()) {
            NodeInst nodeInstBlock = iteratorNodeInst.next();
            Iterator<Variable> iteratorParameters = nodeInstBlock.getParameters();
            while (iteratorParameters.hasNext()) {
                Variable parameterNode = iteratorParameters.next();
                if (parameterNode.toString().contains("gAdr")) {
                    String parameterKey = parameterNode.getObject().toString();
                    String nameParameters = parameterKey.substring(0, parameterKey.lastIndexOf("h"));
                    mfi = new MemoryFile();
                    mfe = new MemoryFile();
                    Iterator<PortInst> itr = nodeInstBlock.getPortInsts();
                    while (itr.hasNext()) {
                        String nameKey = itr.next().toString();
                        if ((nameKey.contains(ProjectConfiguration.getPinPatternHead())) && (nameKey.contains(ProjectConfiguration.getPinPatternTailFirst()))) {
                            //It was mAd001_1
                            String key = nameKey.substring(nameKey.lastIndexOf(ProjectConfiguration.getPinPatternHead())
                                    + ProjectConfiguration.getPinPatternHead().length(),
                                    nameKey.lastIndexOf(ProjectConfiguration.getPinPatternTailFirst()));
                            //Has become 001
                            String addressKey = nameParameters + key;
                            mfi.addDependency(addressKey, key);
                            mfe.addDependency(key, addressKey);
                        }
                    }
                    nameToMemoryAddressMap.put(nameParameters, new Pair<>(mfi, mfe));
                }
            }
        }
    }

    /**
     * Class holds data of one unique file that consists of dependencies between
     * true address (real address in microscheme) and internal address (address
     * in software from 000 to xxx).
     *
     */
    private class MemoryFile {

        private final HashMap<String, String> dependencyMap = new HashMap<>();

        private MemoryFile() {
        }

        private void addDependency(String internalAddress, String trueAddress) {
            dependencyMap.put(internalAddress, trueAddress);
        }

        public String getDependency(String internalAddress) {
            return dependencyMap.get(internalAddress);
        }
    }

}

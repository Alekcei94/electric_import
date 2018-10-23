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

import com.sun.electric.tool.dcs.Accessory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Class holds all data related to memory addresses.
 *
 * @author diivanov
 */
public class MemoryMap {

    private static MemoryMap memoryMap;

    private final HashMap<String, MemoryFile> nameToMemoryAddressMap = new HashMap<>();

    private MemoryMap() {
        buildMap(LinksHolder.getInstance().getMemoryMapRoot());
    }

    public static MemoryMap getInstance() {
        if (memoryMap == null) {
            memoryMap = new MemoryMap();
        }
        return memoryMap;
    }

    /**
     * Method to get all memory files from txt files
     */
    private void buildMap(String path) {
        try {
            Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            buildMemoryFile(filePath);
                        } catch (IOException ioe) {
                            Accessory.showMessage("Your internal map files are corrupted. "
                                    + "Reinstall would be solution.");
                            ioe.printStackTrace();
                        }
                    });
        } catch (IOException ioe) {
            Accessory.showMessage("Your internal map files are corrupted. "
                    + "Reinstall would be solution.");
            ioe.printStackTrace();
        }
    }

    private void buildMemoryFile(Path path) throws IOException {
        MemoryFile mf = new MemoryFile(path.getFileName().toString());
        System.out.println(mf.getName());
        try (InputStream in = Files.newInputStream(path)) {
            try (BufferedReader reader
                    = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] split = line.split(" ");
                    String trueAddress = split[0];
                    String internalAddress = split[1];
                    mf.addDependency(internalAddress, trueAddress);
                    System.out.println(internalAddress);
                    System.out.println(trueAddress);
                }
            }
        }
        nameToMemoryAddressMap.put(mf.getName(), mf);
    }

    /**
     * Class holds data of one unique file that consists of dependencies between
     * true address (real address in microscheme) and internal address (address
     * in software from 000 to xxx).
     *
     * @author diivanov
     */
    private class MemoryFile {

        private final String name;
        private final HashMap<String, String> dependencyMap = new HashMap<>();

        private MemoryFile(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        private void addDependency(String internalAddress, String trueAddress) {
            dependencyMap.put(internalAddress, trueAddress);
        }

        public String getDependency(String internalAddress) {
            return dependencyMap.get(internalAddress);
        }
    }

}

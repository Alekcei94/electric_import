/* Electric(tm) VLSI Design System
 *
 * File: CommonMethods.java
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
package com.sun.electric.tool.dcs;

import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.hierarchy.Library;
import java.util.Iterator;

/**
 * Class is here to implement some common methods using in autotracing proccess
 * like parsing (Node/Port)Inst into a strings with information about their base
 * name and address or getting all ports that are connected to given point.
 *
 * @author diivanov
 */
public class CommonMethods {

    private CommonMethods() {
        throw new AssertionError();
    }

    /**
     * Method to get Cell if it exists in Library or Null.
     * @param cellName
     * @param libName
     * @return
     */
    public static Cell getCellFromName(String libName, String cellName) {
        Iterator<Library> itrLib = Library.getLibraries();
        while (itrLib.hasNext()) {
            Library lib = itrLib.next();
            if (lib.getName().equals(libName)) {
                Iterator<Cell> itrCell = lib.getCells();
                while (itrCell.hasNext()) {
                    Cell cell = itrCell.next();
                    if (cell.getName().equals(cellName)) {
                        return cell;
                    }
                }
            }
        }
        return null;
    }
    

    /**
     * method implemets parsing of Port String to get Block Name.
     * @param port
     * @return
     */
    public static String parsePortToBlock(String port) {
        assert port != null;
        return port.substring(port.indexOf(":") + 1, port.indexOf("{"));
        // port '5400TP035:ION{ic}[ION<1].ION'
    }

    /**
     * method implemets parsing of Port String to get Port Name.
     * @param port
     * @return
     */
    public static String parsePortToPort(String port) {
        assert port != null;
        return port.substring(port.indexOf(".") + 1, port.lastIndexOf("'")); // name smth like CB<7454
        // port '5400TP035:ION{ic}[ION<1].ION'
    }
}

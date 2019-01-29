/* Electric(tm) VLSI Design System
 *
 * File: BlockMap.java
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

import java.util.regex.Pattern;

/**
 * This class holds information about all possibilities in autotracing system.
 * All allowed blocks is introduced in external file and will be read by
 * BlockMap, then it helps us get patterns for elements. Pattern-general:
 * PAM.*INP1 -> PAM.1bh.INP1 Pattern-certain: PAM\.1bh\.INP1 -> PAM\.1bh\.INP1
 */
public class BlockMap {

    public static class BlockPattern {

        private final Pattern name;
        private final String addr;
        private final Pattern port;

        public BlockPattern(String name, String addr, String port) {
            assert name != null;
            assert port != null;
            this.name = Pattern.compile(name);
            this.addr = addr;
            this.port = Pattern.compile(port);
        }

        /**
         * @return the name
         */
        public Pattern getName() {
            return name;
        }

        /**
         * @return the addr
         */
        public String getAddr() {
            return addr;
        }

        /**
         * @return the port
         */
        public Pattern getPort() {
            return port;
        }
        
        /**
         * Get full pattern.
         * @return 
         */
        @Override
        public String toString() {
            String toReturn = this.name.toString() + Constants.getSplitter()
                    + this.addr + Constants.getSplitter() + this.port.toString();
            return toReturn;
        }

    }
}

/* Electric(tm) VLSI Design System
 *
 * File: Chain.java
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
package com.sun.electric.tool.dcs.autotracing;

import com.sun.electric.tool.dcs.Data.BlockMap.BlockPattern;
import com.sun.electric.tool.dcs.Data.Constants;
import com.sun.electric.tool.dcs.Exceptions.HardFunctionalException;
import com.sun.electric.tool.dcs.Exceptions.InvalidStructureError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author diivanov
 */
public class Chain extends Vertex {

    private final Map<String, ChainElement> chainElements = new HashMap<>();
    private List<ChainElement> connectionChainElementList;
    private final String simplifiedName;

    //private final List<ChainElement> connectionChainElementsList = new ArrayList<>();
    private int weight = 1;
    private boolean isDeleted;

    private static final Logger logger = LoggerFactory.getLogger(Chain.class);

    /**
     * Constructor: parse input String to port elements, adding parse return to
     * ArrayList.
     *
     * @param context
     */
    public Chain(String context) {
        super(context);
        formChainElementsMap(context);
        this.simplifiedName = simplifyName(context);
    }

    /**
     * Constructor: copy Contructor. Parameter isDeleted won't be copied because
     * should be used as local.
     *
     * @param vertex
     */
    public Chain(Chain vertex) {
        super(vertex.getContext());
        formChainElementsMap(vertex.getContext());
        this.weight = vertex.getWeight();
        this.simplifiedName = vertex.getSimplifiedName();
        this.isDeleted = vertex.isDeleted();
    }

    /**
     * Method to create map of chainElements where ChainElement is one string in
     * chain's context.
     *
     * @param context
     */
    private void formChainElementsMap(String context) {
        String[] connectedVertices = context.split(" ");
        for (String element : connectedVertices) {
            ChainElement chainElement = chainElements.get(element);
            if (chainElement != null) {
                throw new InvalidStructureError("Incorrect configuration of graph file");
            }
            chainElement = new ChainElement(element);
            chainElements.put(element, chainElement);
        }
    }

    /**
     * Method to get all chainElements from this chain.
     *
     * @return
     */
    public List<ChainElement> getConnectionChainElementsList() {
        if (connectionChainElementList == null) {
            connectionChainElementList = chainElements.values().stream()
                    .filter(value -> value.isConnectionElement())
                    .collect(Collectors.toList());
        }
        return connectionChainElementList;
    }

    /**
     * Method to improve perfomancy of chains by using cut names
     *
     * @param fullName
     * @return
     */
    private String simplifyName(String fullName) {
        String[] split = fullName.split(" ");
        if (split.length == 0) {
            throw new HardFunctionalException("Incorrect structure, there are some spaces in graph");
        }
        String simplifyName = split[0];
        split = simplifyName.split(Constants.getSplitter());
        if (split.length != 3) {
            throw new HardFunctionalException("Incorrect structure, one of the elements isn't ");
        }
        simplifyName = split[1] + "." + split[2];
        return simplifyName;
    }

    /**
     * Method to show if this chain has certain block. Chain fits pattern if
     * anyone of ChainElements fit it.
     *
     * @param pattern
     * @return
     */
    public boolean fitsPattern(BlockPattern pattern) {
        return chainElements.values().stream().anyMatch((elem) -> (elem.fitsPattern(pattern)));
    }

    /**
     * Method to get port of chainElement with current name.addr. Null safe.
     *
     * @param nameAndAddr
     * @return
     */
    public String getPortOfChainElement(String nameAndAddr) {
        if (nameAndAddr == null) {
            throw new HardFunctionalException("Null in structure.");
        }
        List<ChainElement> elemList = getConnectionChainElementsList();
        for (ChainElement elem : elemList) {
            if (elem.getNameAndAddr().equals(nameAndAddr)) {
                return elem.getPort();
            }
        }
        throw new HardFunctionalException("No elements with context");
    }

    /**
     * Not sure if it will be used
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.getContext());
        return hash;
    }

    /**
     * Not sure if it will be used.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (!(o instanceof Chain)) {
            return false;
        } else if (!((Chain) o).getContext().equals(this.getContext())) {
            return false;
        }
        return true;
    }

    /**
     * @return the simplifiedName
     */
    public String getSimplifiedName() {
        return simplifiedName;
    }

    /**
     * Method to get String line of chain.
     *
     * @return
     */
    public boolean isDeleted() {
        return isDeleted == true;
    }

    /**
     * Method to get String line of chain.
     */
    public void setDeleted() {
        isDeleted = true;
    }

    /**
     * Set weight for cost function.
     *
     * @param newWeight
     */
    public void setWeight(int newWeight) {
        weight = newWeight;
    }

    /**
     * Get weight to count cost function.
     *
     * @return
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Add weight to count cost function.
     */
    public void addWeight() {
        weight += 2;
    }

    /**
     * Class incapsulates parameters of each object inside chain.
     *
     * @Contract: global address is unique.
     */
    public class ChainElement {

        private final String context; // PAM.1bh.INP1 (name.globalAddr.port)

        private final String name;
        private final String globalAddr;
        private final String port;
        private boolean connectionElement;

        private ChainElement(String context) {
            this.context = context;
            String splitter = Constants.getSplitter();
            logger.debug(context);
            String[] split = context.split(splitter);
            if (split.length != 3) {
                throw new InvalidStructureError("Incorrect configuration of graph file");
            }
            this.name = split[0];
            this.globalAddr = split[1];
            this.port = split[2];
            for (String connectionName : Constants.getConnectionElementNames()) {
                if (name.equals(connectionName)) {
                    connectionElement = true;
                    break;
                }
            }
        }

        /**
         * ChainElement fits pattern if: 1) Names are equal or pattern doesn't
         * have name. 2) Addrs are equal or pattern doesn't have addr. 3) Ports
         * are equal or pattern doesn't have port.
         *
         * @param pattern
         * @return
         */
        public boolean fitsPattern(BlockPattern pattern) {
            if ((pattern.getAddr() != null) && (!pattern.getAddr().equals(this.globalAddr))) {
                return false;
            } else if (!pattern.getName().matcher(this.name).matches()) {
                return false;
            } else if (!pattern.getPort().matcher(this.port).matches()) {
                return false;
            }
            return true;
        }

        /**
         * Method to show if this element is connected to chainElement. We
         * consider element as connected if it has same block name and global
         * address.
         *
         * @param chainElement
         * @return
         */
        public boolean isSameBlock(ChainElement chainElement) {
            if (!this.name.equals(chainElement.getName())) {
                return false;
            }
            if (!this.globalAddr.equals(chainElement.getGlobalAddr())) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + Objects.hashCode(this.context);
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            } else if (!(o instanceof ChainElement)) {
                return false;
            } else if (!((ChainElement) o).getContext().equals(getContext())) {
                return false;
            }
            return true;
        }

        /**
         * @return the context
         */
        public String getContext() {
            return context;
        }

        /**
         * Method to get name and addr to use it as unique name.
         *
         * @return
         */
        public String getNameAndAddr() {
            return this.name + "." + this.globalAddr;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the globalAddr
         */
        public String getGlobalAddr() {
            return globalAddr;
        }

        /**
         * @return the port
         */
        public String getPort() {
            return port;
        }

        /**
         * @return the connectionElement
         */
        public boolean isConnectionElement() {
            return connectionElement;
        }

    }
}

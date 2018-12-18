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

import com.sun.electric.tool.dcs.Data.Constants;
import com.sun.electric.tool.dcs.Exceptions.InvalidStructureError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author diivanov
 */
public class Chain extends Vertex {

    private final Map<String, ChainElement> chainElements = new HashMap<>();
    //private final List<ChainElement> connectionChainElementsList = new ArrayList<>();
    private int weight = 1;
    private boolean isDeleted;

    /**
     * Constructor: parse input String to port elements, adding parse return to
     * ArrayList.
     *
     * @param context
     */
    public Chain(String context) {
        super(context);
        formChainElementsMap(context);
    }

    /**
     * Constructor: copy Contructor. Parameter isDeleted won't be copied because
     * should be used as local.
     *
     * @param vertex
     */
    public Chain(Vertex vertex) {
        super(vertex.getContext());
        Chain chain = (Chain) vertex;
        formChainElementsMap(chain.getContext());
        this.weight = chain.getWeight();
    }
    
    public List getConnectionChainElementsList() {
        return chainElements.values().stream()
                .filter(value -> value.isConnectionElement())
                .collect(Collectors.toList());
    }

    private void formChainElementsMap(String vertsFromGlobalGraph) {
        String[] connectedVertices = vertsFromGlobalGraph.split(" ");
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
     * Class incapsulates parameters of each object inside chain.
     * @Contract: global address is unique.
     */
    public class ChainElement {

        private final String context; // PADDR.13676.PX3 (name.globalAddr.port)

        private final String name;
        private final String globalAddr;
        private final String port;

        private final boolean connectionElement;

        private ChainElement(String context) {
            this.context = context;
            String splitter = Constants.getSplitter();
            String[] split = context.split(splitter);
            if (split.length != 3) {
                throw new InvalidStructureError("Incorrect configuration of graph file");
            }
            this.name = split[0];
            this.globalAddr = split[1];
            this.port = split[2];
            connectionElement = this.name.equals(Constants.getConnectionElementName());
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the connectionElement
         */
        public boolean isConnectionElement() {
            return connectionElement;
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
         * Method to check if object relates to same block as this object.
         * Suggesting that global address is unique.
         * @param chainElement
         * @return 
         */
        public boolean isSameBlock(ChainElement chainElement) {
            return chainElement.getGlobalAddr().equals(this.getGlobalAddr()); 
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

    }
}

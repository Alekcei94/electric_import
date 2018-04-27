/* Electric(tm) VLSI Design System
 *
 * File: Pair.java
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

import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;

/**
 *
 * @author diivanov
 * @param <T>
 * @param <V>
 */
public final class Pair<T, V> {

    private T first;
    private V second;

    /**
     *
     * @param first
     * @param second
     */
    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Get First Object with generic type.
     * @return 
     */
    public T getFirstObject() {
        return first;
    }

    /**
     * Get Second Object with generic type.
     * @return 
     */
    public V getSecondObject() {
        return second;
    }

    /**
     * Sometimes you have to use another object instead.
     * @param first
     */
    public void setFirstObject(T first) {
        this.first = first;
    }

    /**
     * Sometimes you have to use another object instead.
     * @param second
     */
    public void setSecondObject(V second) {
        this.second = second;
    }
}

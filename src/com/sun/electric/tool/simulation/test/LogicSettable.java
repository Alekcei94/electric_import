/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: LogicSettable.java
 * Written by Tom O'Neill.
 *
 * Copyright (c) 2004, Static Free Software. All rights reserved.
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
package com.sun.electric.tool.simulation.test;

/**
 * Generic interface for setting logic levels for outputs to chip
 */
public interface LogicSettable {
	
    /**
     * @return Current value for Logic State
     */
    public abstract boolean isLogicStateHigh();
    
    /**
     * Sets logic state to requested value
     * @param logicState New value for logic state
     */
    public abstract void setLogicState(boolean logicState);
}

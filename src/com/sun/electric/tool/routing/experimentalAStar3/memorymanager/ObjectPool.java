/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: ObjectPool.java
 * Written by: Christian Harnisch, Ingo Besenfelder, Michael Neumann (Team 3)
 *
 * Copyright (c) 2010, Static Free Software. All rights reserved.
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
package com.sun.electric.tool.routing.experimentalAStar3.memorymanager;

/**
 * Generic memory pool for objects. It allows to acquire and release objects.
 * Released objects are cached for later use, bypassing Java's garbage
 * collector.
 * 
 * @author Christian Harnisch
 * 
 */
public interface ObjectPool<E>
{
  public E acquire();

  public void release(E object);
}

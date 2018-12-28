/* Electric(tm) VLSI Design System
 *
 * File: HardFunctionalException.java
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
package com.sun.electric.tool.dcs.Exceptions;

import com.sun.electric.tool.dcs.Accessory;

public class HardFunctionalException extends RuntimeException {

    public HardFunctionalException() {
        super();
    }

    public HardFunctionalException(String message) {
        super(message);
        Accessory.showMessage(message);
    }

    public HardFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }

    public HardFunctionalException(Throwable cause) {
        super(cause);
    }
}

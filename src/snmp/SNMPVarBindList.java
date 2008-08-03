/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package snmp;

import java.util.*;


/**
 * The SNMPVarBindList class is a specialization of SNMPSequence that contains a
 * list of SNMPVariablePair objects.
 * <pre>
 * <code>
 *   -- variable bindings
 * 
 *      VarBind ::=
 *          SEQUENCE {
 *                      name
 *                          ObjectName,
 * 
 *                      value
 *                          ObjectSyntax
 *                   }
 * 
 *      VarBindList ::=
 *          SEQUENCE OF
 *              VarBind
 * 
 *      END
 * </code>
 * </pre>
 * 
 * @see snmp.SNMPVariablePair
 */
public class SNMPVarBindList extends SNMPSequence
{
    
    /** 
     *  Creates a new empty variable binding list.
     */
    public SNMPVarBindList()
    {
        super();
    }
    
    
    /**
     *  Returns the variable pairs in the list, separated by spaces.
     */
    public String toString()
    {
        Vector sequence = (Vector)(this.getValue());
        StringBuffer valueStringBuffer = new StringBuffer();
        
        for (Object pair : sequence)
        {
            valueStringBuffer.append(pair.toString());
            valueStringBuffer.append(" ");
        }
        
        return valueStringBuffer.toString();
    }
     
}
/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 * Copyright (C) 2010, Matt Hamilton <matthamilton@live.com>
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

package snmp.datatypes.sequence;


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
 * @see snmp.datatypes.sequence.SnmpVariablePair
 */
public class SnmpVarBindList extends SnmpSequence
{
    
    /** 
     *  Creates a new empty variable binding list.
     */
    public SnmpVarBindList()
    {
        super();
    }
    
    
    /**
     *  Returns the variable pairs in the list, separated by spaces.
     */
    @Override
    public String toString()
    {
        StringBuffer valueString = new StringBuffer();
        
        for (Object pair : sequence)
        {
            valueString.append(pair.toString());
            valueString.append(" ");
        }
        
        return valueString.toString();
    }
     
}
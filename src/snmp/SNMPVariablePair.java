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
 * The SNMPVariablePair class implements the VarBind specification detailed
 * below from <a href="http://www.ietf.org/rfc/rfc1157.txt">RFC 1157</a>. It is a specialization of SNMPSequence, defining a
 * 2-element sequence containing a single (object identifier, value) pair. Note
 * that the values are themselves SNMPObjects.
 * <pre>
 * <code>
 *  -- variable bindings
 * 
 *      VarBind ::=
 *          SEQUENCE {
 *                      name
 *                          ObjectName,
 * 
 *                      value
 *                          ObjectSyntax
 *                   }
 * </code>
 * </pre>
 */
public class SNMPVariablePair extends SNMPSequence
{  
    /**
     * Creates a new variable pair having the supplied object identifier and
     * vale.
     */
    public SNMPVariablePair(SNMPObjectIdentifier objectID, SNMPObject value)
        throws SNMPBadValueException
    {
        super();
        Vector<SNMPObject> contents = new Vector<SNMPObject>();
        contents.add(0, objectID);
        contents.add(1, value);
        this.setValue(contents);
    }
    
    
    /**
     * Gets the variable pair's object identifier.
     *//*
    public SNMPObjectIdentifier getObjectId()
    {
        Vector contents = (Vector)(this.getValue());
        return (SNMPObjectIdentifier)contents.get(0);
    }
    
    *//**
     * Gets the variable pair's SNMPObject value.
     *//*
    public SNMPObject getObjectValue()
    {
        Vector contents = (Vector)(this.getValue());
        return (SNMPObject)contents.get(1);
    }*/
    
}
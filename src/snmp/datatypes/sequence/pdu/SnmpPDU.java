/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 * Copyright (C) 2009, Matt Hamilton <matthamilton@live.com>
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

package snmp.datatypes.sequence.pdu;

import snmp.datatypes.sequence.SnmpSequence;

/**
 * Interface for classes that represent a PDU.
 */
public interface SnmpPDU 
{
    /** 
     *  Extracts the variable binding list from a PDU. Useful for retrieving
     *  the set of (object identifier, value) pairs returned in response to 
     *  a request to an SNMP device. The variable binding list is just an SNMP
     *  sequence containing identifier, value pairs.
     *  @see snmp.datatypes.sequence.SnmpVarBindList
     */
    public SnmpSequence getVarBindList();
}

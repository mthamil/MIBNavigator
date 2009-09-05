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

import snmp.datatypes.SnmpObjectIdentifier;
import snmp.datatypes.SnmpTimeTicks;


/**
 * Interface for recent version SNMP traps.
 */
public interface SnmpTrapPDU
{
	/** 
     *  Extracts the snmpTrapOID from the trap's variable bind 
     *  list (it's the second of the variable pairs).
     */
    public SnmpObjectIdentifier getSNMPTrapOID();
    
    
    /** 
     *  Extracts the sysUptime from the trap's variable bind
     *  list (it's the first of the variable pairs).
     */
    public SnmpTimeTicks getSysUptime();
}

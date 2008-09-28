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


/**
 *  Exception thrown when an attempt to get value of SNMP OID from device fails. Reason could be that 
 *  specified variable not supported by device, or that supplied community name has insufficient
 *  privileges.
 */
public class SNMPGetException extends SNMPRequestException
{
    /**
     *  Creates exception with errorIndex and errorStatus.
     */
    public SNMPGetException(int errorIndex, ErrorStatus errorStatus)
    {
        super(errorIndex, errorStatus);
    }
    
    
    /**
     *  Creates exception with errorIndex, errorStatus and message string.
     */
    public SNMPGetException(String message, int errorIndex, ErrorStatus errorStatus)
    {
        super(message, errorIndex, errorStatus);
    }
    
}
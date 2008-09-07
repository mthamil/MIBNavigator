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
 *  Exception thrown when attempt to set or get value of SNMP OID fails. Reason could be that 
 *  specified variable not supported by device, or that supplied community name has insufficient
 *  privileges. 
 */
public class SNMPException extends Exception
{
	/**
     *  Creates a new exception.
     */
    public SNMPException()
    {
        super();
    }
    
    
    /**
     *  Creates a new exception with message string.
     */
    public SNMPException(String message)
    {
        super(message);
    }
    
    
}
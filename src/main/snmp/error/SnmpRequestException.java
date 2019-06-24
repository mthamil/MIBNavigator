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

package snmp.error;

/**
 *  Exception thrown when request to get or set the value of an SNMP OID on a device fails. Reason could be
 *  that specified variable not supported by device, or that supplied community name has insufficient
 *  privileges. errorStatus parameter allows the reason for the failure to be specified, and errorIndex
 *  allows the index of the failed OID to be specified.
 */
public class SnmpRequestException extends SnmpException
{
    public int errorIndex = 0;
    public ErrorStatus errorStatus = ErrorStatus.NoError;
    
    
    /**
     *  Creates exception with errorIndex, errorStatus
     */
    public SnmpRequestException(int errorIndex, ErrorStatus errorStatus)
    {
        super();
        
        this.errorIndex = errorIndex;
        this.errorStatus = errorStatus;
    }
    
    
    /**
     *  Creates exception with errorIndex, errorStatus, and message string
     */
    public SnmpRequestException(String message, int errorIndex, ErrorStatus errorStatus)
    {
        super(message);
        
        this.errorIndex = errorIndex;
        this.errorStatus = errorStatus;
    }
    
    
//    public static void main(String[] args)
//    {
//        ErrorStatus test = ErrorStatus.Failed;
//        int num = test.ordinal();
//        ErrorStatus test2 = ErrorStatus.getInstance(5);
//    }
    
}
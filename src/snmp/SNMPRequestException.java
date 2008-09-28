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
 *  Exception thrown when request to get or set the value of an SNMP OID on a device fails. Reason could be
 *  that specified variable not supported by device, or that supplied community name has insufficient
 *  privileges. errorStatus parameter allows the reason for the failure to be specified, and errorIndex
 *  allows the index of the failed OID to be specified.
 */
public class SNMPRequestException extends SNMPException
{
    // INTEGER { 
    //    noError(0),
    //    tooBig(1),
    //    noSuchName(2), -- for proxy compatibility 
    //    badValue(3),   -- for proxy compatibility 
    //    readOnly(4),   -- for proxy compatibility 
    //    genErr(5), 
    //    noAccess(6),
    //    wrongType(7), 
    //    wrongLength(8), 
    //    wrongEncoding(9), 
    //    wrongValue(10),
    //    noCreation(11), 
    //    inconsistentValue(12), 
    //    resourceUnavailable(13),
    //    commitFailed(14), 
    //    undoFailed(15), 
    //    authorizationError(16), 
    //    notWritable(17),
    //    inconsistentName(18) 
    // },
    public enum ErrorStatus
    {
        NoError, ValueTooBig, ValueNotAvailable, BadValue, ValueReadOnly, Failed,  // basic errors
        NoAccess, WrongType, WrongLength, WrongEncoding, WrongValue, CreationFailed, 
        InconsistentValue, ResourceUnavailable, CommitFailed, UndoFailed, AuthorizationError,
        NotWritable, InconsistentName;
        
        public static ErrorStatus getInstance(int value)
        {
            if (value > ErrorStatus.values().length)
                throw new IllegalArgumentException("No corresponding instance.");
            
            ErrorStatus[] vals = ErrorStatus.values();
            return vals[value];
        }
    }

    
    public int errorIndex = 0;
    public ErrorStatus errorStatus = ErrorStatus.NoError;
    
    
    /**
     *  Creates exception with errorIndex, errorStatus
     */
    public SNMPRequestException(int errorIndex, ErrorStatus errorStatus)
    {
        super();
        
        this.errorIndex = errorIndex;
        this.errorStatus = errorStatus;
    }
    
    
    /**
     *  Creates exception with errorIndex, errorStatus, and message string
     */
    public SNMPRequestException(String message, int errorIndex, ErrorStatus errorStatus)
    {
        super(message);
        
        this.errorIndex = errorIndex;
        this.errorStatus = errorStatus;
    }
    
    
    public static void main(String[] args)
    {
        ErrorStatus test = ErrorStatus.Failed;
        
        int num = test.ordinal();
        //RequestErrorStatus[] vals = RequestErrorStatus.values();
        //RequestErrorStatus test2 = vals[5];
        ErrorStatus test2 = ErrorStatus.getInstance(5);
    }
    
}
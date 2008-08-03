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
    public enum RequestErrorStatus
    {
        NO_ERROR, VALUE_TOO_BIG, VALUE_NOT_AVAILABLE, BAD_VALUE, VALUE_READ_ONLY, FAILED,  // basic errors
        NO_ACCESS, WRONG_TYPE, WRONG_LENGTH, WRONG_ENCODING, WRONG_VALUE, CREATION_FAILED, 
        INCONSISTENT_VALUE, RESOURCE_UNAVAILABLE, COMMIT_FAILED, UNDO_FAILED, AUTHORIZATION_ERROR,
        NOT_WRITABLE, INCONSISTENT_NAME;
        
        public static RequestErrorStatus getInstance(int value)
        {
            if (value > RequestErrorStatus.values().length)
                throw new IllegalArgumentException("No corresponding instance.");
            
            RequestErrorStatus[] vals = RequestErrorStatus.values();
            return vals[value];
        }
    }
    
    public static final int NO_ERROR = 0;
    public static final int VALUE_TOO_BIG = 1;
    public static final int VALUE_NOT_AVAILABLE = 2;
    public static final int BAD_VALUE = 3;
    public static final int VALUE_READ_ONLY = 4;
    public static final int FAILED = 5;
    
    public int errorIndex = 0;
    public int errorStatus = NO_ERROR;
    
    
    /**
     *  Create exception with errorIndex, errorStatus
     */
    public SNMPRequestException(int errorIndex, int errorStatus)
    {
        super();
        
        this.errorIndex = errorIndex;
        this.errorStatus = errorStatus;
    }
    
    
    /**
     *  Create exception with errorIndex, errorStatus, and message string
     */
    public SNMPRequestException(String message, int errorIndex, int errorStatus)
    {
        super(message);
        
        this.errorIndex = errorIndex;
        this.errorStatus = errorStatus;
    }
    
    
    public static void main(String[] args)
    {
        RequestErrorStatus test = RequestErrorStatus.FAILED;
        
        int num = test.ordinal();
        //RequestErrorStatus[] vals = RequestErrorStatus.values();
        //RequestErrorStatus test2 = vals[5];
        RequestErrorStatus test2 = RequestErrorStatus.getInstance(5);
    }
    
}
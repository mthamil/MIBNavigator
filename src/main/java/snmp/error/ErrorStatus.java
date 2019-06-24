/*
 * SNMP Package
 *
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

package snmp.error;

import java.util.EnumMap;
import java.util.Map;

/**
 * SNMP Error Statuses
 * <pre>
 * INTEGER { 
 *    noError(0),
 *    tooBig(1),
 *    noSuchName(2), -- for proxy compatibility 
 *    badValue(3),   -- for proxy compatibility 
 *    readOnly(4),   -- for proxy compatibility 
 *    genErr(5), 
 *    noAccess(6),
 *    wrongType(7), 
 *    wrongLength(8), 
 *    wrongEncoding(9), 
 *    wrongValue(10),
 *    noCreation(11), 
 *    inconsistentValue(12), 
 *    resourceUnavailable(13),
 *    commitFailed(14), 
 *    undoFailed(15), 
 *    authorizationError(16), 
 *    notWritable(17),
 *    inconsistentName(18) 
 * }
 * </pre>
 */
public enum ErrorStatus
{
	// basic errors
    NoError,
    TooBig,
    NoSuchName,
    BadValue,
    ReadOnly,
    GeneralError,
    
    NoAccess,
    WrongType,
    WrongLength,
    WrongEncoding,
    WrongValue,
    NoCreation,
    InconsistentValue,
    ResourceUnavailable,
    CommitFailed,
    UndoFailed,
    AuthorizationError,
    NotWritable,
    InconsistentName;
    
    /*
        SNMPv1 error-status    SNMPv2 error-status
		===================    ===================
		noError                noError
		tooBig                 tooBig
		noSuchName             noSuchName
		badValue               badValue
		genErr                 genErr
		
		
		SNMPv2 error-status    SNMPv1 error-status
		===================    ===================
		noError                noError
		tooBig                 tooBig
		genErr                 genErr
		
		wrongValue             badValue
		wrongEncoding          badValue
		wrongType              badValue
		wrongLength            badValue
		inconsistentValue      badValue
		
		noAccess               noSuchName
		notWritable            noSuchName
		noCreation             noSuchName
		inconsistentName       noSuchName
		
		resourceUnavailable    genErr
		commitFailed           genErr
		undoFailed             genErr
		authorizationError     noSuchName
     */
    
    /**
     * Maps SNMPv2 to SNMPv1 error statuses.
     */
    private static Map<ErrorStatus, ErrorStatus> v2Tov1ErrorMap = new EnumMap<ErrorStatus, ErrorStatus>(ErrorStatus.class);

    static
    {
    	// Define mapping of SNMPv2 errors to SNMPv1 errors.
    	v2Tov1ErrorMap.put(ErrorStatus.NoError,      ErrorStatus.NoError);
    	v2Tov1ErrorMap.put(ErrorStatus.TooBig,       ErrorStatus.TooBig);
    	v2Tov1ErrorMap.put(ErrorStatus.GeneralError, ErrorStatus.GeneralError);
    	
    	v2Tov1ErrorMap.put(ErrorStatus.WrongValue,        ErrorStatus.BadValue);
    	v2Tov1ErrorMap.put(ErrorStatus.WrongEncoding,     ErrorStatus.BadValue);
    	v2Tov1ErrorMap.put(ErrorStatus.WrongType,         ErrorStatus.BadValue);
    	v2Tov1ErrorMap.put(ErrorStatus.WrongLength,       ErrorStatus.BadValue);
    	v2Tov1ErrorMap.put(ErrorStatus.InconsistentValue, ErrorStatus.BadValue);
    	
    	v2Tov1ErrorMap.put(ErrorStatus.NoAccess,           ErrorStatus.NoSuchName);
    	v2Tov1ErrorMap.put(ErrorStatus.NotWritable,        ErrorStatus.NoSuchName);
    	v2Tov1ErrorMap.put(ErrorStatus.NoCreation,         ErrorStatus.NoSuchName);
    	v2Tov1ErrorMap.put(ErrorStatus.InconsistentName,   ErrorStatus.NoSuchName);
    	v2Tov1ErrorMap.put(ErrorStatus.AuthorizationError, ErrorStatus.NoSuchName);
    	
    	v2Tov1ErrorMap.put(ErrorStatus.ResourceUnavailable, ErrorStatus.GeneralError);
    	v2Tov1ErrorMap.put(ErrorStatus.CommitFailed,        ErrorStatus.GeneralError);
    	v2Tov1ErrorMap.put(ErrorStatus.UndoFailed,          ErrorStatus.GeneralError);
    }
    
    /**
     * Gets the Error Status corresponding to the given integer value.
     * @param value
     * @return
     */
    public static ErrorStatus getInstance(int value)
    {
    	ErrorStatus[] errorStatuses = ErrorStatus.values();
        if (value > errorStatuses.length)
            throw new IllegalArgumentException("No corresponding instance.");
        
        return errorStatuses[value];
    }
    
    /**
     * Gets the SNMPv1 Error corresponding to a SNMPv2 Error.
     * @param snmpV2Error
     * @return
     */
    public static ErrorStatus getSnmpV1Error(ErrorStatus snmpV2Error)
    {
    	return v2Tov1ErrorMap.get(snmpV2Error);
    }
}

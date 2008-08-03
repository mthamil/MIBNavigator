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
 *  SNMPRequestListener is an interface that must be implemented by any class which wishes
 *  to act as a handler for request messages sent from remote SNMP management entities.
 *  The SNMPv1SimpleAgent class listens for request messages, and passes any it receives on to
 *  SNMPRequestListener subclasses that have registered with it through its addRequestListener method.
 */
public interface SNMPRequestListener
{
    
    /**
     *   Handles Get- or Set- request messages. The supplied request PDU may contain multiple OIDs; this
     *   method should process those OIDs it understands, and return an SNMPVarBindList containing those OIDs
     *   which it has handled and their corresponding values. The order of returned OID-value pairs is not
     *   important, as the SNMPv1SimpleAgent will order the information appropriately. Each implementer of
     *   SNMPRequestListener will likely handle only a subset of the list of supplied OIDs; those OIDs which
     *   are not relevant to a particular listener should be ignored, to be handled by another SNMPRequestListener. 
     *   If any OIDs remain unhandled after all listeners' processRequest methods have been called, the 
     *   SNMPv1SimpleAgent will return an appropriate error indication to the management entity.
     *
     *   @throws SNMPGetException, SNMPSetException
     *   If a listener receives a request for an OID which it is intended to handle, but there is a problem with
     *   the request - e.g., a set-request for a value which is read-only, or an incorrect value type for a set - the 
     *   listener should throw an SNMPGetException or SNMPSetException to indicate the error. The exception should 
     *   include both the index of the OID in the list of supplied OIDs, as well as an error status code (status values 
     *   are provided as constants in the SNMPRequestException class definition). The SNMPRequestException class and  
     *   subclasses provide constructors allowing the specification of the error index and status code. Note that the 
     *   error index follows the SNMP convention of starting at 1, not 0: thus if there is a problem with the first OID, 
     *   the error index should be 1. The SNMPAgentInterface will use the information in the exception to communicate 
     *   the error to the requesting management entity. The community name should also be used to determine if a request
     *   is valid for the supplied community name.
     *   
     */
    public SNMPSequence processRequest(SNMPPDU requestPDU, String communityName)
        throws SNMPGetException, SNMPSetException;
        
    
    /**
     *   Handles Get-Next- request messages. The supplied request PDU may contain multiple OIDs; this
     *   method should process those OIDs it understands, and return an SNMPVarBindList containing special
     *   variable pairs indicating those supplied OIDs which it has handled, i.e., it must indicate a
     *   supplied OID, the "next" OID, and the value of this next OID. To do this, the return value is a
     *   sequence of SNMPVariablePairs, in which the first component - the OID - is one of the supplied OIDs,
     *   and the second component - the value - is itself an SNMPVariablePair containing the "next" OID and
     *   its value:
     *
     *       return value = sequence of SNMPVariablePair(original OID, SNMPVariablePair(following OID, value)) 
     *
     *   In this way the SNMPv1SimpleAgent which calls this method will be able to determine which of the 
     *   supplied OIDs each "next" OID corresponds to.
     *
     *   The order of returned "double" OID-(OID-value) pairs is not important, as the SNMPv1SimpleAgent 
     *   will order the information appropriately in the response. Each implementer of
     *   SNMPRequestListener will likely handle only a subset of the list of supplied OIDs; those OIDs which
     *   are not relevant to a particular listener should be ignored, to be handled by another SNMPRequestListener. 
     *   If any OIDs remain unhandled after all listeners' processRequest() methods have been called, the 
     *   SNMPv1SimpleAgent will return an appropriate error indication to the management entity.
     *
     *   @throws SNMPGetException
     *   If a listener receives a request for an OID which it is intended to handle, but there is a problem with
     *   the request - e.g., a get-next request for a value which is not readable for the supplied community name - 
     *   the listener should throw an SNMPGetException to indicate the error. The exception should 
     *   include both the index of the OID in the list of supplied OIDs, as well as an error status code (status values 
     *   are provided as constants in the SNMPRequestException class definition). The SNMPRequestException class and  
     *   subclasses provide constructors allowing the specification of the error index and status code. Note that the 
     *   error index follows the SNMP convention of starting at 1, not 0: thus if there is a problem with the first OID, 
     *   the error index should be 1. The SNMPAgentInterface will use the information in the exception to communicate 
     *   the error to the requesting management entity. The community name should also be used to determine if a request
     *   is valid for the supplied community name.
     *   
     */
    public SNMPSequence processGetNextRequest(SNMPPDU requestPDU, String communityName)
        throws SNMPGetException;
    
}
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

import java.math.BigInteger;
import java.util.Vector;



/**
 * The SNMPv2BulkRequestPDU class represents an SNMPv2 Bulk Request PDU from 
 * <a href="http://www.ietf.org/rfc/rfc1905.txt">RFC 1905</a>, as indicated below. 
 * This forms the payload of an SNMPv2 Bulk Request message.
 * <pre>
 * <code>
 *  -- protocol data units
 * 
 *  3.  Definitions
 * 
 *      SNMPv2-PDU DEFINITIONS ::= BEGIN
 * 
 *          IMPORTS
 *              ObjectName, ObjectSyntax, Integer32
 *                  FROM SNMPv2-SMI;
 * 
 *      -- protocol data units
 * 
 *      PDUs ::=
 *          CHOICE {
 *                  get-request
 *                      GetRequest-PDU,
 * 
 *                  get-next-request
 *                      GetNextRequest-PDU,
 * 
 *                  get-bulk-request
 *                      GetBulkRequest-PDU,
 * 
 *                  response
 *                      Response-PDU,
 * 
 *                  set-request
 *                      SetRequest-PDU,
 * 
 *                  inform-request
 *                      InformRequest-PDU,
 * 
 *                  snmpV2-trap
 *                      SNMPv2-Trap-PDU
 *                 }
 * 
 *      -- PDUs
 * 
 *      GetRequest-PDU ::=
 *          [0]
 *              IMPLICIT PDU
 * 
 *      GetNextRequest-PDU ::=
 *          [1]
 *              IMPLICIT PDU
 * 
 *      Response-PDU ::=
 *          [2]
 *              IMPLICIT PDU
 * 
 *      SetRequest-PDU ::=
 *          [3]
 *              IMPLICIT PDU
 * 
 *       -- [4] is obsolete
 * 
 *      GetBulkRequest-PDU ::=
 *          [5]
 *              IMPLICIT BulkPDU
 * 
 *      InformRequest-PDU ::=
 *          [6]
 *              IMPLICIT PDU
 * 
 *      SNMPv2-Trap-PDU ::=
 *          [7]
 *              IMPLICIT PDU
 * 
 *  
 *      max-bindings
 *          INTEGER ::= 2147483647
 * 
 *  
 *      BulkPDU ::=                     -- MUST be identical in
 *          SEQUENCE {                  -- structure to PDU
 *                      request-id			
 *                          Integer32,
 * 
 *                      non-repeaters		
 *                          INTEGER (0..max-bindings),
 * 
 *                      max-repetitions		
 *                          INTEGER (0..max-bindings),
 * 
 *                      variable-bindings	
 *                          VarBindList   -- values are ignored
 *                   }
 * </code>
 * </pre>
 * @see snmp.SNMPVarBindList 
 * @see snmp.SNMPPDU
 * @see <a href="http://www.ietf.org/rfc/rfc1905.txt">RFC 1905</a>
 */
public class SNMPv2BulkRequestPDU extends SNMPSequence
                                  implements SNMPPDUMarker
{
    
    /**
     * Create a new PDU of the specified type, with given request ID,
     * non-repeaters, and max-repetitions fields, and containing the supplied
     * SNMP sequence as data.
     */
    public SNMPv2BulkRequestPDU(int requestID, int nonRepeaters, int maxRepetitions, SNMPSequence varList)
        throws SNMPBadValueException
    {
        super();
        Vector<SNMPObject> contents = new Vector<SNMPObject>();
        tag = SNMPBERType.SNMPv2_BULK_REQUEST;
        contents.add(0, new SNMPInteger(requestID));
        contents.add(1, new SNMPInteger(nonRepeaters));
        contents.add(2, new SNMPInteger(maxRepetitions));
        contents.add(3, varList);
        this.setValue(contents);
    }
    
    
    /**
     * Create a new PDU of the specified type from the supplied BER encoding.
     * 
     * @throws SNMPBadValueException
     *             Indicates invalid SNMP Bulk PDU encoding supplied in enc.
     */
    protected SNMPv2BulkRequestPDU(byte[] enc, SNMPBERType pduType)
        throws SNMPBadValueException
    {
        tag = pduType;
        extractFromBEREncoding(enc);
        
        // validate the message: make sure we have the appropriate pieces
        Vector contents = (Vector)(this.getValue());
        
        if (contents.size() != 4)
            throw new SNMPBadValueException("Bad Bulk Request PDU");
        
        if (!(contents.get(0) instanceof SNMPInteger))
            throw new SNMPBadValueException("Bad Bulk Request PDU: bad request ID");
        
        if (!(contents.get(1) instanceof SNMPInteger))
            throw new SNMPBadValueException("Bad Bulk Request PDU: bad non-repeaters field");
        
        if (!(contents.get(2) instanceof SNMPInteger))
            throw new SNMPBadValueException("Bad Bulk Request PDU: bad max-repetitions field");
        
        if (!(contents.get(3) instanceof SNMPSequence))
            throw new SNMPBadValueException("Bad Bulk Request PDU: bad variable binding list");
        
        // now validate the variable binding list: should be list of sequences which
        // are (OID, value) pairs
        SNMPSequence varBindList = this.getVarBindList();
        for (int i = 0; i < varBindList.size(); i++)
        {
            SNMPObject element = varBindList.getSNMPObjectAt(i);
            
            // must be a two-element sequence
            if (!(element instanceof SNMPSequence))
                throw new SNMPBadValueException("Bad Bulk Request PDU: bad variable binding at index" + i);
            
            // variable binding sequence must have 2 elements, first of which must be an object identifier
            SNMPSequence varBind = (SNMPSequence)element;
            if ((varBind.size() != 2) || !(varBind.getSNMPObjectAt(0) instanceof SNMPObjectIdentifier))
                throw new SNMPBadValueException("Bad Bulk Request PDU: bad variable binding at index" + i);
        }

    }
    
    
    /** 
     *  A utility method that extracts the variable binding list from the pdu. Useful for retrieving
     *  the set of (object identifier, value) pairs returned in response to a request to an SNMP
     *  device. The variable binding list is just an SNMP sequence containing the identifier, value pairs.
     *  @see snmp.SNMPVarBindList
     */
    public SNMPSequence getVarBindList()
    {
        Vector contents = (Vector)(this.getValue());
        return (SNMPSequence)(contents.get(3));
    }
    
    
    /** 
     *  A utility method that extracts the request ID number from this PDU.
     */
    public int getRequestID()
    {
        Vector contents = (Vector)(this.getValue());
        return ((BigInteger)((SNMPInteger)(contents.get(0))).getValue()).intValue();
    }
    
    
    /** 
     *  A utility method that extracts the non-repeaters field for this PDU.
     */
    public int getNonRepeaters()
    {
        Vector contents = (Vector)(this.getValue());
        return ((BigInteger)((SNMPInteger)(contents.get(1))).getValue()).intValue();
    }
    
    
    /** 
     *  A utility method that returns the max-repetitions field for this PDU.
     */
    public int getMaxRepetitions()
    {
        Vector contents = (Vector)(this.getValue());
        return ((BigInteger)((SNMPInteger)(contents.get(2))).getValue()).intValue();
    }
    

    /** 
     *  A utility method that returns the PDU type of this PDU.
     */
    public SNMPBERType getPDUType()
    {
        return tag;
    }
    
}
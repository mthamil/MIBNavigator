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

import java.util.*;
import java.math.*;



/**
 * The SNMPTrapPDU class represents an SNMPv1 Trap PDU from <a href="http://www.ietf.org/rfc/rfc1157.txt">RFC 1157</a>, as
 * indicated below. This forms the payload of an SNMP Trap message. *
 * <pre>
 * <code>
 *   -- protocol data units
 *   
 *   PDUs ::=
 *       CHOICE {
 *                   get-request
 *                       GetRequest-PDU,
 *   
 *                   get-next-request
 *                       GetNextRequest-PDU,
 *   
 *                   get-response
 *                       GetResponse-PDU,
 *   
 *                   set-request
 *                       SetRequest-PDU,
 *   
 *                   trap
 *                       Trap-PDU
 *              }
 *   
 *  -- PDUs
 *   
 *       GetRequest-PDU ::=
 *           [0]
 *               IMPLICIT PDU
 *   
 *       GetNextRequest-PDU ::=
 *           [1]
 *               IMPLICIT PDU
 *   
 *       GetResponse-PDU ::=
 *           [2]
 *               IMPLICIT PDU
 *   
 *       SetRequest-PDU ::=
 *           [3]
 *               IMPLICIT PDU
 *             
 *       
 *       Trap-PDU ::=
 *           [4]
 *               IMPLICIT SEQUENCE {
 *                                  enterprise              -- type of object generating
 *                                      OBJECT IDENTIFIER,  -- trap, see sysObjectID in [5]
 * 
 *                                  agent-addr              -- address of object generating
 *                                      NetworkAddress,     -- trap
 * 
 *                                  generic-trap            -- generic trap type
 *                                      INTEGER {
 *                                                  coldStart(0),
 *                                                  warmStart(1),
 *                                                  linkDown(2),
 *                                                  linkUp(3),
 *                                                  authenticationFailure(4),
 *                                                  egpNeighborLoss(5),
 *                                                  enterpriseSpecific(6)
 *                                              },
 * 
 *                                  specific-trap  -- specific code, present even
 *                                      INTEGER,   -- if generic-trap is not
 *                                                 -- enterpriseSpecific
 * 
 *                                  time-stamp     -- time elapsed between the last
 *                                      TimeTicks, -- (re)initialization of the network
 *                                                 -- entity and the generation of the trap
 * 
 *                                  variable-bindings -- &quot;interesting&quot; information
 *                                      VarBindList
 *                                }
 * </code>
 * </pre>
 * @see snmp.SNMPVarBindList 
 * @see snmp.SNMPPDU
 * @see <a href="http://www.ietf.org/rfc/rfc1157.txt">RFC 1157</a>
 */
public class SNMPv1TrapPDU extends SNMPSequence
                           implements SNMPPDUMarker
{
    
    /**
     * Create a new Trap PDU of the specified type, with given request ID, error
     * status, and error index, and containing the supplied SNMP sequence as
     * data.
     */
    public SNMPv1TrapPDU(SNMPObjectIdentifier enterpriseOID, SNMPIPAddress agentAddress, int genericTrap, int specificTrap, SNMPTimeTicks timestamp, SNMPSequence varList)
        throws SNMPBadValueException
    {
        super();
        
        tag = SNMPBERType.SNMP_TRAP;
        
        Vector<SNMPObject> contents = new Vector<SNMPObject>();
        
        contents.add(enterpriseOID);
        contents.add(agentAddress);
        contents.add(new SNMPInteger(genericTrap));
        contents.add(new SNMPInteger(specificTrap));
        contents.add(timestamp);
        contents.add(varList);
        
        this.setValue(contents);
    }
    
    
    /**
     *  Create a new Trap PDU of the specified type, with given request ID, error status, and error index,
     *  and containing an empty SNMP sequence (VarBindList) as additional data.
     */
    public SNMPv1TrapPDU(SNMPObjectIdentifier enterpriseOID, SNMPIPAddress agentAddress, int genericTrap, int specificTrap, SNMPTimeTicks timestamp)
        throws SNMPBadValueException
    {
        super();
        
        tag = SNMPBERType.SNMP_TRAP;
        
        Vector<SNMPObject> contents = new Vector<SNMPObject>();
        
        contents.add(enterpriseOID);
        contents.add(agentAddress);
        contents.add(new SNMPInteger(genericTrap));
        contents.add(new SNMPInteger(specificTrap));
        contents.add(timestamp);
        contents.add(new SNMPVarBindList());
        
        this.setValue(contents);
    }
    
    
    /**
     *  Create a new PDU of the specified type from the supplied BER encoding.
     *  
     *  @throws SNMPBadValueException Indicates invalid SNMP PDU encoding supplied in enc.
     */
    protected SNMPv1TrapPDU(byte[] enc)
        throws SNMPBadValueException
    {
        tag = SNMPBERType.SNMP_TRAP;
        extractFromBEREncoding(enc);
        
        // validate the message: make sure we have the appropriate pieces
        Vector contents = (Vector)(this.getValue());
        
        if (contents.size() != 6)
            throw new SNMPBadValueException("Bad Trap PDU");
        
        if (!(contents.get(0) instanceof SNMPObjectIdentifier))
            throw new SNMPBadValueException("Bad Trap PDU: bad enterprise OID");
        
        if (!(contents.get(1) instanceof SNMPIPAddress))
            throw new SNMPBadValueException("Bad Trap PDU: bad agent address");
        
        if (!(contents.get(2) instanceof SNMPInteger))
            throw new SNMPBadValueException("Bad Trap PDU: bad generic trap code");
        
        if (!(contents.get(3) instanceof SNMPInteger))
            throw new SNMPBadValueException("Bad Trap PDU: bad specific trap code");
        
        if (!(contents.get(4) instanceof SNMPTimeTicks))
            throw new SNMPBadValueException("Bad Trap PDU: bad timestamp");
        
        if (!(contents.get(5) instanceof SNMPSequence))
            throw new SNMPBadValueException("Bad Trap PDU: bad variable binding list");
        
        // now validate the variable binding list: should be list of sequences which
        // are (OID, value) pairs
        SNMPSequence varBindList = this.getVarBindList();
        for (int i = 0; i < varBindList.size(); i++)
        {
            SNMPObject element = varBindList.getSNMPObjectAt(i);
            
            // must be a two-element sequence
            if (!(element instanceof SNMPSequence))
                throw new SNMPBadValueException("Bad Trap PDU: bad variable binding at index" + i);
            
            // variable binding sequence must have 2 elements, first of which must be an object identifier
            SNMPSequence varBind = (SNMPSequence)element;
            if ((varBind.size() != 2) || !(varBind.getSNMPObjectAt(0) instanceof SNMPObjectIdentifier))
                throw new SNMPBadValueException("Bad Trap PDU: bad variable binding at index" + i);
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
        return (SNMPSequence)(contents.get(5));
    }
    
    
    /** 
     *  A utility method that extracts the enterprise OID from this PDU.
     */
    public SNMPObjectIdentifier getEnterpriseOID()
    {
        Vector contents = (Vector)(this.getValue());
        return (SNMPObjectIdentifier)contents.get(0);
    }
    
    
    /** 
     *  A utility method that extracts the sending agent address this PDU.
     */
    public SNMPIPAddress getAgentAddress()
    {
        Vector contents = (Vector)(this.getValue());
        return (SNMPIPAddress)contents.get(1);
    }
    
    
    /** 
     *  A utility method that returns the generic trap code for this PDU.
     */
    public int getGenericTrap()
    {
        Vector contents = (Vector)(this.getValue());
        return ((BigInteger)((SNMPInteger)(contents.get(2))).getValue()).intValue();
    }
    
    
    /** 
     *  A utility method that returns the specific trap code for this PDU.
     */
    public int getSpecificTrap()
    {
        Vector contents = (Vector)(this.getValue());
        return ((BigInteger)((SNMPInteger)(contents.get(3))).getValue()).intValue();
    }
    
    
    /** 
     *  A utility method that returns the timestamp for this PDU.
     */
    public long getTimestamp()
    {
        Vector contents = (Vector)(this.getValue());
        return ((BigInteger)((SNMPTimeTicks)(contents.get(4))).getValue()).longValue();
    }
    
}
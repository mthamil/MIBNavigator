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
 * @see snmp.SnmpVarBindList 
 * @see snmp.SnmpPDU
 * @see <a href="http://www.ietf.org/rfc/rfc1157.txt">RFC 1157</a>
 */
public class SnmpV1TrapPDU extends SnmpSequence
                           implements SnmpPDUMarker
{
	
	/**
	 * Predefined generic trap codes.
	 */
	public enum GenericTrapType 
	{ 
		ColdStart, WarmStart, LinkDown, LinkUp, AuthenticationFailure, NeighborLoss, Enterprise;
		
		public static GenericTrapType getInstance(int value)
		{
		    if (value > GenericTrapType.values().length)
		        throw new IllegalArgumentException("No corresponding instance.");
		    
		    GenericTrapType[] vals = GenericTrapType.values();
		    return vals[value];
		}
	
	}
    
    /**
     * Creates a new Trap PDU of the specified type, with given request ID, error
     * status, and error index, and containing the supplied SNMP sequence as
     * data.
     */
    public SnmpV1TrapPDU(SnmpObjectIdentifier enterpriseOID, SnmpIpAddress agentAddress, GenericTrapType trapType, 
    	int specificTrap, SnmpTimeTicks timestamp, SnmpSequence varList) throws SnmpBadValueException
    {
        super();
        
        tag = SnmpBERType.SnmpTrap;
        
        List<SnmpObject> contents = new Vector<SnmpObject>();
        
        contents.add(enterpriseOID);
        contents.add(agentAddress);
        contents.add(new SnmpInteger(trapType.ordinal()));
        contents.add(new SnmpInteger(specificTrap));
        contents.add(timestamp);
        contents.add(varList);
        
        this.setValue(contents);
    }
    
    
    /**
     *  Creates a new Trap PDU of the specified type, with given request ID, error status, and error index,
     *  and containing an empty SNMP sequence (VarBindList) as additional data.
     */
    public SnmpV1TrapPDU(SnmpObjectIdentifier enterpriseOID, SnmpIpAddress agentAddress, GenericTrapType trapType, 
    	int specificTrap, SnmpTimeTicks timestamp)  throws SnmpBadValueException
    {
        this(enterpriseOID, agentAddress, trapType, specificTrap, timestamp, new SnmpVarBindList());
    }
    
    
    /**
     *  Creates a new PDU of the specified type from the supplied BER encoding.
     *  
     *  @throws SnmpBadValueException Indicates invalid SNMP PDU encoding supplied in enc.
     */
    protected SnmpV1TrapPDU(byte[] enc) throws SnmpBadValueException
    {
        tag = SnmpBERType.SnmpTrap;
        decode(enc);
        
        // validate the message: make sure we have the appropriate pieces
        List<SnmpObject> contents = sequence;
        
        if (contents.size() != 6)
            throw new SnmpBadValueException("Bad Trap PDU");
        
        if (!(contents.get(0) instanceof SnmpObjectIdentifier))
            throw new SnmpBadValueException("Bad Trap PDU: bad enterprise OID");
        
        if (!(contents.get(1) instanceof SnmpIpAddress))
            throw new SnmpBadValueException("Bad Trap PDU: bad agent address");
        
        if (!(contents.get(2) instanceof SnmpInteger))
            throw new SnmpBadValueException("Bad Trap PDU: bad generic trap code");
        
        if (!(contents.get(3) instanceof SnmpInteger))
            throw new SnmpBadValueException("Bad Trap PDU: bad specific trap code");
        
        if (!(contents.get(4) instanceof SnmpTimeTicks))
            throw new SnmpBadValueException("Bad Trap PDU: bad timestamp");
        
        if (!(contents.get(5) instanceof SnmpSequence))
            throw new SnmpBadValueException("Bad Trap PDU: bad variable binding list");
        
        // now validate the variable binding list: should be list of sequences which
        // are (OID, value) pairs
        SnmpSequence varBindList = this.getVarBindList();
        for (int i = 0; i < varBindList.size(); i++)
        {
            SnmpObject element = varBindList.getSNMPObjectAt(i);
            
            // must be a two-element sequence
            if (!(element instanceof SnmpSequence))
                throw new SnmpBadValueException("Bad Trap PDU: bad variable binding at index" + i);
            
            // variable binding sequence must have 2 elements, first of which must be an object identifier
            SnmpSequence varBind = (SnmpSequence)element;
            if ((varBind.size() != 2) || !(varBind.getSNMPObjectAt(0) instanceof SnmpObjectIdentifier))
                throw new SnmpBadValueException("Bad Trap PDU: bad variable binding at index" + i);
        }
    }
    
    
    /** 
     *  @see snmp.SnmpPDUMarker#getVarBindList
     */
    public SnmpSequence getVarBindList()
    {
        return (SnmpSequence)(sequence.get(5));
    }
    
    
    /** 
     *  Extracts the enterprise OID from this PDU.
     */
    public SnmpObjectIdentifier getEnterpriseOID()
    {
        return (SnmpObjectIdentifier)sequence.get(0);
    }
    
    
    /** 
     *  Extracts the sending agent address this PDU.
     */
    public SnmpIpAddress getAgentAddress()
    {
        return (SnmpIpAddress)sequence.get(1);
    }
    
    
    /** 
     *  Returns the generic trap code for this PDU.
     */
    public GenericTrapType getGenericTrap()
    {
    	SnmpInteger value = (SnmpInteger)sequence.get(2);
    	int ordinal = ((BigInteger)value.getValue()).intValue();
    	return GenericTrapType.getInstance(ordinal);
    }
    
    
    /** 
     *  Returns the specific trap code for this PDU.
     */
    public int getSpecificTrap()
    {
        return ((BigInteger)((SnmpInteger)(sequence.get(3))).getValue()).intValue();
    }
    
    
    /** 
     *  Returns the timestamp for this PDU.
     */
    public long getTimestamp()
    {
        return ((BigInteger)((SnmpTimeTicks)(sequence.get(4))).getValue()).longValue();
    }
    
}
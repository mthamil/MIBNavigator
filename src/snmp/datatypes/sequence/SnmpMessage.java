/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
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

package snmp.datatypes.sequence;

import java.util.*;

import snmp.SnmpVersion;
import snmp.datatypes.SnmpInteger;
import snmp.datatypes.SnmpObject;
import snmp.datatypes.SnmpOctetString;
import snmp.datatypes.sequence.pdu.SnmpBasicPDU;
import snmp.datatypes.sequence.pdu.SnmpV1TrapPDU;
import snmp.datatypes.sequence.pdu.SnmpV2TrapPDU;
import snmp.error.SnmpBadValueException;


/**
 * Defines the SNMPMessage class as a special case of SNMPSequence. Defines a
 * top-level SNMP message, as per the following definitions from <a href="http://www.ietf.org/rfc/rfc1157.txt">RFC 1157</a> and
 * <a href="http://www.ietf.org/rfc/rfc1901.txt">RFC 1901</a>.
 * <pre>
 * <code>
 *      RFC1157-SNMP DEFINITIONS
 * 
 *          IMPORTS FROM RFC1155-SMI;
 * 
 *          -- top-level message
 * 
 *          Message ::=
 *              SEQUENCE {
 *                         version         -- version-1 for this RFC
 *                              INTEGER {
 *                                          version-1(0)
 *                                      },
 * 
 *                          community      -- community name
 *                              OCTET STRING,
 * 
 *                          data           -- e.g., PDUs if trivial
 *                              ANY        -- authentication is being used
 *                       }
 *  
 *  
 *      -- From RFC 1901:
 *  
 *      COMMUNITY-BASED-SNMPv2 DEFINITIONS ::= BEGIN
 * 
 *          -- top-level message
 * 
 *          Message ::=
 *              SEQUENCE {
 *                          version
 *                              INTEGER {
 *                                          version(1)  -- modified from RFC 1157
 *                                      },
 * 
 *                          community           -- community name
 *                              OCTET STRING,
 * 
 *                          data                -- PDUs as defined in [4]
 *                              ANY
 *                       }
 * </code>
 * </pre>
 */
public class SnmpMessage extends SnmpSequence
{
    
    /**
     * Creates an SNMP message with specified version, community, and PDU. Use
     * SNMPv2 for enhanced capabilities provided through RFC 1157.
     */
    public SnmpMessage(SnmpVersion version, String community, SnmpBasicPDU pdu)
    {
        super();
        List<SnmpObject> contents = new Vector<SnmpObject>();
        contents.add(0, new SnmpInteger(version.ordinal()));
        contents.add(1, new SnmpOctetString(community));
        contents.add(2, pdu);
        
        try
        {
            this.setValue(contents);
        }
        catch (SnmpBadValueException e)
        {
            // can't happen! all supplied Vector elements are SNMP Object subclasses
        }
    }
    

    /**
     *  Creates an SNMP message with specified version, community, and trap PDU.
     *  Use SNMPv2 for enhanced capabilities provided through RFC 1157.
     */
    public SnmpMessage(SnmpVersion version, String community, SnmpV1TrapPDU pdu)
    {
        super();
        List<SnmpObject> contents = new Vector<SnmpObject>();
        contents.add(0, new SnmpInteger(version.ordinal()));
        contents.add(1, new SnmpOctetString(community));
        contents.add(2, pdu);
        
        try
        {
            this.setValue(contents);
        }
        catch (SnmpBadValueException e)
        {
            // can't happen! all supplied Vector elements are SNMP Object subclasses
        }
    }
    
    
    /**
     *  Creates an SNMP message with specified version, community, and v2 trap pdu.
     *  Use SNMPv2.
     */
    public SnmpMessage(SnmpVersion version, String community, SnmpV2TrapPDU pdu)
    {
        super();
        List<SnmpObject> contents = new Vector<SnmpObject>();
        contents.add(0, new SnmpInteger(version.ordinal()));
        contents.add(1, new SnmpOctetString(community));
        contents.add(2, pdu);
        
        try
        {
            this.setValue(contents);
        }
        catch (SnmpBadValueException e)
        {
            // can't happen! all supplied Vector elements are SNMP Object subclasses
        }
    }
    
    
    /**
     *  Constructs an SNMPMessage from a received ASN.1 byte representation.
     *  
     *  @throws SnmpBadValueException Indicates invalid SNMP message encoding supplied.
     */
    public SnmpMessage(byte[] encoding)
        throws SnmpBadValueException
    {
        super(encoding);
        
        // validate the message: make sure we have the appropriate pieces
        List<SnmpObject> contents = sequence;
        
        if (contents.size() != 3)
            throw new SnmpBadValueException("Bad SNMP message");
        
        if (!(contents.get(0) instanceof SnmpInteger))
            throw new SnmpBadValueException("Bad SNMP message: bad version");
        
        if (!(contents.get(1) instanceof SnmpOctetString))
            throw new SnmpBadValueException("Bad SNMP message: bad community name");
        
        if (!(contents.get(2) instanceof SnmpBasicPDU) && !(contents.get(2) instanceof SnmpV1TrapPDU) 
                && !(contents.get(2) instanceof SnmpV2TrapPDU))
            throw new SnmpBadValueException("Bad SNMP message: bad PDU");
        
    }
    
    
    /** 
     *  Returns the PDU contained in the SNMP message as a plain Java Object. 
     *  The PDU is the third component of the sequence, after the version and community name.
     */
    public Object getPDUAsObject() throws SnmpBadValueException
    {
        Object pdu = sequence.get(2);
        return pdu;
    }
    
    
    /** 
     *  Returns the PDU contained in the SNMP message. The PDU is the third component
     *  of the sequence, after the version and community name.
     */
    public SnmpBasicPDU getPDU() throws SnmpBadValueException
    {
        Object pdu = sequence.get(2);
        
        if (!(pdu instanceof SnmpBasicPDU))
            throw new SnmpBadValueException("Wrong PDU type in message: expected SNMPPDU, have " + pdu.getClass().toString());
        
        return (SnmpBasicPDU)pdu;
    }
    
    
    /** 
     *  Returns the PDU contained in the SNMP message as an SNMPv1TrapPDU. The PDU is the 
     *  third component of the sequence, after the version and community name.
     */
    public SnmpV1TrapPDU getv1TrapPDU() throws SnmpBadValueException
    {
        Object pdu = sequence.get(2);
        
        if (!(pdu instanceof SnmpV1TrapPDU))
            throw new SnmpBadValueException("Wrong PDU type in message: expected SNMPTrapPDU, have " + pdu.getClass().toString());
        
        return (SnmpV1TrapPDU)pdu;
    }
    
    
    /** 
     *  Returns the PDU contained in the SNMP message as an SNMPv2TrapPDU. The PDU is the 
     *  third component of the sequence, after the version and community name.
     */
    public SnmpV2TrapPDU getv2TrapPDU() throws SnmpBadValueException
    {
        Object pdu = sequence.get(2);
        
        if (!(pdu instanceof SnmpV2TrapPDU))
            throw new SnmpBadValueException("Wrong PDU type in message: expected SNMPv2TrapPDU, have " + pdu.getClass().toString());
        
        return (SnmpV2TrapPDU)pdu;
    }
    
    
    
    /** 
     *  Returns the community name contained in the SNMP message. The community name is the 
     *  second component of the sequence, after the version.
     */
    public String getCommunityName() throws SnmpBadValueException
    {
        Object communityName = sequence.get(1);
        
        if (!(communityName instanceof SnmpOctetString))
            throw new SnmpBadValueException("Wrong SNMP type for community name in message: expected SNMPOctetString, have " 
                    + communityName.getClass().toString());
        
        return ((SnmpOctetString)communityName).toString();
    }
        
}
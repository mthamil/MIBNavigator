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
public class SNMPMessage extends SNMPSequence
{
    
    /**
     * Create an SNMP message with specified version, community, and pdu. Use
     * version = 0 for SNMP version 1, or version = 1 for enhanced capapbilities
     * provided through RFC 1157.
     */
    public SNMPMessage(int version, String community, SNMPPDU pdu)
    {
        super();
        Vector<SNMPObject> contents = new Vector<SNMPObject>();
        contents.add(0, new SNMPInteger(version));
        contents.add(1, new SNMPOctetString(community));
        contents.add(2, pdu);
        
        try
        {
            this.setValue(contents);
        }
        catch (SNMPBadValueException e)
        {
            // can't happen! all supplied Vector elements are SNMP Object subclasses
        }
    }
    

    /**
     *  Create an SNMP message with specified version, community, and trap pdu.
     *  Use version = 0 for SNMP version 1, or version = 1 for enhanced capapbilities
     *  provided through RFC 1157.
     */
    public SNMPMessage(int version, String community, SNMPv1TrapPDU pdu)
    {
        super();
        Vector<SNMPObject> contents = new Vector<SNMPObject>();
        contents.add(0, new SNMPInteger(version));
        contents.add(1, new SNMPOctetString(community));
        contents.add(2, pdu);
        
        try
        {
            this.setValue(contents);
        }
        catch (SNMPBadValueException e)
        {
            // can't happen! all supplied Vector elements are SNMP Object subclasses
        }
    }
    
    
    /**
     *  Create an SNMP message with specified version, community, and v2 trap pdu.
     *  Use version = 1.
     */
    public SNMPMessage(int version, String community, SNMPv2TrapPDU pdu)
    {
        super();
        Vector<SNMPObject> contents = new Vector<SNMPObject>();
        contents.add(0, new SNMPInteger(version));
        contents.add(1, new SNMPOctetString(community));
        contents.add(2, pdu);
        
        try
        {
            this.setValue(contents);
        }
        catch (SNMPBadValueException e)
        {
            // can't happen! all supplied Vector elements are SNMP Object subclasses
        }
    }
    
    
    /**
     *  Construct an SNMPMessage from a received ASN.1 byte representation.
     *  
     *  @throws SNMPBadValueException Indicates invalid SNMP message encoding supplied.
     */
    protected SNMPMessage(byte[] enc)
        throws SNMPBadValueException
    {
        super(enc);
        
        // validate the message: make sure we have the appropriate pieces
        Vector contents = (Vector)(this.getValue());
        
        if (contents.size() != 3)
            throw new SNMPBadValueException("Bad SNMP message");
        
        if (!(contents.get(0) instanceof SNMPInteger))
            throw new SNMPBadValueException("Bad SNMP message: bad version");
        
        if (!(contents.get(1) instanceof SNMPOctetString))
            throw new SNMPBadValueException("Bad SNMP message: bad community name");
        
        if (!(contents.get(2) instanceof SNMPPDU) && !(contents.get(2) instanceof SNMPv1TrapPDU) 
                && !(contents.get(2) instanceof SNMPv2TrapPDU))
            throw new SNMPBadValueException("Bad SNMP message: bad PDU");
        
    }
    
    
    /** 
     *  Utility method which returns the PDU contained in the SNMP message as a plain Java Object. 
     *  The pdu is the third component of the sequence, after the version and community name.
     */
    public Object getPDUAsObject()
        throws SNMPBadValueException
    {
        Vector contents = (Vector)(this.getValue());
        Object pdu = contents.get(2);
        return pdu;
    }
    
    
    /** 
     *  Utility method which returns the PDU contained in the SNMP message. The pdu is the third component
     *  of the sequence, after the version and community name.
     */
    public SNMPPDU getPDU()
        throws SNMPBadValueException
    {
        Vector contents = (Vector)(this.getValue());
        Object pdu = contents.get(2);
        
        if (!(pdu instanceof SNMPPDU))
            throw new SNMPBadValueException("Wrong PDU type in message: expected SNMPPDU, have " + pdu.getClass().toString());
        
        return (SNMPPDU)pdu;
    }
    
    
    /** 
     *  Utility method which returns the PDU contained in the SNMP message as an SNMPv1TrapPDU. The pdu is the 
     *  third component of the sequence, after the version and community name.
     */
    public SNMPv1TrapPDU getv1TrapPDU()
        throws SNMPBadValueException
    {
        Vector contents = (Vector)(this.getValue());
        Object pdu = contents.get(2);
        
        if (!(pdu instanceof SNMPv1TrapPDU))
            throw new SNMPBadValueException("Wrong PDU type in message: expected SNMPTrapPDU, have " + pdu.getClass().toString());
        
        return (SNMPv1TrapPDU)pdu;
    }
    
    
    /** 
     *  Utility method which returns the PDU contained in the SNMP message as an SNMPv2TrapPDU. The pdu is the 
     *  third component of the sequence, after the version and community name.
     */
    public SNMPv2TrapPDU getv2TrapPDU()
        throws SNMPBadValueException
    {
        Vector contents = (Vector)(this.getValue());
        Object pdu = contents.get(2);
        
        if (!(pdu instanceof SNMPv2TrapPDU))
            throw new SNMPBadValueException("Wrong PDU type in message: expected SNMPv2TrapPDU, have " + pdu.getClass().toString());
        
        return (SNMPv2TrapPDU)pdu;
    }
    
    
    
    /** 
     *  Utility method which returns the community name contained in the SNMP message. The community name is the 
     *  second component of the sequence, after the version.
     */
    public String getCommunityName()
        throws SNMPBadValueException
    {
        Vector contents = (Vector)(this.getValue());
        Object communityName = contents.get(1);
        
        if (!(communityName instanceof SNMPOctetString))
            throw new SNMPBadValueException("Wrong SNMP type for community name in message: expected SNMPOctetString, have " 
                    + communityName.getClass().toString());
        
        return ((SNMPOctetString)communityName).toString();
    }
    
}
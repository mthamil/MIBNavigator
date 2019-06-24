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

package snmp;

import snmp.datatypes.SnmpBERType;
import snmp.datatypes.SnmpNull;
import snmp.datatypes.SnmpObject;
import snmp.datatypes.SnmpObjectIdentifier;
import snmp.datatypes.sequence.SnmpMessage;
import snmp.datatypes.sequence.SnmpSequence;
import snmp.datatypes.sequence.SnmpVariablePair;
import snmp.datatypes.sequence.pdu.SnmpBasicPDU;
import snmp.error.ErrorStatus;
import snmp.error.SnmpBadValueException;
import utilities.iteration.InspectableIterator;

/**
 * Class that assists in the creation of SNMP messages.
 */
public class SnmpMessageFactory
{
	private String community;
	private SnmpVersion version;
	
	private InspectableIterator<Integer> requestIdIterator;
	
	/**
	 * Creates a default message factory with an iterator
	 * that generates Request IDs.
	 * @param idIterator the object used to generate Request IDs
	 */
	public SnmpMessageFactory(InspectableIterator<Integer> idIterator) 
	{
		requestIdIterator = idIterator;
	}
	
	/**
	 * Creates a message factory that creates messages using the given
	 * community string and SNMP version.
	 * @param idIterator the object used to generate Request IDs
	 * @param version
	 * @param community
	 */
	public SnmpMessageFactory(InspectableIterator<Integer> idIterator, SnmpVersion version, String community)
	{
		this.requestIdIterator = idIterator;
		this.community = community;
		this.version = version;
	}
	
	/**
	 * Sets the community string used for new messages.
	 * @param community
	 */
	public void setCommunity(String community)
	{
		this.community = community;
	}
	
	/**
	 * Sets the SNMP version used for new messages.
	 * @param version
	 */
	public void setVersion(SnmpVersion version)
	{
		this.version = version;
	}
	
	/**
	 * Gets the current request ID.
	 * @return
	 */
	public int getCurrentRequestId()
	{
		return requestIdIterator.current();
	}
	
	/**
     * Constructs an SNMPMessage with SNMP Nulls for its values.
	 * @param messageType the type of message
	 * @param oids the string OIDs to use in the message
     * 
     * @return an SNMPMessage
     */
    public SnmpMessage createMessage(SnmpBERType messageType, String[] oids)
        throws SnmpBadValueException
    {
        // Initialize the null values array.
        SnmpObject[] values = new SnmpObject[oids.length];
        //Arrays.fill(values, new SNMPNull());
        for (int i = 0; i < values.length; i++)
            values[i] = new SnmpNull();

        return this.createMessage(messageType, oids, values);
    }
    
    
    /**
     * Constructs an SNMPMessage with the given SNMPObject array for its values.
     * @param messageType the type of message
     * @param oids the string OIDs to use in the message
     * @param newValues the corresponding SNMPObjects to use in the message
     * 
     * @return an SNMPMessage
     */
    public SnmpMessage createMessage(SnmpBERType messageType, String[] oids, SnmpObject[] newValues)
        throws SnmpBadValueException
    {
        SnmpSequence varList = new SnmpSequence();

        ErrorStatus errorStatus = ErrorStatus.NoError;
        int errorIndex = 0;

        for (int i = 0; i < oids.length; i++)
        {
            SnmpObjectIdentifier requestedObjectIdentifier = new SnmpObjectIdentifier(oids[i]);
            SnmpVariablePair nextPair = new SnmpVariablePair(requestedObjectIdentifier, newValues[i]);
            varList.addSNMPObject(nextPair);
        }

        SnmpBasicPDU pdu = new SnmpBasicPDU(messageType, requestIdIterator.next(), errorStatus, errorIndex, varList);
        SnmpMessage message = new SnmpMessage(version, community, pdu);
        return message;
    }
    
    
    /**
     * Constructs an SNMPMessage with an SNMPNull as its value.
     * @param messageType The byte tag indicating the type of PDU as found in SNMPBERCodec.
     * @param requestedOid The OID to use in the message.
     * 
     * @return An SNMPMessage.
     * @throws SnmpBadValueException
     */
    public SnmpMessage createMessage(SnmpBERType messageType, SnmpObjectIdentifier requestedOid) 
        throws SnmpBadValueException
    {
        return this.createMessage(messageType, requestedOid, new SnmpNull());
    }
    
    
    /**
     * Constructs an SNMPMessage with a specified SNMPObject as its value.
     * @param messageType The byte tag indicating the type of PDU as found in SNMPBERCodec.
     * @param requestedOid The OID to use in the message.
     * @param newValue The SNMPObject to use in the message.
     * 
     * @return An SNMPMessage.
     * @throws SnmpBadValueException
     */
    public SnmpMessage createMessage(SnmpBERType messageType, SnmpObjectIdentifier requestedOid, SnmpObject newValue)
        throws SnmpBadValueException
    {
    	ErrorStatus errorStatus = ErrorStatus.NoError;
        int errorIndex = 0;

        SnmpVariablePair nextPair = new SnmpVariablePair(requestedOid, newValue);

        SnmpSequence varList = new SnmpSequence();
        varList.addSNMPObject(nextPair);
        SnmpBasicPDU pdu = new SnmpBasicPDU(messageType, requestIdIterator.next(), errorStatus, errorIndex, varList);

        SnmpMessage message = new SnmpMessage(version, community, pdu);
        return message;
    }
    
    /**
     * Constructs an SNMP message with SNMP nulls.
     * @param messageType The byte tag indicating the type of PDU as found in SNMPBERCodec
     * @param errorStatus
     * @param errorIndex
     * @param requestedOids
     * 
     * @return
     * @throws SnmpBadValueException
     */
    public SnmpMessage createMessage(SnmpBERType messageType, ErrorStatus errorStatus, int errorIndex, 
    		SnmpObjectIdentifier[] requestedOids) throws SnmpBadValueException
	{
    	SnmpSequence varList = new SnmpSequence();

    	for (int i = 0; i < requestedOids.length; i++)
    	{
    	    SnmpVariablePair nextPair = new SnmpVariablePair(requestedOids[i], new SnmpNull());
    	    varList.addSNMPObject(nextPair);
    	}

    	SnmpBasicPDU pdu = new SnmpBasicPDU(SnmpBERType.SnmpGetNextRequest, requestIdIterator.next(), errorStatus, errorIndex, varList);
    	SnmpMessage message = new SnmpMessage(version, community, pdu);	
    	return message;
	}

}

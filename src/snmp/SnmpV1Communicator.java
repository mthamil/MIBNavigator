/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 * Copyright (C) 2009, Matt Hamilton <matthamilton@live.com>
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

import java.io.*;
import java.net.*;

import snmp.datatypes.SnmpBERCodec;
import snmp.datatypes.SnmpBERType;
import snmp.datatypes.SnmpNull;
import snmp.datatypes.SnmpObject;
import snmp.datatypes.SnmpObjectIdentifier;
import snmp.datatypes.sequence.SnmpMessage;
import snmp.datatypes.sequence.SnmpSequence;
import snmp.datatypes.sequence.SnmpVarBindList;
import snmp.datatypes.sequence.SnmpVariablePair;
import snmp.datatypes.sequence.pdu.SnmpBasicPDU;
import snmp.error.SnmpBadValueException;
import snmp.error.SnmpGetException;
import snmp.error.SnmpSetException;
import snmp.error.ErrorStatus;


/**
 *  The class SNMPv1Communicator defines methods for communicating with SNMP entities.
 *  The approach is that from version 1 of SNMP, using no encryption of data. Communication occurs
 *  via UDP using port 161, the standard SNMP port, unless explicitly set otherwise.
 */
public class SnmpV1Communicator
{
    public static final int DEFAULT_SNMP_PORT = 161;

    // Largest size for datagram packet payload; based on
    // RFC 1157, need to handle messages of at least 484 bytes.
    private int receiveBufferSize = 512;

    private SnmpVersion version;
    private int port;
    private InetAddress hostAddress;
    private String community;
    private DatagramSocket dSocket;

    public int requestID = 1;

    /**
     *  Constructs a new communication object to communicate with the specified host using the
     *  given community name. The version setting should be either 0 (version 1) or 1 (version 2,
     *  a la RFC 1157). The default SNMP port is used and it has a default timeout of 15 seconds.
     */
    public SnmpV1Communicator(SnmpVersion version, InetAddress hostAddress, String community)
        throws SocketException
    {
        this.version = version;
        this.hostAddress = hostAddress;
        this.community = community;
        this.port = SnmpV1Communicator.DEFAULT_SNMP_PORT;

        dSocket = new DatagramSocket();
        dSocket.setSoTimeout(15000);    //15 seconds
    }

    
    /**
     *  Permits setting timeout value for underlying datagram socket (in milliseconds).
     *  The timeout must be greater than zero.
     */
    public void setTimeout(int socketTimeout) throws SocketException
    {
        // Timeouts cannot be negative and 0 would be an infinite timeout which is undesirable.
        if (socketTimeout <= 0)
            throw new IllegalArgumentException("Timeout must be greater than zero.");
        
        dSocket.setSoTimeout(socketTimeout);
    }
    
    
    /**
     *  Permits setting a port different than the default SNMP port.  Some SNMP agents can
     *  be configured to listen on ports other than 161. The port number must not be negative.
     */
    public void setPort(int newPort)
    {
        if (newPort < 0)
            throw new IllegalArgumentException("Port number must not be negative.");
        
        port = newPort;
    }
    
    
    /**
     *  Sets the size of the buffer used to receive response packets. RFC 1157 stipulates that an SNMP
     *  implementation must be able to receive packets of at least 484 bytes, so if you try to set the
     *  size to a value less than this, the receive buffer size will be set to 484 bytes. In addition,
     *  the maximum size of a UDP packet payload is 65535 bytes, so setting the buffer to a larger size
     *  will just waste memory. The default value is 512 bytes. The value may need to be increased if
     *  get-requests are issued for multiple OIDs.
     */
    public void setReceiveBufferSize(int receiveBufferSize)
    {
    	this.receiveBufferSize = (receiveBufferSize >= 484) ? receiveBufferSize : 484;
    }


    /**
     *  Gets the current size of the buffer used to receive response packets.
     */
    public int getReceiveBufferSize()
    {
        return receiveBufferSize;
    }


    /**
     *  Closes the "connection" with the device.
     */
    public void closeConnection() throws SocketException
    {
        dSocket.close();
    }

    
    /**
     * Constructs an SNMPMessage with SNMPNulls for its values.
     * 
     * @param itemIds the OIDs to use in the message
     * @param messageType the type of message
     * @return an SNMPMessage
     */
    private SnmpMessage createMessage(String[] itemIds, SnmpBERType messageType)
        throws SnmpBadValueException
    {
        // Initialize the null values array.
        SnmpObject[] values = new SnmpObject[itemIds.length];
        //Arrays.fill(values, new SNMPNull());
        for (int i = 0; i < values.length; i++)
            values[i] = new SnmpNull();

        return this.createMessage(itemIds, messageType, values);
    }
    
    
    /**
     * Constructs an SNMPMessage with the given SNMPObject array for its values.
     * 
     * @param itemIds the OIDs to use in the message
     * @param messageType the type of message
     * @param newValues the corresponding SNMPObjects to use in the message
     * @return an SNMPMessage
     */
    private SnmpMessage createMessage(String[] itemIds, SnmpBERType messageType, SnmpObject[] newValues)
        throws SnmpBadValueException
    {
        SnmpSequence varList = new SnmpSequence();

        ErrorStatus errorStatus = ErrorStatus.NoError;
        int errorIndex = 0;

        for (int i = 0; i < itemIds.length; i++)
        {
            SnmpObjectIdentifier requestedObjectIdentifier = new SnmpObjectIdentifier(itemIds[i]);
            SnmpVariablePair nextPair = new SnmpVariablePair(requestedObjectIdentifier, newValues[i]);
            varList.addSNMPObject(nextPair);
        }

        SnmpBasicPDU pdu = new SnmpBasicPDU(messageType, requestID, errorStatus, errorIndex, varList);
        SnmpMessage message = new SnmpMessage(version, community, pdu);
        return message;
    }
    
    
    /**
     * Constructs an SNMPMessage with an SNMPNull as its value.
     * 
     * @param requestedOid The OID to use in the message.
     * @param messageType The byte tag indicating the type of PDU as found in SNMPBERCodec.
     * @return An SNMPMessage.
     * @throws SnmpBadValueException
     */
    private SnmpMessage createMessage(SnmpObjectIdentifier requestedOid, SnmpBERType messageType) 
        throws SnmpBadValueException
    {
        return this.createMessage(requestedOid, messageType, new SnmpNull());
    }
    
    
    /**
     * Constructs an SNMPMessage with a specified SNMPObject as its value.
     * 
     * @param requestedOid The OID to use in the message.
     * @param messageType The byte tag indicating the type of PDU as found in SNMPBERCodec.
     * @param newValue The SNMPObject to use in the message.
     * @return An SNMPMessage.
     * @throws SnmpBadValueException
     */
    private SnmpMessage createMessage(SnmpObjectIdentifier requestedOid, SnmpBERType messageType, SnmpObject newValue)
        throws SnmpBadValueException
    {
    	ErrorStatus errorStatus = ErrorStatus.NoError;
        int errorIndex = 0;

        SnmpVariablePair nextPair = new SnmpVariablePair(requestedOid, newValue);

        SnmpSequence varList = new SnmpSequence();
        varList.addSNMPObject(nextPair);
        SnmpBasicPDU pdu = new SnmpBasicPDU(messageType, requestID, errorStatus, errorIndex, varList);

        SnmpMessage message = new SnmpMessage(version, community, pdu);
        return message;
    }
    


    /**
     *  Retrieves the MIB variable values corresponding to the object identifiers
     *  given in itemIds (in dotted-integer notation). Return as SNMPVarBindList object; if no
     *  such variable (either due to device not supporting it, or community name having incorrect
     *  access privilege), SNMPGetException is thrown.
     *  
     *  @param itemIds one or more OIDs in dotted string form
     *  @throws IOException when a timeout is experienced while waiting for response to request.
     *  @throws SnmpBadValueException
     *  @throws SnmpGetException Thrown if supplied OID has value that can't be retrieved
     */
    public SnmpVarBindList getMIBEntry(String ... itemIds)
        throws IOException, SnmpBadValueException, SnmpGetException
    {
        return this.getMIBEntry(itemIds, SnmpBERType.SnmpGetRequest);
    }


    /**
     *  Retrieves the MIB variable value corresponding to the object identifiers following those
     *  given in itemIds (in dotted-integer notation). Return as SNMPVarBindList object;
     *  if no such variable (either due to device not supporting it, or community name having
     *  incorrect access privilege), SNMPGetException thrown.
     *  
     *  @param itemIds one or more OIDs in dotted string form
     *  @throws IOException when a timeout is experienced while waiting for response to request.
     *  @throws SnmpBadValueException
     *  @throws SnmpGetException Thrown if one of supplied OIDs has value that can't be retrieved
     */
    public SnmpVarBindList getNextMIBEntry(String ... itemIds)
        throws IOException, SnmpBadValueException, SnmpGetException
    {
        return this.getMIBEntry(itemIds, SnmpBERType.SnmpGetNextRequest);
    }

    
    /**
     *  Retrieves values for an array of String object identifiers. Processing varies slightly based on whether 
     *  the request type is a GetRequest or a GetNextRequest.
     * 
     *  @param itemIds one or more OIDs in dotted string form
     *  @param getType the BER type identifier of the request.  Must be either GET_REQUEST or GET_NEXT_REQUEST.
     *  @throws IOException when a timeout is experienced while waiting for response to request.
     *  @throws SnmpBadValueException
     *  @throws SnmpGetException Thrown if OID following one of supplied OIDs has value that can't be retrieved
     */
    private SnmpVarBindList getMIBEntry(String[] itemIds, SnmpBERType getType)
        throws IOException, SnmpBadValueException, SnmpGetException
    {
        if (getType != SnmpBERType.SnmpGetRequest && getType != SnmpBERType.SnmpGetNextRequest)
            throw new SnmpBadValueException("Bad request type: " + getType);
        
        // Send request to specified host to retrieve values of object identifiers.

        SnmpVarBindList retrievedVars = new SnmpVarBindList();
        
        SnmpMessage message = this.createMessage(itemIds, getType);
        byte[] messageEncoding = message.encode();

        DatagramPacket outPacket = new DatagramPacket(messageEncoding, messageEncoding.length, hostAddress, port);
        dSocket.send(outPacket);

        while (true)    // wait until receive reply for requestID & OID (or error)
        {
            DatagramPacket inPacket = new DatagramPacket(new byte[receiveBufferSize], receiveBufferSize);
            dSocket.receive(inPacket);

            byte[] encodedMessage = inPacket.getData();
            SnmpMessage receivedMessage = new SnmpMessage(SnmpBERCodec.extractNextTLV(encodedMessage,0).value);
            SnmpBasicPDU receivedPDU = receivedMessage.getPDU();

            // Check request identifier; if incorrect, just ignore packet and continue waiting.
            if (receivedPDU.getRequestID() == requestID)
            {
                // Check error status; if retrieval problem, throw SNMPGetException.
                if (receivedPDU.getErrorStatus() != ErrorStatus.NoError)
                {
                    // Determine error index.
                    int errorIndex = receivedPDU.getErrorIndex();
                    
                    String msgPrefix = "OID ";
                    if (getType == SnmpBERType.SnmpGetNextRequest)
                        msgPrefix = msgPrefix + "following ";
  
                    throw new SnmpGetException(msgPrefix + itemIds[errorIndex - 1] + " not available for retrieval", 
                            errorIndex, receivedPDU.getErrorStatus());
                }

                // Copy data from retrieved sequence to variable-bind list.
                SnmpSequence varList = receivedPDU.getVarBindList();

                for (int i = 0; i < varList.size(); i++)
                {
                    SnmpSequence newPair = (SnmpSequence)(varList.getSNMPObjectAt(i));

                    SnmpObjectIdentifier newObjectIdentifier = (SnmpObjectIdentifier)(newPair.getSNMPObjectAt(0));
                    SnmpObject newValue = newPair.getSNMPObjectAt(1);

                    if (getType == SnmpBERType.SnmpGetRequest && !(newObjectIdentifier.toString().equals(itemIds[i])))
                    {
                        // wrong OID; throw GetException
                        throw new SnmpGetException("OID " + itemIds[i] + " expected at index " + i + ", OID " + newObjectIdentifier 
                                + " received", i + 1, ErrorStatus.GeneralError);
                    }

                    retrievedVars.addSNMPObject(newPair);
                }

                break;
            }
        }

        requestID++;
        return retrievedVars;
    }
    
    

    /**
     *  Sets the MIB variable value of the object identifier given in itemId (in dotted-integer notation). 
     *  Returns the SNMPVarBindList object returned by a device in its response; can be used to check 
     *  that the set was successful.
     *  
     *  @throws IOException when a timeout is experienced while waiting for response to request.
     *  @throws SnmpBadValueException
     */
    public SnmpVarBindList setMIBEntry(String itemId, SnmpObject newValue)
        throws IOException, SnmpBadValueException, SnmpSetException
    {
        return this.setMIBEntry(new String[] { itemId }, new SnmpObject[] { newValue });
    }


    /**
     *  Sets the MIB variable values of the supplied object identifiers given in the
     *  itemIds array (in dotted-integer notation). Returns the SNMPVarBindList returned
     *  by a device in its response; can be used to check that the set was successful.
     *  
     *  @throws IOException when a timeout is experienced while waiting for response to request.
     *  @throws SnmpBadValueException
     */
    public SnmpVarBindList setMIBEntry(String[] itemIds, SnmpObject[] newValues)
        throws IOException, SnmpBadValueException, SnmpSetException
    {
        // check that OID and value arrays have same size
        if (itemIds.length != newValues.length)
            throw new SnmpSetException("OID and value arrays must have same size", 0, ErrorStatus.GeneralError);

        // Send SetRequest to specified host to set values of object identifiers.

        SnmpVarBindList retrievedVars = new SnmpVarBindList();

        SnmpMessage message = this.createMessage(itemIds, SnmpBERType.SnmpSetRequest, newValues);
        byte[] messageEncoding = message.encode();

        DatagramPacket outPacket = new DatagramPacket(messageEncoding, messageEncoding.length, hostAddress, port);
        dSocket.send(outPacket);

        while (true)    // wait until receive reply for correct OID (or error)
        {
            DatagramPacket inPacket = new DatagramPacket(new byte[receiveBufferSize], receiveBufferSize);
            dSocket.receive(inPacket);

            byte[] encodedMessage = inPacket.getData();
            SnmpMessage receivedMessage = new SnmpMessage(SnmpBERCodec.extractNextTLV(encodedMessage,0).value);
            SnmpBasicPDU receivedPDU = receivedMessage.getPDU();

            // Check request identifier; if incorrect, just ignore packet and continue waiting.
            if (receivedPDU.getRequestID() == requestID)
            {
                // Check error status; if retrieval problem, throw SNMPSetException.
                if (receivedPDU.getErrorStatus() != ErrorStatus.NoError)
                {
                    int errorIndex = receivedPDU.getErrorIndex();

                    switch (receivedPDU.getErrorStatus())
                    {
                        case TooBig:
                            throw new SnmpSetException("Value supplied for OID " + itemIds[errorIndex - 1] + " too big.", 
                                    receivedPDU.getErrorIndex(), receivedPDU.getErrorStatus());

                        case NoSuchName:
                            throw new SnmpSetException("OID " + itemIds[errorIndex - 1] + " not available for setting.", 
                                    receivedPDU.getErrorIndex(), receivedPDU.getErrorStatus());

                        case BadValue:
                            throw new SnmpSetException("Bad value supplied for OID " + itemIds[errorIndex - 1] + ".", 
                                    receivedPDU.getErrorIndex(), receivedPDU.getErrorStatus());

                        case ReadOnly:
                            throw new SnmpSetException("OID " + itemIds[errorIndex - 1] + " read-only.", 
                                    receivedPDU.getErrorIndex(), receivedPDU.getErrorStatus());

                        default:
                            throw new SnmpSetException("Error setting OID " + itemIds[errorIndex - 1] + ".", 
                                    receivedPDU.getErrorIndex(), receivedPDU.getErrorStatus());
                    }
                }


                // Copy data from retrieved sequence to var bind list.
                SnmpSequence varList = receivedPDU.getVarBindList();

                for (int i = 0; i < varList.size(); i++)
                {
                    SnmpSequence newPair = (SnmpSequence)(varList.getSNMPObjectAt(i));

                    SnmpObjectIdentifier newObjectIdentifier = (SnmpObjectIdentifier)(newPair.getSNMPObjectAt(0));
                    //SNMPObject receivedValue = newPair.getSNMPObjectAt(1);

                    if (newObjectIdentifier.toString().equals(itemIds[i]))
                        retrievedVars.addSNMPObject(newPair);
                    else      // wrong OID; throw GetException
                        throw new SnmpSetException("OID " + itemIds[i] + " expected at index " + i + ", OID " + newObjectIdentifier 
                                + " received", i + 1, ErrorStatus.GeneralError);
                }

                break;
            }
        }

        requestID++;
        return retrievedVars;
    }

    
    

    /**
     *  Retrieves all MIB variable values whose OIDs start with the supplied baseId. Since the entries of
     *  an SNMP table have the form  <baseID>.<tableEntry>.<index>, this will retrieve all of the table
     *  data as an SNMPVarBindList object consisting of sequence of SNMPVariablePairs.
     *  Uses SNMPGetNextRequests to retrieve variable values in sequence.
     *  
     *  @throws IOException Thrown when timeout experienced while waiting for response to request.
     *  @throws SnmpBadValueException
     */
    public SnmpVarBindList retrieveMIBTable(String baseId)
        throws IOException, SnmpBadValueException, SnmpGetException
    {
        // Send GetNextRequests until an error message or a repeat of the object identifier we sent out is
        // received.
        SnmpVarBindList retrievedVars = new SnmpVarBindList();

        SnmpObjectIdentifier requestedOid = new SnmpObjectIdentifier(baseId);

        ErrorStatus errorStatus = ErrorStatus.NoError;
        while (errorStatus == ErrorStatus.NoError)
        {
            SnmpMessage message = this.createMessage(requestedOid, SnmpBERType.SnmpGetNextRequest);
            byte[] messageEncoding = message.encode();

            DatagramPacket outPacket = new DatagramPacket(messageEncoding, messageEncoding.length, hostAddress, port);
            dSocket.send(outPacket);

            DatagramPacket inPacket = new DatagramPacket(new byte[receiveBufferSize], receiveBufferSize);
            dSocket.receive(inPacket);

            byte[] encodedMessage = inPacket.getData();

            SnmpMessage receivedMessage = new SnmpMessage(SnmpBERCodec.extractNextTLV(encodedMessage,0).value);
            SnmpBasicPDU receivedPDU = receivedMessage.getPDU();
            errorStatus = receivedPDU.getErrorStatus();

            // Check request identifier; if incorrect, just ignore packet and continue waiting.
            if (receivedPDU.getRequestID() == requestID && errorStatus == ErrorStatus.NoError)
            {
                // Check error status; if retrieval problem, just break - could be there are no additional OIDs.
                //if (receivedPDU.getErrorStatus() != 0)
                //    break;

                SnmpSequence varList = receivedPDU.getVarBindList();
                SnmpSequence newPair = (SnmpSequence)(varList.getSNMPObjectAt(0));

                SnmpObjectIdentifier nextOid = (SnmpObjectIdentifier)(newPair.getSNMPObjectAt(0));
                SnmpObject newValue = newPair.getSNMPObjectAt(1);

                // Now see if retrieved ID starts with table base; if not, done with table - break.
                String newOIDString = nextOid.toString();
                if (!newOIDString.startsWith(baseId))
                    break;

                retrievedVars.addSNMPObject(newPair);

                requestedOid = nextOid;
                requestID++;
            }
        }

        return retrievedVars;
    }


    /**
     *  Retrieves all MIB variable values whose OIDs start with the supplied baseIds. The normal way for
     *  this to be used is for the base OID array to consist of the base OIDs of the columns of a table.
     *  This method will then retrieve all of the entries of the table corresponding to these columns, one
     *  row at a time (i.e., the entries for each row will be retrieved in a single SNMP request). This
     *  will retrieve the table data as an SNMPVarBindList object consisting of sequence of SNMPVariablePairs,
     *  with the entries for each row grouped together. This may provide a more convenient arrangement of
     *  the table data than the simpler retrieveMIBTable method taking a single OID as argument; in addition,
     *  it's more efficient, requiring one SNMP request per row rather than one request per entry.
     *  Uses SNMPGetNextRequests to retrieve variable values for each row in sequence.
     *  
     *  @throws IOException Thrown when timeout experienced while waiting for response to request.
     *  @throws SnmpBadValueException
     *  @throws SnmpGetException Thrown if incomplete row retrieved
     */
    public SnmpVarBindList retrieveMIBTable(String[] baseIds)
        throws IOException, SnmpBadValueException, SnmpGetException
    {
        // Send GetNextRequests until an error message or a repeat of the object identifier we sent out
        // is received.
        SnmpVarBindList retrievedVars = new SnmpVarBindList();

        ErrorStatus errorStatus = ErrorStatus.NoError;
        int errorIndex = 0;

        SnmpObjectIdentifier[] requestedObjectIdentifier = new SnmpObjectIdentifier[baseIds.length];
        for (int i = 0; i < baseIds.length; i++)
            requestedObjectIdentifier[i] = new SnmpObjectIdentifier(baseIds[i]);

        retrievalLoop:
        while (errorStatus == ErrorStatus.NoError)
        {
            SnmpSequence varList = new SnmpSequence();

            for (int i = 0; i < requestedObjectIdentifier.length; i++)
            {
                SnmpVariablePair nextPair = new SnmpVariablePair(requestedObjectIdentifier[i], new SnmpNull());
                varList.addSNMPObject(nextPair);
            }

            SnmpBasicPDU pdu = new SnmpBasicPDU(SnmpBERType.SnmpGetNextRequest, requestID, errorStatus, errorIndex, varList);
            SnmpMessage message = new SnmpMessage(version, community, pdu);

            byte[] messageEncoding = message.encode();

            DatagramPacket outPacket = new DatagramPacket(messageEncoding, messageEncoding.length, hostAddress, port);
            dSocket.send(outPacket);

            DatagramPacket inPacket = new DatagramPacket(new byte[receiveBufferSize], receiveBufferSize);
            dSocket.receive(inPacket);

            byte[] encodedMessage = inPacket.getData();
            SnmpMessage receivedMessage = new SnmpMessage(SnmpBERCodec.extractNextTLV(encodedMessage,0).value);
            SnmpBasicPDU receivedPDU = receivedMessage.getPDU();

            // Check request identifier; if incorrect, just ignore packet and continue waiting.
            if (receivedPDU.getRequestID() == requestID)
            {
                // Check error status; if retrieval problem for error index 1, just break - assume there are no additional OIDs.
                // to retrieve. If index is other than 1, throw an exception.
                if (receivedPDU.getErrorStatus() != ErrorStatus.NoError)
                {
                    int retrievedErrorIndex = receivedPDU.getErrorIndex();

                    if (retrievedErrorIndex == 1)
                        break retrievalLoop;
                    
                    throw new SnmpGetException("OID following " + requestedObjectIdentifier[retrievedErrorIndex - 1] + 
                            " not available for retrieval", retrievedErrorIndex, receivedPDU.getErrorStatus());
                }

                // Copy data from retrieved sequence to variable-bind list.
                varList = receivedPDU.getVarBindList();

                // Check that the right number of variables were in reply; if not, throw GetException.
                if (varList.size() != requestedObjectIdentifier.length)
                    throw new SnmpGetException("Incomplete row of table received", 0, ErrorStatus.GeneralError);

                // Copy the retrieved variable pairs into retrievedVars.
                for (int i = 0; i < varList.size(); i++)
                {
                    SnmpSequence newPair = (SnmpSequence)(varList.getSNMPObjectAt(i));

                    SnmpObjectIdentifier newObjectIdentifier = (SnmpObjectIdentifier)(newPair.getSNMPObjectAt(0));
                    SnmpObject newValue = newPair.getSNMPObjectAt(1);

                    // Now see if retrieved OID starts with table base; if not, done with table - break.
                    String newOIDString = newObjectIdentifier.toString();
                    if (!newOIDString.startsWith(baseIds[i]))
                    {
                        // It's the first element of the row; just break.
                        if (i == 0) 
                            break retrievalLoop;    

                        // It's a subsequent row element; throw exception.
                        throw new SnmpGetException("Incomplete row of table received", i + 1, ErrorStatus.GeneralError);
                    }

                    retrievedVars.addSNMPObject(newPair);

                    // Set requested identifiers array to current identifiers to do get-next for next row.
                    requestedObjectIdentifier[i] = newObjectIdentifier;
                }

                requestID++;
            }
        }

        return retrievedVars;
    }
    
    
    /**
     *  Retrieves all MIB variable values subsequent to the starting object identifier
     *  given in startID (in dotted-integer notation). Return as SNMPVarBindList object.
     *  Uses SNMPGetNextRequests to retrieve variable values in sequence.
     *  
     *  @throws IOException Thrown when timeout experienced while waiting for response to request.
     *  @throws SnmpBadValueException
     */
    public SnmpVarBindList retrieveAllMIBInfo(String startID)
        throws IOException, SnmpBadValueException
    {
        // Send GetNextRequests until receive an error message or a 
        // repeat of the object identifier we sent out
        SnmpVarBindList retrievedVars = new SnmpVarBindList();

        SnmpObjectIdentifier requestedOid = new SnmpObjectIdentifier(startID);
        SnmpMessage message = this.createMessage(requestedOid, SnmpBERType.SnmpGetNextRequest);
        byte[] messageEncoding = message.encode();

        DatagramPacket outPacket = new DatagramPacket(messageEncoding, messageEncoding.length, hostAddress, port);
        dSocket.send(outPacket);

        ErrorStatus errorStatus = ErrorStatus.NoError;
        while (errorStatus == ErrorStatus.NoError)
        {
            DatagramPacket inPacket = new DatagramPacket(new byte[receiveBufferSize], receiveBufferSize);
            dSocket.receive(inPacket);

            byte[] encodedMessage = inPacket.getData();
            SnmpMessage receivedMessage = new SnmpMessage(SnmpBERCodec.extractNextTLV(encodedMessage,0).value);
            errorStatus = receivedMessage.getPDU().getErrorStatus();

            SnmpSequence varList = receivedMessage.getPDU().getVarBindList();
            SnmpSequence newPair = (SnmpSequence)(varList.getSNMPObjectAt(0));
            SnmpObjectIdentifier nextOid = (SnmpObjectIdentifier)(newPair.getSNMPObjectAt(0));

            retrievedVars.addSNMPObject(newPair);

            if (requestedOid.equals(nextOid))
                break;

            requestedOid = nextOid;
            requestID++;

            // Construct and send next packet.
            message = this.createMessage(requestedOid, SnmpBERType.SnmpGetNextRequest);
            messageEncoding = message.encode();
            outPacket = new DatagramPacket(messageEncoding, messageEncoding.length, hostAddress, port);
            dSocket.send(outPacket);
        }

        return retrievedVars;
    }

    
    
    
    
//    public static void main(String[] args)
//    {
//        try
//        {
//            //contact: Genco IT
//            SnmpV1Communicator comm = new SnmpV1Communicator(SnmpVersion.SNMPv1, InetAddress.getByName("127.0.0.1"), "de2la6");
//            
//            //String[] oids = { "1.3.6.1.2.1.1.5", "1.3.6.1.2.1.1.6", "1.3.6.1.2.1.1.7" };
//            //String oid = "1.3.6.1.2.1.1.4.0";
//            //SNMPVarBindList results = comm.getNextMIBEntry(oids);
//            
//            //String[] oids = { "1.3.6.1.2.1.1.5.0", "1.3.6.1.2.1.1.6.0", "1.3.6.1.2.1.1.7.0" };
//            //String oid = "1.3.6.1.2.1.1.4.0";
//            //SNMPVarBindList results = comm.getMIBEntry(oid);
//            
//            //SNMPOctetString value = new SNMPOctetString("Genco IT");
//            //SNMPVarBindList results = comm.setMIBEntry(oid, value);
//            
//            String oid = "1.3.6.1.2.1.1";
//            SnmpVarBindList results = comm.retrieveMIBTable(oid);
//            
//            System.out.println(results.toString());
//        }
//        catch (Exception e)
//        {
//            e.getMessage();
//        }
//    }
}
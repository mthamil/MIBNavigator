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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import snmp.error.SnmpGetException;
import snmp.error.SnmpRequestException;
import snmp.error.SnmpSetException;
import snmp.error.ErrorStatus;


/**
 *  The class SNMPv1SimpleAgent implements an interface for responding to requests sent from a remote SNMP 
 *  manager. The agent simply listens for requests for information, and passes requested OIDs on to concrete 
 *  subclasses of SNMPRequestListener. These are expected to retrieve requested information from the system,
 *  and return this to the agent interface for inclusion in a response to the manager. 
 *  The approach is that from version 1 of SNMP, using no encryption of data. Communication occurs
 *  via UDP.
 */
public class SnmpV1SimpleAgent implements Runnable
{
    // largest size for datagram packet payload; based on
    // RFC 1157, need to handle messages of at least 484 bytes
    public int receiveBufferSize = 512;
    
    private SnmpVersion version = SnmpVersion.SNMPv1;
    
    private DatagramSocket dSocket;
    private Thread receiveThread;
    private List<SnmpRequestListener> requestListeners;
    
    private PrintWriter errorLogger;
    
    private SnmpSequence requestedVarList;
    private SnmpSequence responseVarList;
    private Hashtable<SnmpObjectIdentifier,SnmpObject> variablePairs;
    private int errorIndex = 0;
    private ErrorStatus errorStatus = ErrorStatus.NoError;
    
    /**
     *  Constructs a new agent object to listen for requests from remote SNMP managers. The agent listens
     *  on the standard SNMP UDP port 161.
     */
    public SnmpV1SimpleAgent(SnmpVersion newVersion) throws SocketException
    {
        this(newVersion, SnmpV1Communicator.DEFAULT_SNMP_PORT, new PrintWriter(System.out));
    }
    

    /**
     *  Constructs a new agent object to listen for requests from remote SNMP managers. The agent listens
     *  on the supplied port.
     */
    public SnmpV1SimpleAgent(SnmpVersion newVersion, int localPort) throws SocketException
    {
        this(newVersion, localPort, new PrintWriter(System.out));
    }
    
    
    /**
     *  Constructs a new agent object to listen for requests from remote SNMP managers. The agent listens
     *  on the standard SNMP UDP port 161, and sends error messages to the specified PrintWriter.
     */
    public SnmpV1SimpleAgent(SnmpVersion newVersion, PrintWriter errorReceiver)
        throws SocketException
    {
        this(newVersion, SnmpV1Communicator.DEFAULT_SNMP_PORT, errorReceiver);
    }
    
    
    /**
     *  Constructs a new agent object to listen for requests from remote SNMP managers. The agent listens
     *  on the supplied port, and sends error messages to the specified PrintWriter.
     */
    public SnmpV1SimpleAgent(SnmpVersion newVersion, int localPort, PrintWriter errorReceiver)
        throws SocketException
    {
        version = newVersion;
        dSocket = new DatagramSocket(localPort);
        requestListeners = new Vector<SnmpRequestListener>(); 
        receiveThread = new Thread(this);
        errorLogger = errorReceiver;
    }
    
    
    /**
     *  Sets the specified PrintWriter to receive error messages.
     */
    public void setErrorReceiver(PrintWriter errorReceiver)
    {
        errorLogger = errorReceiver;
    }
    
    
    public void addRequestListener(SnmpRequestListener newListener)
    {
        // See if listener already added; if so, ignore.           
        if (!requestListeners.contains(newListener))
        	requestListeners.add(newListener);
    }
    
    
    public void removeRequestListener(SnmpRequestListener listener)
    {       
        requestListeners.remove(listener);
    }

    
    /**
     *  Starts listening for requests from remote managers.
     */
    public void startReceiving()
    {
        // if receiveThread not already running, start it
        if (!receiveThread.isAlive())
        {
            receiveThread = new Thread(this);
            receiveThread.start();
        }
    }
    
    
    /**
     *  Stops listening for requests from remote managers.
     */
    public void stopReceiving() throws SocketException
    {
        // interrupt receive thread so it will die a natural death
        receiveThread.interrupt();
    }

    
    /**
     *  Waits for SNMP request messages to come in on port 161 (or the port supplied in the constructor), 
     *  then dispatches the retrieved SNMPPDU and community name to each of the registered SNMPRequestListeners 
     *  by calling their processRequest methods.
     */
    public void run()
    {
        while (!receiveThread.isInterrupted())
        {   
            try
            {
                DatagramPacket inPacket = new DatagramPacket(new byte[receiveBufferSize], receiveBufferSize);
                dSocket.receive(inPacket);
                
                // Extract remote sender information.
                InetAddress requesterAddress = inPacket.getAddress();
                int requesterPort = inPacket.getPort();
                
                // Extract request data.
                byte[] encodedMessage = inPacket.getData();
                SnmpMessage receivedMessage = new SnmpMessage(SnmpBERCodec.extractNextTLV(encodedMessage,0).value);
                String communityName = receivedMessage.getCommunityName();
                SnmpPDU receivedPDU = receivedMessage.getPDU();
                SnmpBERType requestPDUType = receivedPDU.getPDUType();
                
                requestedVarList = receivedPDU.getVarBindList();
                variablePairs = new Hashtable<SnmpObjectIdentifier,SnmpObject>();
                responseVarList = new SnmpSequence();
                errorIndex = 0;
                errorStatus = ErrorStatus.NoError;
                int requestID = receivedPDU.getRequestID();
                
                try
                {
                    switch (requestPDUType)
                    {
                        case SnmpGetRequest:
                        case SnmpSetRequest:
                        case SnmpGetNextRequest:
                            this.handleRequest(receivedPDU, communityName, requestPDUType);
                            break;
                        
                        default:
                            continue;  // some other PDU type; silently ignore and skip the rest of this loop iteration
                    }
                }
                catch (SnmpRequestException e)
                {
                    // exception should contain the index and cause of error; return this in message
                    errorIndex = e.errorIndex;
                    errorStatus = e.errorStatus;

                    // just return request variable list as response variable list
                    responseVarList = requestedVarList;
                }
                catch (Exception e)
                {
                    // don't have a specific index and cause of error; return message as general error, index 0
                    errorIndex = 0;
                    errorStatus = ErrorStatus.GeneralError;
                    
                    // just return request variable list as response variable list
                    responseVarList = requestedVarList;
                    
                    // also report the exception locally
                    errorLogger.println("Exception while processing request: " + e.toString());
                    errorLogger.flush();
                }
                
                // Construct and send response.
                SnmpPDU pdu = new SnmpPDU(SnmpBERType.SnmpGetResponse, requestID, errorStatus, errorIndex, responseVarList);
                SnmpMessage message = new SnmpMessage(version, communityName, pdu);
                byte[] messageEncoding = message.encode();
                
                DatagramPacket outPacket = new DatagramPacket(messageEncoding, messageEncoding.length, requesterAddress, requesterPort);
                dSocket.send(outPacket);
            }
            catch (IOException e)
            {
                // just report the problem
                errorLogger.println("IOException during request processing: " + e.getMessage());
                errorLogger.flush();
            }
            catch (SnmpBadValueException e)
            {
                // just report the problem
                errorLogger.println("SNMPBadValueException during request processing: " + e.getMessage());
                errorLogger.flush();
            }
            catch (Exception e)
            {
                // just report the problem
                errorLogger.println("Exception during request processing: " + e.toString());
                errorLogger.flush();
            }
        
        }
                
    }
    
    
    /**
     * Passes the received PDU and community name to the processRequest method of any listeners;
     * handles slightly differently depending on whether the request is a get-next, or a get or set.
     */
    private void handleRequest(SnmpPDU receivedPDU, String communityName, SnmpBERType requestPDUType)
        throws SnmpBadValueException, SnmpGetException, SnmpSetException
    {
        // Pass the received PDU and community name to any registered listeners.
        for (SnmpRequestListener listener : requestListeners)
        {
            // Return value is a sequence of nested variable pairs for those OIDs handled by the listener:
            // consists of (supplied OID, (following OID, value)) nested variable pairs.
            SnmpSequence handledVarList;
            if (requestPDUType == SnmpBERType.SnmpGetNextRequest)
                handledVarList = listener.processGetNextRequest(receivedPDU, communityName);
            else
                handledVarList = listener.processRequest(receivedPDU, communityName);

            // Add variable pair to table of handled OIDs, if it's not already there.
            for (int j = 0; j < handledVarList.size(); j++)
            {
                SnmpSequence handledPair = (SnmpSequence)handledVarList.getSNMPObjectAt(j);
                SnmpObjectIdentifier snmpOID = (SnmpObjectIdentifier)handledPair.getSNMPObjectAt(0);
                SnmpObject snmpObject = handledPair.getSNMPObjectAt(1);

                if (!variablePairs.containsKey(snmpOID))
                    variablePairs.put(snmpOID, snmpObject);
            }
        }


        // Construct response containing the handled OIDs; if any OID not handled, throw exception.
        for (int j = 0; j < requestedVarList.size(); j++)
        {
            SnmpSequence requestPair = (SnmpSequence)requestedVarList.getSNMPObjectAt(j);
            SnmpObjectIdentifier snmpOID = (SnmpObjectIdentifier)requestPair.getSNMPObjectAt(0);

            // Find corresponding SNMP object in hashtable.
            if (!variablePairs.containsKey(snmpOID))
            {
                errorIndex = j + 1;
                errorStatus = ErrorStatus.NoSuchName;

                if (requestPDUType == SnmpBERType.SnmpSetRequest)
                    throw new SnmpSetException("OID " + snmpOID + " not handled", errorIndex, errorStatus);

                // GetRequest and GetNextRequest exceptions.
                throw new SnmpGetException("OID " + snmpOID + " not handled", errorIndex, errorStatus);
            }

            SnmpVariablePair responsePair;
            if (requestPDUType == SnmpBERType.SnmpGetNextRequest)
            {
                // value in hashtable is complete variable pair
                responsePair = (SnmpVariablePair)variablePairs.get(snmpOID);
            }
            else
            {
                SnmpObject snmpObject = variablePairs.get(snmpOID);
                responsePair = new SnmpVariablePair(snmpOID, snmpObject);
            }

            responseVarList.addSNMPObject(responsePair);
        }
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
     *  Returns the current size of the buffer used to receive response packets. 
     */
    public int getReceiveBufferSize()
    {
        return receiveBufferSize;
    }

}
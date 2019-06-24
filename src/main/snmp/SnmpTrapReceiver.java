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

package snmp;

import java.io.*;
import java.net.*;
import java.util.*;

import snmp.datatypes.SnmpBERCodec;
import snmp.datatypes.sequence.SnmpMessage;
import snmp.datatypes.sequence.pdu.SnmpV1TrapPDU;
import snmp.datatypes.sequence.pdu.SnmpV2InformRequestPDU;
import snmp.datatypes.sequence.pdu.SnmpV2TrapPDU;
import snmp.error.SnmpBadValueException;




/**
 *  The class SNMPTrapListenerInterface implements a server which listens for trap and inform request 
 * 	messages sent from remote SNMP entities. The approach is that from version 1 and 2c of SNMP, using no 
 * 	encryption of data. Communication occurs via UDP, using port 162, the standard SNMP trap port. This interface
 *  can handle both SNMPv1 and SNMPv2 traps (which have different PDU types), and SNMPv2 Inform Requests.
 *
 *  Applications utilize this class with classes which implement the SNMPTrapListener or SNMPv2TrapListener or
 *  SNMPv2InformRequestListener interfaces. These must provide a processTrap, processv2Trap, or processInformRequest 
 * 	method, and are registered/unregistered with this class through its addv1TrapListener/removev1TrapListener,
 *  addv2TrapListener/removev2TrapListener, or addv2InformRequestListener/removev2InformRequestListener
 *  methods.
 */
public class SnmpTrapReceiver implements Runnable
{    
    // Largest size for datagram packet payload; based on
    // RFC 1157, need to handle messages of at least 484 bytes.
    private int receiveBufferSize = 512;
    
    private DatagramSocket dSocket;
    private Thread receiveThread;
    
    private List<SnmpV1TrapListener> v1TrapListeners;
    private List<SnmpV2TrapListener> v2TrapListeners;
    private List<SnmpV2InformRequestListener> v2InformRequestListeners;
    private PrintWriter errorLogger;
    

    /**
     *  Constructs a new trap receiver object to receive traps from remote SNMP hosts.
     *  This version will accept messages from all hosts using any community name.
     */
    public SnmpTrapReceiver() throws SocketException
    {
        // set System.out as the error writer
        this(new PrintWriter(System.out));
    }
    
    
    /**
     *  Constructs a new trap receiver object to receive traps from remote SNMP hosts.
     *  This version will accept messages from all hosts using any community name. Uses the
     *  specified Writer to deliver error messages.
     */
    public SnmpTrapReceiver(PrintWriter errorReceiver) throws SocketException
    {
        dSocket = new DatagramSocket(SnmpTrapSender.SNMP_TRAP_PORT);
        
        v1TrapListeners = new Vector<SnmpV1TrapListener>();
        v2TrapListeners = new Vector<SnmpV2TrapListener>();
        v2InformRequestListeners = new Vector<SnmpV2InformRequestListener>();
        
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
    
    
    public void addV1TrapListener(SnmpV1TrapListener listener)
    {
        // See if listener already added; if so, ignore.
        if (!v1TrapListeners.contains(listener))
        	v1TrapListeners.add(listener);
    }
    
    
    public void removeV1TrapListener(SnmpV1TrapListener listener)
    {       
        v1TrapListeners.remove(listener);
    }
    
    
    public void addV2TrapListener(SnmpV2TrapListener listener)
    {
        // See if listener already added; if so, ignore.
    	if (!v2TrapListeners.contains(listener))
    		v2TrapListeners.add(listener);
    }
    
    
    public void removeV2TrapListener(SnmpV2TrapListener listener)
    {
        v2TrapListeners.remove(listener);
    }
    
    
    public void addV2InformRequestListener(SnmpV2InformRequestListener listener)
    {
        // See if listener already added; if so, ignore.
    	if (!v2InformRequestListeners.contains(listener))
    		v2InformRequestListeners.add(listener);
    }
    
    
    public void removeV2InformRequestListener(SnmpV2InformRequestListener listener)
    {
        v2InformRequestListeners.remove(listener);
    }

    
    /**
     *  Starts listening for trap and inform messages.
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
     *  Stops listening for trap and inform messages.
     */
    public void stopReceiving() throws SocketException
    {
        // interrupt receive thread so it will die a natural death
        receiveThread.interrupt();
    }

    
    /**
     *  The run() method for the trap interface's listener. Just waits for trap or inform messages to
     *  come in on port 162, then dispatches the received PDUs to each of the registered 
     *  listeners by calling their processTrap() or processInform() methods.
     */
    public void run()
    {
        while (!receiveThread.isInterrupted())
        {
            try
            {
                DatagramPacket inPacket = new DatagramPacket(new byte[receiveBufferSize], receiveBufferSize);
                dSocket.receive(inPacket);
                
                byte[] encodedMessage = inPacket.getData();
                SnmpMessage receivedMessage = new SnmpMessage(SnmpBERCodec.extractNextTLV(encodedMessage,0).value);
                Object receivedPDU = receivedMessage.getPDUAsObject();
                
                if ( !(receivedPDU instanceof SnmpV1TrapPDU) && 
                	 !(receivedPDU instanceof SnmpV2TrapPDU) && 
                	 !(receivedPDU instanceof SnmpV2InformRequestPDU) )
                {
                    	throw new SnmpBadValueException("PDU received that's not a v1 or v2 trap or inform request; message payload of type " 
                    			+ receivedPDU.getClass().toString());
                }
                
                // pass the received trap PDU to the processTrap or procesv2Trap method of any listeners
                if (receivedPDU instanceof SnmpV1TrapPDU)
                {
                    for (SnmpV1TrapListener listener : v1TrapListeners)
                        listener.processv1Trap((SnmpV1TrapPDU)receivedPDU);
                }
                else if (receivedPDU instanceof SnmpV2TrapPDU)
                {                 
                    for (SnmpV2TrapListener listener : v2TrapListeners)
                        listener.processv2Trap((SnmpV2TrapPDU)receivedPDU);
                }
                else if (receivedPDU instanceof SnmpV2InformRequestPDU)
                {                  
                    for (SnmpV2InformRequestListener listener : v2InformRequestListeners)
                        listener.processv2InformRequest((SnmpV2InformRequestPDU)receivedPDU);
                }
                
            }
            catch (IOException e)
            {
                // just report the problem
                errorLogger.println("IOException during request processing: " + e.toString());
                errorLogger.flush();
            }
            catch (SnmpBadValueException e)
            {
                // just report the problem
                errorLogger.println("SNMPBadValueException during request processing: " + e.toString());
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
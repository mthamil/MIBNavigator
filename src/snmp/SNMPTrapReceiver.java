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

import java.io.*;
import java.net.*;
import java.util.*;



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
public class SNMPTrapReceiver implements Runnable
{    
    // largest size for datagram packet payload; based on
    // RFC 1157, need to handle messages of at least 484 bytes
    private int receiveBufferSize = 512;
    
    private DatagramSocket dSocket;
    private Thread receiveThread;
    
    private Vector<SNMPv1TrapListener> v1TrapListeners;
    private Vector<SNMPv2TrapListener> v2TrapListeners;
    private Vector<SNMPv2InformRequestListener> v2InformRequestListeners;
    private PrintWriter errorLogger;
    

    /**
     *  Construct a new trap receiver object to receive traps from remote SNMP hosts.
     *  This version will accept messages from all hosts using any community name.
     */
    public SNMPTrapReceiver()
        throws SocketException
    {
        // set System.out as the error writer
        this(new PrintWriter(System.out));
    }
    
    
    /**
     *  Construct a new trap receiver object to receive traps from remote SNMP hosts.
     *  This version will accept messages from all hosts using any community name. Uses the
     *  specified Writer to deliver error messages.
     */
    public SNMPTrapReceiver(PrintWriter errorReceiver)
        throws SocketException
    {
        dSocket = new DatagramSocket(SNMPTrapSender.SNMP_TRAP_PORT);
        
        v1TrapListeners = new Vector<SNMPv1TrapListener>();
        v2TrapListeners = new Vector<SNMPv2TrapListener>();
        v2InformRequestListeners = new Vector<SNMPv2InformRequestListener>();
        
        receiveThread = new Thread(this);
        
        errorLogger = errorReceiver;  
    }
    
    
    /**
     *  Set the specified PrintWriter to receive error messages.
     */
    public void setErrorReceiver(PrintWriter errorReceiver)
    {
        errorLogger = errorReceiver;
    }
    
    
    public void addv1TrapListener(SNMPv1TrapListener listener)
    {
        // see if listener already added; if so, ignore
        //for (int i = 0; i < v1TrapListeners.size(); i++)
        for (SNMPv1TrapListener trapListener : v1TrapListeners)
        {
            if (listener == trapListener)
                return;
        }
        
        // if got here, it's not in the list; add it
        v1TrapListeners.add(listener);
    }
    
    
    public void removev1TrapListener(SNMPv1TrapListener listener)
    {
        // see if listener in list; if so, remove, if not, ignore
        for (int i = 0; i < v1TrapListeners.size(); i++)
        {
            if (listener == v1TrapListeners.get(i))
            {
                v1TrapListeners.remove(i);
                break;
            }
        }
        
    }
    
    
    public void addv2TrapListener(SNMPv2TrapListener listener)
    {
        // see if listener already added; if so, ignore
        //for (int i = 0; i < v2TrapListeners.size(); i++)
        for (SNMPv2TrapListener trapListener : v2TrapListeners)
        {
            if (listener == trapListener)
                return;
        }
        
        // if got here, it's not in the list; add it
        v2TrapListeners.add(listener);
    }
    
    
    public void removev2TrapListener(SNMPv2TrapListener listener)
    {
        // see if listener in list; if so, remove, if not, ignore
        for (int i = 0; i < v2TrapListeners.size(); i++)
        {
            if (listener == v2TrapListeners.get(i))
            {
                v2TrapListeners.remove(i);
                break;
            }
        }
        
    }
    
    
    public void addv2InformRequestListener(SNMPv2InformRequestListener listener)
    {
        // see if listener already added; if so, ignore
        //for (int i = 0; i < v2InformRequestListeners.size(); i++)
        for (SNMPv2InformRequestListener requestListener : v2InformRequestListeners)
        {
            if (listener == requestListener)
                return;
        }
        
        // if got here, it's not in the list; add it
        v2InformRequestListeners.add(listener);
    }
    
    
    public void removev2InformRequestListener(SNMPv2InformRequestListener listener)
    {
        // see if listener in list; if so, remove, if not, ignore
        for (int i = 0; i < v2InformRequestListeners.size(); i++)
        {
            if (listener == v2InformRequestListeners.get(i))
            {
                v2InformRequestListeners.remove(i);
                break;
            }
        }
        
    }

    
    /**
     *  Start listening for trap and inform messages.
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
     *  Stop listening for trap and inform messages.
     */
    public void stopReceiving()
        throws SocketException
    {
        // interrupt receive thread so it will die a natural death
        receiveThread.interrupt();
    }

    
    /**
     *  The run() method for the trap interface's listener. Just waits for trap or inform messages to
     *  come in on port 162, then dispatches the recieved PDUs to each of the registered 
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
                SNMPMessage receivedMessage = new SNMPMessage(SNMPBERCodec.extractNextTLV(encodedMessage,0).value);
                Object receivedPDU = receivedMessage.getPDUAsObject();
                
                if ( !(receivedPDU instanceof SNMPv1TrapPDU) && !(receivedPDU instanceof SNMPv2TrapPDU) && !(receivedPDU instanceof SNMPv2InformRequestPDU) )
                    throw new SNMPBadValueException("PDU received that's not a v1 or v2 trap or inform request; message payload of type " + receivedPDU.getClass().toString());
                
                // pass the received trap PDU to the processTrap or procesv2Trap method of any listeners
                if (receivedPDU instanceof SNMPv1TrapPDU)
                {
                    for (SNMPv1TrapListener listener : v1TrapListeners)
                        listener.processv1Trap((SNMPv1TrapPDU)receivedPDU);
                }
                else if (receivedPDU instanceof SNMPv2TrapPDU)
                {                 
                    for (SNMPv2TrapListener listener : v2TrapListeners)
                        listener.processv2Trap((SNMPv2TrapPDU)receivedPDU);
                }
                else if (receivedPDU instanceof SNMPv2InformRequestPDU)
                {                  
                    for (SNMPv2InformRequestListener listener : v2InformRequestListeners)
                        listener.processv2InformRequest((SNMPv2InformRequestPDU)receivedPDU);
                }
                
            }
            catch (IOException e)
            {
                // just report the problem
                errorLogger.println("IOException during request processing: " + e.toString());
                errorLogger.flush();
            }
            catch (SNMPBadValueException e)
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
    
    
    /*private String hexByte(byte b)
    {
        int pos = b;
        if (pos < 0)
            pos += 256;
        String returnString = new String();
        returnString += Integer.toHexString(pos/16);
        returnString += Integer.toHexString(pos%16);
        return returnString;
    }
    
    
    private String getHex(byte theByte)
    {
        int b = theByte;
        
        if (b < 0)
            b += 256;
        
        String returnString = new String(Integer.toHexString(b));
        
        // add leading 0 if needed
        if (returnString.length()%2 == 1)
            returnString = "0" + returnString;
            
        return returnString;
    }*/
    
    
    /**
     *  Set the size of the buffer used to receive response packets. RFC 1157 stipulates that an SNMP
     *  implementation must be able to receive packets of at least 484 bytes, so if you try to set the
     *  size to a value less than this, the receive buffer size will be set to 484 bytes. In addition,
     *  the maximum size of a UDP packet payload is 65535 bytes, so setting the buffer to a larger size
     *  will just waste memory. The default value is 512 bytes. The value may need to be increased if
     *  get-requests are issued for multiple OIDs.
     */
    public void setReceiveBufferSize(int receiveBufferSize)
    {
        if (receiveBufferSize >= 484)
            this.receiveBufferSize = receiveBufferSize;
        else
            this.receiveBufferSize = 484;
    }
    
    
    /**
     *  Returns the current size of the buffer used to receive response packets. 
     */
    public int getReceiveBufferSize()
    {
        return this.receiveBufferSize;
    }
    
}
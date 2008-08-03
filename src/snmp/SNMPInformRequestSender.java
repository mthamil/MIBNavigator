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



/**
 *  The class SNMPInformRequestSender implements a method for sending SNMPv2 inform request messages to a remote SNMP manager. 
 *  The approach is that from version 2c of SNMP, using no encryption of data. Communication occurs via UDP, using port 162, 
 *  the standard SNMP trap and inform request port, as the destination port.
 */
public class SNMPInformRequestSender
{  
    private DatagramSocket dSocket;
    
    /**
     *  Construct a new inform request sender object to send inform requests to remote SNMP hosts.
     */
    public SNMPInformRequestSender()
        throws SocketException
    {
        dSocket = new DatagramSocket();
    }
    

    /**
     *  Construct a new inform request sender object to send inform requests to remote SNMP hosts, binding to
     *  the specified local port.
     */
    public SNMPInformRequestSender(int localPort)
        throws SocketException
    {
        dSocket = new DatagramSocket(localPort);
    }
    
    /**
     *  Send the supplied SNMPv2 inform request pdu to the specified host, using the supplied version number
     *  and community name. 
     */
    public void sendInformRequest(int version, InetAddress hostAddress, String community, SNMPv2InformRequestPDU pdu)
        throws IOException
    {
        SNMPMessage message = new SNMPMessage(version, community, pdu);
        
        byte[] messageEncoding = message.getBEREncoding();
        
        /*
        System.out.println("Request Message bytes:");
        
        for (int i = 0; i < messageEncoding.length; ++i)
            System.out.print(hexByte(messageEncoding[i]) + " ");
        */
        
        DatagramPacket outPacket = new DatagramPacket(messageEncoding, messageEncoding.length, hostAddress, SNMPTrapSender.SNMP_TRAP_PORT);
        
        /*
        System.out.println("Message bytes length (out): " + outPacket.getLength());
        
        System.out.println("Message bytes (out):");
        for (int i = 0; i < messageEncoding.length; ++i)
        {
            System.out.print(hexByte(messageEncoding[i]) + " ");
        }
        System.out.println("\n");
        */
        
        dSocket.send(outPacket);
        
    }
    
    
    /**
     *  Send the supplied inform request pdu to the specified host, using the supplied community name and
     *  using 1 for the version field in the SNMP message.
     */
    public void sendInformRequest(InetAddress hostAddress, String community, SNMPv2InformRequestPDU pdu)
        throws IOException
    {
        int version = 1;
    
        sendInformRequest(version, hostAddress, community, pdu);
    }
    
    
   /* private String hexByte(byte b)
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
    
}
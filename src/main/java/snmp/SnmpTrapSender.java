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

import snmp.datatypes.sequence.SnmpMessage;
import snmp.datatypes.sequence.pdu.SnmpV1TrapPDU;
import snmp.datatypes.sequence.pdu.SnmpV2TrapPDU;




/**
 *  The class SNMPTrapSender implements an interface for sending SNMPv1 and SNMPv2 trap messages to a 
 *  remote SNMP manager. The approach is that from version 1 of SNMP, using no encryption of data. 
 *  Communication occurs via UDP, using port 162, the standard SNMP trap port, as the destination port.
 */
public class SnmpTrapSender
{
    public static final int SNMP_TRAP_PORT = 162;
    
    private DatagramSocket dSocket;
    
    /**
     *  Constructs a new trap sender object to send traps to remote SNMP hosts.
     */
    public SnmpTrapSender() throws SocketException
    {
        dSocket = new DatagramSocket();
    }
    

    /**
     *  Constructs a new trap sender object to send traps to remote SNMP hosts, binding to
     *  the specified local port.
     */
    public SnmpTrapSender(int localPort) throws SocketException
    {
        dSocket = new DatagramSocket(localPort);
    }
    

    /**
     *  Sends the supplied SNMPv1 trap PDU to the specified host, using the supplied version number
     *  and community name. Use version = 0 for SNMP version 1, or version = 1 for enhanced 
     *  capabilities provided through RFC 1157.
     */
    public void sendTrap(SnmpVersion version, InetAddress hostAddress, String community, SnmpV1TrapPDU pdu)
        throws IOException
    {
        SnmpMessage message = new SnmpMessage(version, community, pdu);
        
        byte[] messageEncoding = message.encode();
        
        /*
        System.out.println("Request Message bytes:");
        
        for (int i = 0; i < messageEncoding.length; ++i)
            System.out.print(hexByte(messageEncoding[i]) + " ");
        */
        
        DatagramPacket outPacket = new DatagramPacket(messageEncoding, messageEncoding.length, hostAddress, SNMP_TRAP_PORT);
        
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
     *  Sends the supplied trap PDU to the specified host, using the supplied community name and
     *  using 0 for the version field in the SNMP message (corresponding to SNMP version 1).
     */
    public void sendTrap(InetAddress hostAddress, String community, SnmpV1TrapPDU pdu)
        throws IOException
    {
        sendTrap(SnmpVersion.SNMPv1, hostAddress, community, pdu);
    }
    
    
    /**
     *  Sends the supplied SNMPv2 trap PDU to the specified host, using the supplied version number
     *  and community name. 
     */
    public void sendTrap(SnmpVersion version, InetAddress hostAddress, String community, SnmpV2TrapPDU pdu)
        throws IOException
    {
        SnmpMessage message = new SnmpMessage(version, community, pdu);
        
        byte[] messageEncoding = message.encode();
        
        /*
        System.out.println("Request Message bytes:");
        
        for (int i = 0; i < messageEncoding.length; ++i)
            System.out.print(hexByte(messageEncoding[i]) + " ");
        */
        
        DatagramPacket outPacket = new DatagramPacket(messageEncoding, messageEncoding.length, hostAddress, SNMP_TRAP_PORT);
        
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
     *  Sends the supplied trap PDU to the specified host, using the supplied community name and
     *  using 1 for the version field in the SNMP message.
     */
    public void sendTrap(InetAddress hostAddress, String community, SnmpV2TrapPDU pdu)
        throws IOException
    {
        sendTrap(SnmpVersion.SNMPv2, hostAddress, community, pdu);
    }
  
}
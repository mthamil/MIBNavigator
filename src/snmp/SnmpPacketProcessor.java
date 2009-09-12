/*
 * SNMP Package
 *
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

import java.net.DatagramPacket;
import java.net.InetAddress;

import snmp.datatypes.SnmpBERCodec;
import snmp.datatypes.sequence.SnmpMessage;
import snmp.error.SnmpBadValueException;

/**
 * Converts between SNMP messages and datagram packets.
 */
public class SnmpPacketProcessor
{
	/**
	 * Creates an outgoing datagram packet from an SNMP message.
	 * @param message an SNMP message
	 * @param address the destination IP address
	 * @param port the destination port
	 * @return an encoded outgoing datagram packet
	 */
    public DatagramPacket createPacket(SnmpMessage message, InetAddress address, int port)
    {
    	byte[] messageEncoding = message.encode();
    	return new DatagramPacket(messageEncoding, messageEncoding.length, address, port);
    }
    
    /**
     * Creates an SNMP message from an incoming datagram packet.
     * @param packet a received datagram packet
     * @return a decoded SNMP message
     * @throws SnmpBadValueException
     */
    public SnmpMessage createMessage(DatagramPacket packet) throws SnmpBadValueException
    {
    	byte[] encodedMessage = packet.getData();
    	return new SnmpMessage(SnmpBERCodec.extractNextTLV(encodedMessage,0).value);
    }
}

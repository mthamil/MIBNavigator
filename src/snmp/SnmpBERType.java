/**
 * SNMP Package
 *
 * Copyright (C) 2006, Matt Hamilton <mhamilton2383@comcast.net>
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package snmp;

/**
 * A constant that identifies an SNMP BER data type.
 */
public enum SnmpBERType
{
    SnmpUnknownObject((byte)0x00),

    // Primitive ASN.1 data types
    SnmpInteger((byte)0x02),
    SnmpBitString((byte)0x03),
    SnmpOctetString((byte)0x04),
    SnmpNull((byte)0x05),
    SnmpObjectIdentifier((byte)0x06),
    
    // Complex ASN.1 data types
    SnmpSequence((byte)0x30),

    // Primitive SNMP data types
    SnmpIpAddress((byte)0x40),
    SnmpCounter32((byte)0x41),
    SnmpGauge32((byte)0x42),
    SnmpTimeTicks((byte)0x43),
    SnmpOpaque((byte)0x44),
    SnmpNsapAddress((byte)0x45),
    SnmpCounter64((byte)0x46),
    SnmpUInteger32((byte)0x47),

    // BER PDU Message types
    SnmpGetRequest((byte)0xA0),
    SnmpGetNextRequest((byte)0xA1),
    SnmpGetResponse((byte)0xA2),
    SnmpSetRequest((byte)0xA3),
    SnmpTrap((byte)0xA4),
    SnmpV2BulkRequest((byte)0xA5),
    SnmpV2InformRequest((byte)0xA6),
    SnmpV2Trap((byte)0xA7);
    
    private byte byteValue;
    
    private SnmpBERType(byte newByte)
    {
        byteValue = newByte;
    }
    
    
    /**
     * Gets the byte equivalent of an SNMP type identifier.
     */
    public byte getByte()
    {
        return byteValue;
    }
    
    /**
     * Gets the SNMPBERType instance corresponding to the given byte value.
     * If no such instance exists, an exception is thrown.
     * 
     * @param byteVal the byte value of an SNMPBERType
     */
    public static SnmpBERType getInstance(byte byteVal)
    {
        switch (byteVal)
        {
            case 0x00:
                return SnmpUnknownObject;
            case 0x02:
                return SnmpInteger;
            case 0x03:
                return SnmpBitString;
            case 0x04:
                return SnmpOctetString;
            case 0x05:
                return SnmpNull;
            case 0x06:
                return SnmpObjectIdentifier;
                
            case 0x30:
                return SnmpSequence;
                
            case 0x40:
                return SnmpIpAddress;
            case 0x41:
                return SnmpCounter32;
            case 0x42:
                return SnmpGauge32;
            case 0x43:
                return SnmpTimeTicks;
            case 0x44:
                return SnmpOpaque;
            case 0x45:
                return SnmpNsapAddress;
            case 0x46:
                return SnmpCounter64;
            case 0x47:
                return SnmpUInteger32;
                
            case (byte) 0xA0:
                return SnmpGetRequest;
            case (byte) 0xA1:
                return SnmpGetNextRequest;
            case (byte) 0xA2:
                return SnmpGetResponse;
            case (byte) 0xA3:
                return SnmpSetRequest;
            case (byte) 0xA4:
                return SnmpTrap;
            case (byte) 0xA5:
                return SnmpV2BulkRequest;
            case (byte) 0xA6:
                return SnmpV2InformRequest;
            case (byte) 0xA7:
                return SnmpV2Trap;
            default:
                throw new IllegalArgumentException("No corresponding instance.");
        }
    }
    
}
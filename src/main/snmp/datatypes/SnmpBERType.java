/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 * Copyright (C) 2010, Matt Hamilton <matthamilton@live.com>
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

package snmp.datatypes;

import java.util.HashMap;
import java.util.Map;

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
    private byte byteValue;
    
    
    private static final Map<Byte, SnmpBERType> typeMap = new HashMap<Byte, SnmpBERType>(SnmpBERType.values().length);
    static
    {
    	// Map enum instances to their byte values.
        for (SnmpBERType type : SnmpBERType.values())
        	typeMap.put(Byte.valueOf(type.getByte()), type);
    }
    
    /**
     * Gets the SNMP BER Type instance corresponding to the given byte value.
     * If no such instance exists, an exception is thrown.
     * 
     * @param byteVal the byte value of an SNMP BER Type
     */
    public static SnmpBERType getInstance(byte byteVal)
    {
    	Byte b = Byte.valueOf(byteVal);
    	if (typeMap.containsKey(b))
    		return typeMap.get(b);
    	
    	throw new IllegalArgumentException("No corresponding instance.");
    }
    
}
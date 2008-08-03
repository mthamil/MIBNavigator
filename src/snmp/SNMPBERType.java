/**
 * SNMP Package
 *
 * Copyright (C) 2006, Matt Hamilton <matthew.hamilton@washburn.edu>
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
public enum SNMPBERType
{
    SNMP_UNKNOWN_OBJECT((byte)0x00),

    //Primitive ASN.1 data types
    SNMP_INTEGER((byte)0x02),
    SNMP_BITSTRING((byte)0x03),
    SNMP_OCTETSTRING((byte)0x04),
    SNMP_NULL((byte)0x05),
    SNMP_OBJECT_IDENTIFIER((byte)0x06),
    
    //Constructed ASN.1 data types
    SNMP_SEQUENCE((byte)0x30),

    //Primitive SNMP data types
    SNMP_IPADDRESS((byte)0x40),
    SNMP_COUNTER32((byte)0x41),
    SNMP_GAUGE32((byte)0x42),
    SNMP_TIMETICKS((byte)0x43),
    SNMP_OPAQUE((byte)0x44),
    SNMP_NSAPADDRESS((byte)0x45),
    SNMP_COUNTER64((byte)0x46),
    SNMP_UINTEGER32((byte)0x47),

    //BER PDU Message types
    SNMP_GET_REQUEST((byte)0xA0),
    SNMP_GET_NEXT_REQUEST((byte)0xA1),
    SNMP_GET_RESPONSE((byte)0xA2),
    SNMP_SET_REQUEST((byte)0xA3),
    SNMP_TRAP((byte)0xA4),
    SNMPv2_BULK_REQUEST((byte)0xA5),
    SNMPv2_INFORM_REQUEST((byte)0xA6),
    SNMPv2_TRAP((byte)0xA7);
    
    private byte byteValue;
    
    private SNMPBERType(byte newByte)
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
   /* public static SNMPBERType getInstance(byte byteVal) 
    {
        for (SNMPBERType type : SNMPBERType.values()) 
        {
           if (type.getByte() == byteVal) 
              return type;
        }

        throw new IllegalArgumentException("No corresponding instance.");          
    }*/
    
    
    /**
     * Gets the SNMPBERType instance corresponding to the given byte value.
     * If no such instance exists, an exception is thrown.
     * 
     * @param byteVal the byte value of an SNMPBERType
     */
    public static SNMPBERType getInstance(byte byteVal)
    {
        switch (byteVal)
        {
            case 0x00:
                return SNMP_UNKNOWN_OBJECT;
            case 0x02:
                return SNMP_INTEGER;
            case 0x03:
                return SNMP_BITSTRING;
            case 0x04:
                return SNMP_OCTETSTRING;
            case 0x05:
                return SNMP_NULL;
            case 0x06:
                return SNMP_OBJECT_IDENTIFIER;
                
            case 0x30:
                return SNMP_SEQUENCE;
                
            case 0x40:
                return SNMP_IPADDRESS;
            case 0x41:
                return SNMP_COUNTER32;
            case 0x42:
                return SNMP_GAUGE32;
            case 0x43:
                return SNMP_TIMETICKS;
            case 0x44:
                return SNMP_OPAQUE;
            case 0x45:
                return SNMP_NSAPADDRESS;
            case 0x46:
                return SNMP_COUNTER64;
            case 0x47:
                return SNMP_UINTEGER32;
                
            case (byte) 0xA0:
                return SNMP_GET_REQUEST;
            case (byte) 0xA1:
                return SNMP_GET_NEXT_REQUEST;
            case (byte) 0xA2:
                return SNMP_GET_RESPONSE;
            case (byte) 0xA3:
                return SNMP_SET_REQUEST;
            case (byte) 0xA4:
                return SNMP_TRAP;
            case (byte) 0xA5:
                return SNMPv2_BULK_REQUEST;
            case (byte) 0xA6:
                return SNMPv2_INFORM_REQUEST;
            case (byte) 0xA7:
                return SNMPv2_TRAP;
            default:
                throw new IllegalArgumentException("No corresponding instance.");
        }
    }
    
    
    
    public static void main(String[] args)
    {
        byte test = SNMPBERType.SNMP_GAUGE32.getByte();
        SNMPBERType test2 = SNMPBERType.getInstance((byte)0xA7);
    }
}
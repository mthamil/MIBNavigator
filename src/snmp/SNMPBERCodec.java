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


/** 
 *  SNMPBERCodec defines methods for converting from ASN.1 BER encoding to SNMPObject subclasses. The extraction
 *  process usually produces a tree structure of objects with an SNMPSequence object at the root; this
 *  is the usual behavior when a received encoded message is received from an SNMP device.
 */
public class SNMPBERCodec
{
    
    // *** Begin SNMP Data Types ***
    /*public static final byte SNMP_UNKNOWN_OBJECT = 0x00;
    
    public static final byte SNMP_INTEGER = 0x02;
    public static final byte SNMP_BITSTRING = 0x03;
    public static final byte SNMP_OCTETSTRING = 0x04;
    public static final byte SNMP_NULL = 0x05;
    public static final byte SNMP_OBJECT_IDENTIFIER = 0x06;
    public static final byte SNMP_SEQUENCE = 0x30;
    
    public static final byte SNMP_IPADDRESS = (byte)0x40;
    public static final byte SNMP_COUNTER32 = (byte)0x41;
    public static final byte SNMP_GAUGE32 = (byte)0x42;
    public static final byte SNMP_TIMETICKS = (byte)0x43;
    public static final byte SNMP_OPAQUE = (byte)0x44;
    public static final byte SNMP_NSAPADDRESS = (byte)0x45;
    public static final byte SNMP_COUNTER64 = (byte)0x46;
    public static final byte SNMP_UINTEGER32 = (byte)0x47;
    
    public static final byte SNMP_GET_REQUEST = (byte)0xA0;
    public static final byte SNMP_GET_NEXT_REQUEST = (byte)0xA1;
    public static final byte SNMP_GET_RESPONSE = (byte)0xA2;
    public static final byte SNMP_SET_REQUEST = (byte)0xA3;
    public static final byte SNMP_TRAP = (byte)0xA4;

    public static final byte SNMPv2_BULK_REQUEST = (byte)0xA5;
    public static final byte SNMPv2_INFORM_REQUEST = (byte)0xA6;
    public static final byte SNMPv2_TRAP = (byte)0xA7;
    //  *** End SNMP Data Types ***
    
    public static final byte SNMPv2pCOMMUNICATION = (byte)0xA2;
    public static final byte SNMPv2pAUTHORIZEDMESSAGE = (byte)0xA1;
    public static final byte SNMPv2pENCRYPTEDMESSAGE = (byte)0xA1;
    public static final byte SNMPv2pENCRYPTEDDATA = (byte)0xA1;
*/
     
    /** 
     *  Extracts an SNMP object given its type, length, and value triple as an SNMPTLV object.
     *  Called by SNMPObject subclass constructors.
     *  
     *  @throws SNMPBadValueException Indicates byte array in value field is not interpretable for
     *  the specified SNMP object type.
     */
    public static SNMPObject extractEncoding(SNMPTLV theTLV)
        throws SNMPBadValueException
    {
        switch (theTLV.tag)
        {
            case SNMP_INTEGER:
                return new SNMPInteger(theTLV.value);
            
            case SNMP_SEQUENCE:
                return new SNMPSequence(theTLV.value);
            
            case SNMP_OBJECT_IDENTIFIER:
                return new SNMPObjectIdentifier(theTLV.value);
            
            case SNMP_OCTETSTRING:
                return new SNMPOctetString(theTLV.value);
            
            case SNMP_BITSTRING:
                return new SNMPBitString(theTLV.value);
            
            case SNMP_IPADDRESS:
                return new SNMPIPAddress(theTLV.value);
            
            case SNMP_COUNTER32:
                return new SNMPCounter32(theTLV.value);
            
            case SNMP_GAUGE32:
                return new SNMPGauge32(theTLV.value);
            
            case SNMP_TIMETICKS:
                return new SNMPTimeTicks(theTLV.value);
            
            case SNMP_NSAPADDRESS:
                return new SNMPNSAPAddress(theTLV.value);
            
            case SNMP_COUNTER64:
                return new SNMPCounter64(theTLV.value);
            
            case SNMP_UINTEGER32:
                return new SNMPUInteger32(theTLV.value);
                
            //fall through
            case SNMP_GET_REQUEST:
            case SNMP_GET_NEXT_REQUEST:
            case SNMP_GET_RESPONSE:
            case SNMP_SET_REQUEST:
                return new SNMPPDU(theTLV.value, theTLV.tag);
            
            case SNMP_TRAP:
                return new SNMPv1TrapPDU(theTLV.value);
            
            case SNMPv2_TRAP:
                return new SNMPv2TrapPDU(theTLV.value);
            
            case SNMPv2_INFORM_REQUEST:
                return new SNMPv2InformRequestPDU(theTLV.value);
            
            //fall through
            case SNMP_NULL: 
            case SNMP_OPAQUE:
                return new SNMPNull();
            
            default:
                return new SNMPUnknownObject(theTLV.value);
        }
    }
    
    
    /** 
     *  Extracts the type, length and value of the SNMP object whose BER encoding begins at the
     *  specified position in the given byte array.
     *  
     *  @throws SNMPBadValueException if there is any problem with TLV extraction.
     */
    public static SNMPTLV extractNextTLV(byte[] enc, int position)
        throws SNMPBadValueException
    {
        SNMPTLV nextTLV = new SNMPTLV();
        int currentPos = position;
        
        try
        {
            // get tag
            
            /*
            if ((enc[currentPos] % 32) < 31)
            {
                // single byte tag; extract value
                nextTLV.tag = (int)(enc[currentPos]);
            }
            else
            {
                // multiple byte tag; for now, just return value in subsequent bytes ...
                // but need to think about universal / application fields, etc...
                nextTLV.tag = 0;
                
                do
                {
                    currentPos++;
                    nextTLV.tag = nextTLV.tag * 128 + (int)(enc[currentPos] % 128);
                }
                while ((enc[currentPos]/128) >= 1);
            }
            */
            
            // single byte tag; extract value
            try
            {
                nextTLV.tag = SNMPBERType.getInstance(enc[currentPos]);
            }
            catch (IllegalArgumentException e)  // if no corresponding instance exists
            {
                nextTLV.tag = SNMPBERType.SNMP_UNKNOWN_OBJECT;
            }
            currentPos++;    // now at start of length info
            
            // get length of data
            
            int dataLength;
            
            int unsignedValue = enc[currentPos];
            if (unsignedValue < 0)
                unsignedValue += 256;
                
            if ((unsignedValue / 128) < 1)
            {
                // single byte length; extract value
                dataLength = unsignedValue;
            }
            else
            {
                // multiple byte length; first byte's value (minus first bit) is # of length bytes
                int numBytes = (unsignedValue % 128);
                
                dataLength = 0;
                
                for (int i = 0; i < numBytes; i++)
                {
                    currentPos++;
                    unsignedValue = enc[currentPos];
                    if (unsignedValue < 0)
                        unsignedValue += 256;
                    dataLength = dataLength * 256 + unsignedValue;
                }
            }
            
            currentPos++;    // now at start of data
            
            // set total length
            nextTLV.totalLength = currentPos - position + dataLength;
            
            // extract data portion
            
            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            outBytes.write(enc, currentPos, dataLength);
            nextTLV.value = outBytes.toByteArray();
                    
            return nextTLV;
        
        }
        catch (IndexOutOfBoundsException e)
        {
            // whatever the exception, throw an SNMPBadValueException
            throw new SNMPBadValueException("Problem while decoding SNMP: packet truncated or corrupt");
        }
        catch (Exception e)
        {
            // whatever the exception, throw an SNMPBadValueException
            throw new SNMPBadValueException("Problem while decoding SNMP");
        }
            
    }
    
    
    /** 
     *  Utility function for encoding a length as a BER byte sequence
     */
    public static byte[] encodeLength(int length)
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        
        // see if can be represented in single byte
        // don't forget the first bit is the "long field test" bit!!
        if (length < 128)
        {
            byte[] len = {(byte)length};
            outBytes.write(len, 0, 1);
        }
        else
        {
            // too big for one byte
            // see how many are needed:
            int numBytes = 0;
            int temp = length;
            while (temp > 0)
            {
                ++numBytes;
                temp = (int)Math.floor(temp / 256);
            }
            
            byte num = (byte)numBytes;
            num += 128;        // set the "long format" bit
            outBytes.write(num);
            
            byte[] len = new byte[numBytes];
            for (int i = numBytes - 1; i >= 0; --i)
            {
                len[i] = (byte)(length % 256);
                length = (int)Math.floor(length / 256);
            }
            outBytes.write(len, 0, numBytes);
            
        }
        
        return outBytes.toByteArray();
    }
    
}
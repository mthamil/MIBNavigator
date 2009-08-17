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
 *  SNMPBERCodec defines methods for converting from ASN.1 Basic Encoding Rules encoding to 
 *  SNMPObject subclasses. The extraction process usually produces a tree structure of objects 
 *  with an SNMPSequence object at the root; this is the usual behavior when a received encoded 
 *  message is received from an SNMP device.
 */
public class SnmpBERCodec
{    
    /** 
     *  Extracts an SNMP object given its type, length, and value triple as an SNMP TLV object.
     *  Called by SNMP Object subclass constructors.
     *  
     *  @throws SnmpBadValueException Indicates byte array in value field is not interpretable for
     *  the specified SNMP object type.
     */
    public static SnmpObject decode(SnmpTLV tlv)
        throws SnmpBadValueException
    {
        switch (tlv.tag)
        {
            case SnmpInteger:
                return new SnmpInteger(tlv.value);
            
            case SnmpSequence:
                return new SnmpSequence(tlv.value);
            
            case SnmpObjectIdentifier:
                return new SnmpObjectIdentifier(tlv.value);
            
            case SnmpOctetString:
                return new SnmpOctetString(tlv.value);
            
            case SnmpBitString:
                return new SnmpBitString(tlv.value);
            
            case SnmpIpAddress:
                return new SnmpIpAddress(tlv.value);
            
            case SnmpCounter32:
                return new SnmpCounter32(tlv.value);
            
            case SnmpGauge32:
                return new SnmpGauge32(tlv.value);
            
            case SnmpTimeTicks:
                return new SnmpTimeTicks(tlv.value);
            
            case SnmpNsapAddress:
                return new SnmpNSAPAddress(tlv.value);
            
            case SnmpCounter64:
                return new SnmpCounter64(tlv.value);
            
            case SnmpUInteger32:
                return new SnmpUInteger32(tlv.value);
                
            // Fall through
            case SnmpGetRequest:
            case SnmpGetNextRequest:
            case SnmpGetResponse:
            case SnmpSetRequest:
                return new SnmpPDU(tlv.value, tlv.tag);
            
            case SnmpTrap:
                return new SnmpV1TrapPDU(tlv.value);
            
            case SnmpV2Trap:
                return new SnmpV2TrapPDU(tlv.value);
            
            case SnmpV2InformRequest:
                return new SnmpV2InformRequestPDU(tlv.value);
            
            // Fall through
            case SnmpNull: 
            case SnmpOpaque:
                return new SnmpNull();
            
            default:
                return new SnmpUnknownObject(tlv.value);
        }
    }
    
    
    /** 
     *  Extracts the type, length and value of the SNMP object whose BER encoding begins at the
     *  specified position in the given byte array.
     *  
     *  @throws SnmpBadValueException if there is any problem with TLV extraction.
     */
    public static SnmpTLV extractNextTLV(byte[] enc, int position)
        throws SnmpBadValueException
    {
        SnmpTLV nextTLV = new SnmpTLV();
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
                nextTLV.tag = SnmpBERType.getInstance(enc[currentPos]);
            }
            catch (IllegalArgumentException e)  // if no corresponding instance exists
            {
                nextTLV.tag = SnmpBERType.SnmpUnknownObject;
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
            nextTLV.length = currentPos - position + dataLength;
            
            // extract data portion
            
            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            outBytes.write(enc, currentPos, dataLength);
            nextTLV.value = outBytes.toByteArray();
                    
            return nextTLV;
        
        }
        catch (IndexOutOfBoundsException e)
        {
            // whatever the exception, throw an SNMPBadValueException
            throw new SnmpBadValueException("Problem while decoding SNMP: packet truncated or corrupt");
        }
        catch (Exception e)
        {
            // whatever the exception, throw an SNMPBadValueException
            throw new SnmpBadValueException("Problem while decoding SNMP");
        }
            
    }
    
    
    /** 
     *  Encodes a length as a BER byte sequence.
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
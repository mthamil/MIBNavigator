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


/**
 *  Class representing a general string of bits.
 */
public class SNMPBitString extends SNMPOctetString
{
    protected SNMPBERType tag = SNMPBERType.SNMP_BITSTRING;
        
    /**
     *  Create a zero-length bit string.
     */
    public SNMPBitString()
    {
        this.data = new byte[0];
    }
    
    
    /**
     *  Create a bit string from the bytes of the supplied String.
     */
    public SNMPBitString(String stringData)
    {
        this.data = stringData.getBytes();
    }
    
    
    /**
     *  Create a bit string from the supplied byte array. The array may be either
     *  user-supplied, or part of a retrieved BER encoding. Note that the BER encoding
     *  of the data of a bit string is just the raw bytes.
     */
    public SNMPBitString(byte[] enc)
    {
        extractFromBEREncoding(enc);
    }
    
}
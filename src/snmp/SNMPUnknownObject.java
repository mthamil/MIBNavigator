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
 *  Used when an unknown SNMP object type is encountered. Just takes a byte array
 *  for its constructor, and uses this as raw bytes.
 */
public class SNMPUnknownObject extends SNMPObject
{
    private byte[] data;
    
    protected SNMPBERType tag = SNMPBERType.SNMP_UNKNOWN_OBJECT;    
    
    /**
     *  Just takes a byte array, and uses this as raw bytes.
     */
    public SNMPUnknownObject(byte[] enc)
    {
        data = enc;
    }
    
    
    /**
     *  Return a byte array containing the raw bytes supplied.
     */
    public Object getValue()
    {
        return data;
    }
    
    
    /**
     *  Takes a byte array containing the raw bytes stored as the value.
     */
    public void setValue(Object data)
        throws SNMPBadValueException
    {
        if (data instanceof byte[])
            this.data = (byte[])data;
        else
            throw new SNMPBadValueException(" Unknown Object: bad object supplied to set value ");
    }
    
    
    /**
     *  Return the BER encoding of this object.
     */
    protected byte[] getBEREncoding()
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        
        // calculate encoding for length of data
        byte[] len = SNMPBERCodec.encodeLength(data.length);
        
        // encode T,L,V info
        outBytes.write(tag.getByte());
        outBytes.write(len, 0, len.length);
        outBytes.write(data, 0, data.length);
    
        return outBytes.toByteArray();
    }
    
    
    /**
     *  Return String created from raw bytes of this object.
     */
    public String toString()
    {
        return new String(data);
    }
    
}
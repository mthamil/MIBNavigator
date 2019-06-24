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

package snmp.datatypes;

import java.io.*;
import java.util.Arrays;

import snmp.error.SnmpBadValueException;


/**
 *  Used when an unknown SNMP object type is encountered. Just takes a byte array
 *  for its constructor, and uses this as raw bytes.
 */
public class SnmpUnknownObject extends SnmpObject
{
    private byte[] data;
    
    protected SnmpBERType tag = SnmpBERType.SnmpUnknownObject;    
    
    /**
     *  Takes a byte array and uses it as raw bytes.
     */
    public SnmpUnknownObject(byte[] encoding)
    {
        data = Arrays.copyOf(encoding, encoding.length);
    }
    
    
    /**
     *  Returns a byte array containing the raw bytes supplied.
     */
    @Override
    public Object getValue()
    {
        return data;
    }
    
    
    /**
     *  Takes a byte array containing the raw bytes stored as the value.
     */
    @Override
    public void setValue(Object data)
        throws SnmpBadValueException
    {
        if (data instanceof byte[])
            this.data = Arrays.copyOf((byte[])data, ((byte[])data).length);
        else
            throw new SnmpBadValueException(" Unknown Object: bad object supplied to set value ");
    }
    
    
    /**
     *  Returns the BER encoding of this object.
     */
    @Override
    public byte[] encode()
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        
        // calculate encoding for length of data
        byte[] len = SnmpBERCodec.encodeLength(data.length);
        
        // encode T,L,V info
        outBytes.write(tag.getByte());
        outBytes.write(len, 0, len.length);
        outBytes.write(data, 0, data.length);
    
        return outBytes.toByteArray();
    }
    
    
    /**
     *  Returns a String created from raw bytes of this object.
     */
    @Override
    public String toString()
    {
        return new String(data);
    }
    
}
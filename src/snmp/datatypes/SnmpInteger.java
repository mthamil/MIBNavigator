/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
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

package snmp.datatypes;

import java.math.*;
import java.io.*;

import snmp.error.SnmpBadValueException;


/** 
 *  Defines an arbitrarily-sized integer value; there is no limit on size due to the use
 *  of Java.lang.BigInteger to store the value internally. For an indicator which "pegs" at its 
 *  maximum value if initialized with a larger value, use SNMPGauge32; for a counter which wraps,
 *  use SNMPCounter32 or SNMPCounter64.
 *  @see snmp.datatypes.SnmpCounter32
 *  @see snmp.datatypes.SnmpGauge32
 *  @see snmp.datatypes.SnmpCounter64
 */
public class SnmpInteger extends SnmpObject
{
    protected BigInteger value;
    protected SnmpBERType tag = SnmpBERType.SnmpInteger;
    
    /** 
     *  Initializes value to 0.
     */
    public SnmpInteger()
    {
        this(0);    // initialize value to 0
    }
    

    public SnmpInteger(long value)
    {
        this.value = new BigInteger(Long.valueOf(value).toString());
    }
    
    
    public SnmpInteger(BigInteger value)
    {
        this.value = value;
    }
    
    
    /** 
     *  Initializes from the BER encoding, usually received in a response from 
     *  an SNMP device responding to an SNMPGetRequest.
     *  
     *  @throws SnmpBadValueException Indicates an invalid BER encoding supplied. Shouldn't
     *  occur in normal operation, i.e., when valid responses are received from devices.
     */
    public SnmpInteger(byte[] encodedValue)
        throws SnmpBadValueException
    {
        decodeValue(encodedValue);
    }    
    

    /** 
     *  Returns a java.lang.BigInteger object with the current value.
     */
    public Object getValue()
    {
        return value;
    }
    
    
    /** 
     *  Sets the value with an instance of java.lang.Integer or
     *  java.lang.BigInteger.
     *  
     *  @throws SnmpBadValueException Indicates an incorrect object type supplied.
     */
    public void setValue(Object newValue)
        throws SnmpBadValueException
    {
        if (newValue instanceof BigInteger)
            value = (BigInteger)newValue;
        else if (newValue instanceof Integer)
            value = new BigInteger(((Integer)newValue).toString());
        else if (newValue instanceof String)
            value = new BigInteger((String)newValue);
        else
            
            throw new SnmpBadValueException(" Integer: bad object supplied to set value ");
    }
    
    
    /** 
     *  Returns the full BER encoding (type, length, value) of the SNMPInteger subclass.
     */
    public byte[] encode()
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        
        // write contents    
        // boy, was THIS easy! Love that Java!
        byte[] data = value.toByteArray();
        
        // calculate encoding for length of data
        byte[] len = SnmpBERCodec.encodeLength(data.length);
        
        // encode T,L,V info
        outBytes.write(tag.getByte());
        outBytes.write(len, 0, len.length);
        outBytes.write(data, 0, data.length);
    
        return outBytes.toByteArray();
    }
    
    
    /** 
     *  Extracts a value from the BER encoding of the value. Called in constructors for
     *  SNMPInteger subclasses.
     *  
     *  @throws SnmpBadValueException Indicates an invalid BER encoding supplied. Shouldn't
     *  occur in normal operation, i.e., when valid responses are received from devices.
     */
    public void decodeValue(byte[] encodedValue) throws SnmpBadValueException
    {
        try
        {
            value = new BigInteger(encodedValue);
        }
        catch (NumberFormatException e)
        {
            throw new SnmpBadValueException(" Integer: bad BER encoding supplied to set value ");
        }
    }
    

    public String toString()
    {
        return value.toString();
    }
    

    public String toString(int radix)
    {
        return value.toString(radix);
    }
    
}
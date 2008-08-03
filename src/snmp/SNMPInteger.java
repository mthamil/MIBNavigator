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

import java.math.*;
import java.io.*;


/** 
 *  Defines an arbitrarily-sized integer value; there is no limit on size due to the use
 *  of Java.lang.BigInteger to store the value internally. For an indicator which "pegs" at its 
 *  maximum value if initialized with a larger value, use SNMPGauge32; for a counter which wraps,
 *  use SNMPCounter32 or SNMPCounter64.
 *  @see snmp.SNMPCounter32
 *  @see snmp.SNMPGauge32
 *  @see snmp.SNMPCounter64
 */
public class SNMPInteger extends SNMPObject
{
    protected BigInteger value;
    protected SNMPBERType tag = SNMPBERType.SNMP_INTEGER;
    
    /** 
     *  Initialize value to 0.
     */
    public SNMPInteger()
    {
        this(0);    // initialize value to 0
    }
    

    public SNMPInteger(long value)
    {
        this.value = new BigInteger(new Long(value).toString());
    }
    
    
    public SNMPInteger(BigInteger value)
    {
        this.value = value;
    }
    
    
    /** 
     *  Used to initialize from the BER encoding, usually received in a response from 
     *  an SNMP device responding to an SNMPGetRequest.
     *  
     *  @throws SNMPBadValueException Indicates an invalid BER encoding supplied. Shouldn't
     *  occur in normal operation, i.e., when valid responses are received from devices.
     */
    protected SNMPInteger(byte[] enc)
        throws SNMPBadValueException
    {
        extractValueFromBEREncoding(enc);
    }    
    

    /** 
     *  Returns a java.lang.BigInteger object with the current value.
     */
    public Object getValue()
    {
        return value;
    }
    
    
    /** 
     *  Used to set the value with an instance of java.lang.Integer or
     *  java.lang.BigInteger.
     *  
     *  @throws SNMPBadValueException Indicates an incorrect object type supplied.
     */
    public void setValue(Object newValue)
        throws SNMPBadValueException
    {
        if (newValue instanceof BigInteger)
            value = (BigInteger)newValue;
        else if (newValue instanceof Integer)
            value = new BigInteger(((Integer)newValue).toString());
        else if (newValue instanceof String)
            value = new BigInteger((String)newValue);
        else
            
            throw new SNMPBadValueException(" Integer: bad object supplied to set value ");
    }
    
    
    /** 
     *  Returns the full BER encoding (type, length, value) of the SNMPInteger subclass.
     */
    protected byte[] getBEREncoding()
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        
        // write contents    
        // boy, was THIS easy! Love that Java!
        byte[] data = value.toByteArray();
        
        // calculate encoding for length of data
        byte[] len = SNMPBERCodec.encodeLength(data.length);
        
        // encode T,L,V info
        outBytes.write(tag.getByte());
        outBytes.write(len, 0, len.length);
        outBytes.write(data, 0, data.length);
    
        return outBytes.toByteArray();
    }
    
    
    /** 
     *  Used to extract a value from the BER encoding of the value. Called in constructors for
     *  SNMPInteger subclasses.
     *  
     *  @throws SNMPBadValueException Indicates an invalid BER encoding supplied. Shouldn't
     *  occur in normal operation, i.e., when valid responses are received from devices.
     */
    public void extractValueFromBEREncoding(byte[] enc)
        throws SNMPBadValueException
    {
        try
        {
            value = new BigInteger(enc);
        }
        catch (NumberFormatException e)
        {
            throw new SNMPBadValueException(" Integer: bad BER encoding supplied to set value ");
        }
    }
    

    public String toString()
    {
        return value.toString();
        // return new String(value.toString());
    }
    

    public String toString(int radix)
    {
        return value.toString(radix);
        // return new String(value.toString());
    }
    
}
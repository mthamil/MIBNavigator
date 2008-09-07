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


/** 
 *  Defines a 32-bit unsigned integer value; wraps if initialized with a larger value.
 *  @see snmp.SNMPInteger
 */
public class SNMPUInteger32 extends SNMPInteger
{
    // maximum value is 2^32 - 1
    private static BigInteger maxValue = new BigInteger("4294967295");
    
    /**
     *  Initializes value to 0.
     */
    public SNMPUInteger32()
    {
        this(0);    // initialize value to 0
    }
    
    
    /**
     *  Initializes value to newValue; wrap if newValue too big for 32 bits.
     */
    public SNMPUInteger32(long newValue)
    {
        tag = SNMPBERType.SNMP_UINTEGER32;
        
        value = new BigInteger(new Long(newValue).toString());
        
        // wrap if value > maxValue
        value = value.mod(maxValue);
    }
    
    
    /** 
     *  Initializes from the BER encoding, usually received in a response from 
     *  an SNMP device responding to an SNMPGetRequest.
     *  
     *  @throws SNMPBadValueException Indicates an invalid BER encoding supplied. Shouldn't
     *  occur in normal operation, i.e., when valid responses are received from devices.
     */
    protected SNMPUInteger32(byte[] enc)
        throws SNMPBadValueException
    {
        tag = SNMPBERType.SNMP_UINTEGER32;
        
        extractValueFromBEREncoding(enc);
        
        // wrap if value > maxValue
        value = value.mod(maxValue);
    }
    
    
    /** 
     *  Sets the value with an instance of java.lang.Integer or
     *  java.lang.BigInteger. The value of the constructed SNMPUInteger32 object is the
     *  supplied value mod 2^32.
     *  
     *  @throws SNMPBadValueException Indicates an incorrect object type supplied.
     */
    public void setValue(Object newValue)
        throws SNMPBadValueException
    {
        if (newValue instanceof BigInteger)
        {
            value = (BigInteger)newValue;
            value = value.mod(maxValue);    // wrap when value exceeds 2^32
        }
        else if (newValue instanceof Integer)
        {
            value = new BigInteger(newValue.toString());
            value = value.mod(maxValue);    // wrap when value exceeds 2^32
        }
        else if (newValue instanceof String)
        {
            value = new BigInteger((String)newValue);
            value = value.mod(maxValue);    // wrap when value exceeds 2^32
        }
        else
            throw new SNMPBadValueException(" Unsigned Integer: bad object supplied to set value ");
    }
    
}
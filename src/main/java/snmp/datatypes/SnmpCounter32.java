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

import java.math.*;

import snmp.error.SnmpBadValueException;


/** 
 *  Defines a 32-bit counter, whose value wraps if initialized with a larger
 *  value. For an indicator which "pegs" at its maximum value if initialized with
 *  a larger value, use SNMPGauge32; for a counter with a wider range, use SNMPCounter64.
 *  @see snmp.datatypes.SnmpGauge32
 *  @see snmp.datatypes.SnmpCounter64
 */
public class SnmpCounter32 extends SnmpInteger
{
    // maximum value is 2^32 - 1
    private static BigInteger maxValue = new BigInteger("4294967295");
    
    /** 
     *  Initializes value to 0.
     */
    public SnmpCounter32()
    {
        this(0);    // initialize value to 0
    }
    
    
    public SnmpCounter32(long newValue)
    {
        tag = SnmpBERType.SnmpCounter32;
        
        value = new BigInteger(Long.valueOf(newValue).toString());
        
        // wrap if value > maxValue
        value = value.mod(maxValue);
    }
    
    
    /** 
     *  Initializes from the BER encoding, usually received in a response from 
     *  an SNMP device responding to an SNMPGetRequest.
     *  
     *  @throws SnmpBadValueException Indicates an invalid BER encoding supplied. Shouldn't
     *  occur in normal operation, i.e., when valid responses are received from devices.
     */
    public SnmpCounter32(byte[] encodedValue)
        throws SnmpBadValueException
    {
        tag = SnmpBERType.SnmpCounter32;
        
        decodeValue(encodedValue);
        
        // wrap if value > maxValue
        value = value.mod(maxValue);
    }
    
    
    /** 
     *  Sets the value with an instance of java.lang.Integer or
     *  java.lang.BigInteger. The value of the constructed SNMPCounter32 object is the
     *  supplied value mod 2^32.
     *  
     *  @throws SnmpBadValueException Indicates an incorrect object type supplied.
     */
    @Override
    public void setValue(Object newValue)
        throws SnmpBadValueException
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
            throw new SnmpBadValueException(" Counter32: bad object supplied to set value ");
    }
}
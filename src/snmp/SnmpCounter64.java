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
 *  Defines a 64-bit counter, whose value wraps if initialized with a larger
 *  value. For an indicator which "pegs" at its maximum value if initialized with
 *  a larger value, use SNMPGauge32; for a counter with a smaller range, use SNMPCounter32.
 *  @see snmp.SnmpGauge32
 *  @see snmp.SnmpCounter32
 */
public class SnmpCounter64 extends SnmpInteger
{
    // maximum value is 2^64 - 1; using approximation!!
    private static BigInteger maxValue = new BigInteger("18446744070000000000");
    
    /** 
     *  Initializes value to 0.
     */
    public SnmpCounter64()
    {
        this(0);    // initialize value to 0
    }
    
    
    public SnmpCounter64(long newValue)
    {
        tag = SnmpBERType.SnmpCounter64;
        
        value = new BigInteger(new Long(newValue).toString());
        
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
    protected SnmpCounter64(byte[] enc)
        throws SnmpBadValueException
    {
        tag = SnmpBERType.SnmpCounter64;
        
        extractValueFromBEREncoding(enc);
        
        // wrap if value > maxValue
        value = value.mod(maxValue);
    }
    
    
    /** 
     *  Sets the value with an instance of java.lang.Integer or
     *  java.lang.BigInteger. The value of the constructed SNMPCounter64 object is the
     *  supplied value mod 2^64.
     *  
     *  @throws SnmpBadValueException Indicates an incorrect object type supplied.
    */
    public void setValue(Object newValue)
        throws SnmpBadValueException
    {
        if (newValue instanceof BigInteger)
        {
            value = (BigInteger)newValue;
            value = value.mod(maxValue);    // wrap when value exceeds 2^64
        }
        else if (newValue instanceof Integer)
        {
            value = new BigInteger(newValue.toString());
            value = value.mod(maxValue);    // wrap when value exceeds 2^64
        }
        else if (newValue instanceof String)
        {
            value = new BigInteger((String)newValue);
            value = value.mod(maxValue);    // wrap when value exceeds 2^64
        }
        else
            throw new SnmpBadValueException(" Counter64: bad object supplied to set value ");
    }
    
  
}
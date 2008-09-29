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
 *  Defines a 32-bit gauge, whose value "pegs" at the maximum if initialized with a larger
 *  value. For an indicator which wraps when it reaches its maximum value, use SNMPCounter32; 
 *  for a counter with a wider range, use SNMPCounter64.
 *  @see snmp.SnmpCounter32
 *  @see snmp.SnmpCounter64
 */
public class SnmpGauge32 extends SnmpInteger
{
    // maximum value is 2^32 - 1 (hack w/ 4*107...)
    private static BigInteger maxValue = new BigInteger("4294967295");
    
    /** 
     *  Initializes value to 0.
     */
    public SnmpGauge32()
    {
        this(0);    // initialize value to 0
    }
    
    
    public SnmpGauge32(long newValue)
    {
        tag = SnmpBERType.SnmpGauge32;
        
        value = new BigInteger(new Long(newValue).toString());
        
        // peg if value > maxValue
        value = value.min(maxValue);
    }
    
    
    /** 
     *  Initializes from the BER encoding, usually received in a response from 
     *  an SNMP device responding to an SNMPGetRequest.
     *  
     *  @throws SnmpBadValueException Indicates an invalid BER encoding supplied. Shouldn't
     *  occur in normal operation, i.e., when valid responses are received from devices.
     */
    protected SnmpGauge32(byte[] enc)
        throws SnmpBadValueException
    {
        tag = SnmpBERType.SnmpGauge32;
        
        extractValueFromBEREncoding(enc);
        
        // peg if value > maxValue
        value = value.min(maxValue);
    }
    
    
    /** 
     *  Sets the value with an instance of java.lang.Integer or
     *  java.lang.BigInteger. The value of the constructed SNMPGauge32 object is the
     *  supplied value or 2^32, whichever is less.
     * 
     *  @throws SnmpBadValueException Indicates an incorrect object type supplied.
     */
    public void setValue(Object newValue)
        throws SnmpBadValueException
    {
        // plateau when value hits maxValue
        if (newValue instanceof BigInteger)
        {
            value = (BigInteger)newValue;
            value = value.min(maxValue);
        }
        else if (newValue instanceof Integer)
        {
            value = new BigInteger(newValue.toString());
            value = value.min(maxValue);
        }
        else if (newValue instanceof String)
        {
            value = new BigInteger((String)newValue);
            value = value.min(maxValue);
        }
        else
            throw new SnmpBadValueException(" Gauge32: bad object supplied to set value ");
    }
    
    
}
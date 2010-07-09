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

import java.util.Arrays;

import snmp.error.SnmpBadValueException;


/**
 *  Defines class for holding physical 6-byte addresses.
 */
public class SnmpNSAPAddress extends SnmpOctetString
{
    // length limited to 6 octets
    
    /**
     *  Initializes address to 0.0.0.0.0.0.
     */
    public SnmpNSAPAddress()
    {
        tag = SnmpBERType.SnmpNsapAddress;
        
        // initialize to 0.0.0.0.0.0
        data = new byte[6];
        for (int i = 0; i < 6; i++)
            data[i] = 0;
    }
    
    
    public SnmpNSAPAddress(String string)
        throws SnmpBadValueException
    {
        tag = SnmpBERType.SnmpNsapAddress;
        
        data = SnmpNSAPAddress.parseNSAPAddress(string);
    }
    
    
    /** 
     *  Initializes from the BER encoding, as received in a response from 
     *  an SNMP device responding to an SNMPGetRequest, or from a supplied byte array
     *  containing the address components.
     *  
     *  @throws SnmpBadValueException Indicates an invalid array supplied: must have length 6.
     */
    public SnmpNSAPAddress(byte[] encoding)  throws SnmpBadValueException
    {
        tag = SnmpBERType.SnmpNsapAddress;
        
        if (encoding.length == 6)
            data = Arrays.copyOf(encoding, encoding.length);
        else        // wrong size
            throw new SnmpBadValueException(" NSAPAddress: bad BER encoding supplied to set value ");
    }
    
    
    /** 
     * Sets the value from a byte array containing the address.
     *  
     *  @throws SnmpBadValueException Indicates an incorrect object type supplied, or array of
     *  incorrect size.
     */
    @Override
    public void setValue(Object newAddress) throws SnmpBadValueException
    {
        if (newAddress instanceof byte[])
		{
        	byte[] addressBytes = (byte[])newAddress;
        	if (addressBytes.length == 6)
        		data = Arrays.copyOf(addressBytes, addressBytes.length);
		}
        else if (newAddress instanceof String)
        {
            data = SnmpNSAPAddress.parseNSAPAddress((String)newAddress);
        }
        else
        {
            throw new SnmpBadValueException(" NSAPAddress: bad length byte string supplied to set value ");
        }
    }
    

    /** 
     *  Returns pretty-printed (dash-separated) address.
     */
    @Override
    public String toString()
    {
        StringBuffer returnStringBuffer = new StringBuffer();
        
        if (data.length > 0)
        {
            int convert = data[0];
            if (convert < 0)
            	convert += 256;
            
            returnStringBuffer.append(Integer.toHexString(convert));
                    
            for (int i = 1; i < data.length; i++)
            {
                convert = data[i];
                if (convert < 0)
                    convert += 256;
                
                returnStringBuffer.append("-");
                returnStringBuffer.append(Integer.toHexString(convert));
            }
        }
        
        return returnStringBuffer.toString();
    }
    
    /**
     * Attempts to parse a String into a NSAP Address byte array.
     * @param addressString
     * @return
     * @throws SnmpBadValueException
     */
    public static byte[] parseNSAPAddress(String addressString)
        throws SnmpBadValueException
    {
        try
        {   
            // Matt Hamilton on 1/31/06:
            // Rewritten to use String's split method instead of StringTokenizer.
            
            String[] addressArray = addressString.split("[\\.-]");
            
            if (addressArray.length != 6)
                throw new SnmpBadValueException(" NSAPAddress: wrong number of components supplied to set value ");
            
            byte[] returnBytes = new byte[addressArray.length];
            
            for (int i = 0; i < addressArray.length; i++)
            {
                int addressComponent = Integer.parseInt(addressArray[i], 16);
                if ( (addressComponent < 0) || (addressComponent > 255) )
                    throw new SnmpBadValueException(" NSAPAddress: component out of range: " + addressComponent);
                returnBytes[i] = (byte)addressComponent;
            }
            
            return returnBytes;
            
        }
        catch (NumberFormatException e)
        {
            throw new SnmpBadValueException(" NSAPAddress: invalid component supplied to set value ");
        }
    }
    
    /*public static void main(String[] args)
    {
        try
        {
            SNMPNSAPAddress test = new SNMPNSAPAddress("6-77-0-111-145.7");
        }
        catch (Exception e)
        {
        } 
    }*/

}
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

import java.net.Inet4Address;
import java.util.Arrays;

import snmp.error.SnmpBadValueException;


/** 
 *  Class to hold IP addresses; special case of SNMP Octet String.
 */
public class SnmpIpAddress extends SnmpOctetString
{
    // length limited to 4 octets
    
    /** 
     *  Initializes address to 0.0.0.0
     */  
    public SnmpIpAddress()
    {
        // initialize to 0.0.0.0
        tag = SnmpBERType.SnmpIpAddress;
        data = new byte[4];
        Arrays.fill(data, (byte)0);
    }
    
    /** 
     *  Initializes from a string containing a standard "dotted" IP address.
     *   
     *  @throws SnmpBadValueException Indicates an invalid string supplied: more than 4 components,
     *  component values not between 0 and 255, etc.
     */
    public SnmpIpAddress(String addressString)
        throws SnmpBadValueException
    {
        tag = SnmpBERType.SnmpIpAddress;
        this.data = SnmpIpAddress.parseIpAddress(addressString);
    }
    
    /**
     * Creates an SNMP IP address from a IPv4 Internet address.
     * @param address
     * @throws SnmpBadValueException
     */
    public SnmpIpAddress(Inet4Address address)
	{
	    tag = SnmpBERType.SnmpIpAddress;
	    
	    byte[] rawAddress = address.getAddress();
	    this.data = Arrays.copyOf(rawAddress, rawAddress.length);   	
	}
    
    
    /** 
     *  Initializes from the BER encoding, as received in a response from 
     *  an SNMP device responding to an SNMPGetRequest, or from a supplied byte array
     *  containing the address components.
     *  
     *  @throws SnmpBadValueException Indicates an invalid array supplied: must have length 4.
     */
    public SnmpIpAddress(byte[] encoding)
        throws SnmpBadValueException
    {
        tag = SnmpBERType.SnmpIpAddress;
        
        if (encoding.length == 4)
            data = Arrays.copyOf(encoding, encoding.length);
        else        // wrong size
            throw new SnmpBadValueException(" IPAddress: bad BER encoding supplied to set value ");
    }
    
    
    /** 
     *  Sets the value from a byte array containing the address.
     *  
     *  @throws SnmpBadValueException Indicates an incorrect object type supplied, or array of
     *  incorrect size.
     */
    @Override
    public void setValue(Object newAddress)
        throws SnmpBadValueException
    {
        if (newAddress instanceof byte[])
    	{
        	byte[] addressBytes = (byte[])newAddress;
        	if (addressBytes.length == 4)
        		data = Arrays.copyOf(addressBytes, addressBytes.length);
    	}
        else if (newAddress instanceof String)
        {
            data = SnmpIpAddress.parseIpAddress((String)newAddress);
        }
        else if (newAddress instanceof Inet4Address)
        {
    	    byte[] rawAddress = ((Inet4Address)newAddress).getAddress();
    	    this.data = Arrays.copyOf(rawAddress, rawAddress.length);  
        }
        else
        {
            throw new SnmpBadValueException(" IPAddress: bad data supplied to set value ");
        }
    }
    
    
    /**
     * Attempts to parse a String into an IP Address byte array.
     * @param addressString
     * @return
     * @throws SnmpBadValueException
     */
    public static byte[] parseIpAddress(String addressString)
    	throws SnmpBadValueException
    {
        try
        {
            // Matt Hamilton on 1/31/06:
            // Rewritten to use String's split method instead of StringTokenizer.
            
            String[] addressArray = addressString.split("\\.");
            
            if (addressArray.length != 4)
                throw new SnmpBadValueException(" IPAddress: wrong number of components supplied to set value ");
            
            byte[] returnBytes = new byte[addressArray.length];

            for (int i = 0; i < addressArray.length; i++)
            {
                int addressComponent = Integer.parseInt(addressArray[i]);
                if ( (addressComponent < 0) || (addressComponent > 255) )
                    throw new SnmpBadValueException(" IPAddress: component out of range: " + addressComponent);
                returnBytes[i] = (byte)addressComponent;
            }
            
            return returnBytes;  
        }
        catch (NumberFormatException e)
        {
            throw new SnmpBadValueException(" IPAddress: invalid component supplied to set value ");
        }
    }


	/** 
	 *  Returns pretty-printed IP address.
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
            
	        returnStringBuffer.append(convert);
	                
	        for (int i = 1; i < data.length; i++)
	        {
	            convert = data[i];
	            if (convert < 0)
	                convert += 256;
	            returnStringBuffer.append(".");
	            returnStringBuffer.append(convert);
	        }
	    }
	    
	    return returnStringBuffer.toString();
	}
    
//    public static void main(String[] args)
//    {
//        try
//        {
//            SnmpIpAddress test = new SnmpIpAddress("138.230.106.63");
//            System.out.println(test.toString());
//            
//            InetAddress address = InetAddress.getByName("138.230.106.63");
//            System.out.println(Arrays.toString(address.getAddress()));
//        }
//        catch (Exception e)
//        {
//        } 
//    }

}
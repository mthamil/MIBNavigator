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


/**
 *  Defines class for holding physical 6-byte addresses.
 */
public class SNMPNSAPAddress extends SNMPOctetString
{
    // length limited to 6 octets
    
    /**
     *  Initialize address to 0.0.0.0.0.0.
     */
    public SNMPNSAPAddress()
    {
        tag = SNMPBERType.SNMP_NSAPADDRESS;
        
        // initialize to 0.0.0.0.0.0
        data = new byte[6];
        for (int i = 0; i < 6; i++)
            data[i] = 0;
    }
    
    
    public SNMPNSAPAddress(String string)
        throws SNMPBadValueException
    {
        tag = SNMPBERType.SNMP_NSAPADDRESS;
        
        data = parseNSAPAddress(string);
    }
    
    
    /** 
     *  Used to initialize from the BER encoding, as received in a response from 
     *  an SNMP device responding to an SNMPGetRequest, or from a supplied byte array
     *  containing the address components.
     *  
     *  @throws SNMPBadValueException Indicates an invalid array supplied: must have length 6.
     */
    public SNMPNSAPAddress(byte[] enc)
        throws SNMPBadValueException
    {
        tag = SNMPBERType.SNMP_NSAPADDRESS;
        
        if (enc.length == 6)
            data = enc;
        else        // wrong size
            throw new SNMPBadValueException(" NSAPAddress: bad BER encoding supplied to set value ");
    }
    
    
    /** 
     *  Used to set the value from a byte array containing the address.
     *  
     *  @throws SNMPBadValueException Indicates an incorrect object type supplied, or array of
     *  incorrect size.
     */
    public void setValue(Object newAddress)
        throws SNMPBadValueException
    {
        if ((newAddress instanceof byte[]) && (((byte[])newAddress).length == 6))
            data = (byte[])newAddress;
        else if (newAddress instanceof String)
            data = parseNSAPAddress((String)newAddress);
        else
            throw new SNMPBadValueException(" NSAPAddress: bad length byte string supplied to set value ");
    }
    

    /** 
     *  Return pretty-printed (dash-separated) address.
     */
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
    
    
    private byte[] parseNSAPAddress(String addressString)
        throws SNMPBadValueException
    {
        try
        {   
            // Matt Hamilton on 1/31/06:
            // Rewritten to use String's split method instead of StringTokenizer.
            
            String[] addressArray = addressString.split("[\\.-]");
            
            if (addressArray.length != 6)
                throw new SNMPBadValueException(" NSAPAddress: wrong number of components supplied to set value ");
            
            byte[] returnBytes = new byte[addressArray.length];
            
            for (int i = 0; i < addressArray.length; i++)
            {
                int addressComponent = Integer.parseInt(addressArray[i], 16);
                if ( (addressComponent < 0) || (addressComponent > 255) )
                    throw new SNMPBadValueException(" NSAPAddress: component out of range: " + addressComponent);
                returnBytes[i] = (byte)addressComponent;
            }
            
            return returnBytes;
            
        }
        catch (NumberFormatException e)
        {
            throw new SNMPBadValueException(" NSAPAddress: invalid component supplied to set value ");
        }
        
        /*try
        {
            StringTokenizer st = new StringTokenizer(addressString, " .-"); // break on spaces, dots or dashes
            int size = 0;
            
            while (st.hasMoreTokens())
            {
                // figure out how many values are in string
                size++;
                st.nextToken();
            }
            
            if (size != 6)
                throw new SNMPBadValueException(" NSAPAddress: wrong number of components supplied to set value ");
            
            byte[] returnBytes = new byte[size];
            
            st = new StringTokenizer(addressString, " .-");
            
            for (int i = 0; i < size; i++)
            {
                int addressComponent = (Integer.parseInt(st.nextToken(), 16));
                if ((addressComponent < 0) || (addressComponent > 255))
                    throw new SNMPBadValueException(" NSAPAddress: invalid component supplied to set value ");
                returnBytes[i] = (byte)addressComponent;
            }
            
            return returnBytes;
            
        }
        catch (NumberFormatException e)
        {
            throw new SNMPBadValueException(" NSAPAddress: invalid component supplied to set value ");
        }*/
        
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
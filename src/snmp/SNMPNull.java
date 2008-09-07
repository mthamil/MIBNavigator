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
 *  Object representing the SNMP Null data type.
 */
public class SNMPNull extends SNMPObject
{
    
    protected SNMPBERType tag = SNMPBERType.SNMP_NULL;
    
    /**
     *  Returns Java null reference.
     */
    public Object getValue()
    {
        return null;
    }
    
    
    /**
     *  Always throws SNMPBadValueException (which null value did you want, anyway?)
     */
    public void setValue(Object o)
        throws SNMPBadValueException
    {
        throw new SNMPBadValueException(" Null: attempt to set value ");
    }
    

    /**
     *  Returns BER encoding for a null object: two bytes, tag and length of 0.
     */
    protected byte[] getBEREncoding()
    {
        byte[] encoding = new byte[2];
        
        // set tag byte
        encoding[0] = SNMPBERType.SNMP_NULL.getByte();
            
        // len = 0 since no payload!
        encoding[1] = 0;
        
        // no V!
        
        return encoding;
    }
    
    
    /**
     *  Checks just that both are instances of SNMPNull (no embedded value to check).
     */
    public boolean equals(Object other)
    {
        // false if other is null
        if (other == null)
            return false;
        
        // check that they're both of the same class
        if (this.getClass().equals(other.getClass()))
            return true;
       
        return false;
    }
    

    /**
     *  Returns String "Null".
     */
    public String toString()
    {
        return new String("Null");
    }
    
}
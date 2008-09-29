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
 *  SNMP datatype used to represent time value. It is just an extension of SNMPInteger.
 */
public class SnmpTimeTicks extends SnmpInteger
{

	/**
	 * Initializes with a zero value.
	 */
    public SnmpTimeTicks()
    {
        this(0);    // initialize value to 0
    }


    /**
     *  Initializes with a long value that is truncated to 
     *  32 bits for SNMP v2 compatibility.
     */
    public SnmpTimeTicks(long value)
    {
        // we truncate the long value to 32 bits for SNMP v2 compatibility
        super(value & 0x00000000FFFFFFFFL);

        tag = SnmpBERType.SnmpTimeTicks;
    }


    protected SnmpTimeTicks(byte[] enc)
        throws SnmpBadValueException
    {
        super(enc);

        tag = SnmpBERType.SnmpTimeTicks;
    }


	/**
     *  Formats the time value into days:hours:minutes:seconds.hundredthsOfASecond
     *  format for readability and display purposes.
     *  Added by Matt Hamilton on 7/12/2005.
     *  
     *  @return a formatted String representing an amount of time
     */
    public String toString()
    {
        StringBuilder returnTime = new StringBuilder();

        // This conversion will not cause a loss of precision because 
        // long values are truncated during SNMPTimeTicks creation. Long
        // is used rather than int as a precaution, however.
        long hundredths = value.longValue();

        // Get days
        long lngTime = hundredths / 8640000;        // (24 * 60 * 60 * 100) number of 100ths of seconds in a day
        long lngRemaining = hundredths % 8640000;
        returnTime.append(lngTime);
        returnTime.append(":");
        
        // Get hours
        lngTime = lngRemaining / 360000;            // (60 * 60 * 100) number of 100ths of seconds in an hour
        lngRemaining = lngRemaining % 360000;
        returnTime.append(lngTime);
        returnTime.append(":");
        
        // Get minutes
        lngTime = lngRemaining / 6000;              // (60 * 100) number of 100ths of seconds in a minute
        lngRemaining = lngRemaining % 6000;
        returnTime.append(lngTime);
        returnTime.append(":");
        
        // Get seconds
        lngTime = lngRemaining / 100;
        lngRemaining = lngRemaining % 100;
        returnTime.append(lngTime);
        returnTime.append(".");
        
        // Get hundredths of a second
        returnTime.append(lngRemaining);

        return returnTime.toString();
    }

}
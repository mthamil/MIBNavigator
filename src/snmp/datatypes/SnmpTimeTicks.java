/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 * Copyright (C) 2009, Matt Hamilton <matthamilton@live.com>
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

import snmp.error.SnmpBadValueException;


/**
 *  SNMP datatype used to represent time value. It is just an extension of SNMPInteger.
 */
public class SnmpTimeTicks extends SnmpInteger
{
	/** (24 * 60 * 60 * 100) number of 100ths of seconds in a day */
	private static final long HUNDREDTHS_PER_DAY = 8640000;
	
	/** (60 * 60 * 100) number of 100ths of seconds in an hour */
	private static final long HUNDREDTHS_PER_HOUR = 360000;
	
	/** (60 * 100) number of 100ths of seconds in a minute */
	private static final long HUNDREDTHS_PER_MINUTE = 6000;
	
	/** Obviously, there are 100 hundredths of a second in a second */
	private static final long HUNDREDTHS_PER_SECOND = 100;

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


    public SnmpTimeTicks(byte[] encodedValue)
        throws SnmpBadValueException
    {
        super(encodedValue);

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
        long time = hundredths / SnmpTimeTicks.HUNDREDTHS_PER_DAY;
        long remaining = hundredths % SnmpTimeTicks.HUNDREDTHS_PER_DAY;
        returnTime.append(time);
        returnTime.append(":");
        
        // Get hours
        time = remaining / SnmpTimeTicks.HUNDREDTHS_PER_HOUR;
        remaining = remaining % SnmpTimeTicks.HUNDREDTHS_PER_HOUR;
        returnTime.append(time);
        returnTime.append(":");
        
        // Get minutes
        time = remaining / SnmpTimeTicks.HUNDREDTHS_PER_MINUTE;
        remaining = remaining % SnmpTimeTicks.HUNDREDTHS_PER_MINUTE;
        returnTime.append(time);
        returnTime.append(":");
        
        // Get seconds
        time = remaining / SnmpTimeTicks.HUNDREDTHS_PER_SECOND;
        remaining = remaining % SnmpTimeTicks.HUNDREDTHS_PER_SECOND;
        returnTime.append(time);
        returnTime.append(".");
        
        // Get hundredths of a second
        returnTime.append(remaining);

        return returnTime.toString();
    }

}
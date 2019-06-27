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

package snmp;

import org.junit.Test;

import snmp.datatypes.SnmpInteger;
import snmp.datatypes.SnmpIpAddress;
import snmp.datatypes.SnmpNull;
import snmp.datatypes.sequence.SnmpSequence;
import snmp.error.SnmpBadValueException;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

public class SnmpSequenceTests
{
	@Test
	public void testToString()
	{
		SnmpSequence sequence = new SnmpSequence();	
		try
		{
			sequence.addSNMPObject(new SnmpInteger(100L));
			sequence.addSNMPObject(new SnmpIpAddress("127.0.0.1"));
			sequence.addSNMPObject(new SnmpNull());
		}
		catch (SnmpBadValueException e) { }
		
		String s = sequence.toString();
		assertThat(s, is("( 100  127.0.0.1  Null )"));
	}
}

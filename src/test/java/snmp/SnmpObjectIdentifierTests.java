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

package tests.snmp;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import snmp.datatypes.SnmpObjectIdentifier;
import snmp.error.SnmpBadValueException;

public class SnmpObjectIdentifierTests
{
	@Test
	public void testToString()
	{
		SnmpObjectIdentifier oid = null;
		try
		{
			oid = new SnmpObjectIdentifier(new int[] { 138, 14, 90, 4, 62, 1 });
		}
		catch (SnmpBadValueException e) { }
		
		String s = oid.toString();
		assertThat(s, is("138.14.90.4.62.1"));
	}
}

/*
 * SNMP Package
 *
 * Copyright (C) 2010, Matt Hamilton <matthamilton@live.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package snmp;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import snmp.datatypes.SnmpTimeTicks;

public class SnmpTimeTicksTests
{
	@Test
	public void testToString()
	{
		// Test a known value.
		SnmpTimeTicks time = new SnmpTimeTicks(106444246);
		String s = time.toString();
		assertThat(s, is("12:7:40:42.46"));
		
		// Now test each individualcomponent of the time.
		time = new SnmpTimeTicks(99);
		s = time.toString();
		assertThat(s, is("0:0:0:0.99"));
		
		time = new SnmpTimeTicks(100);
		s = time.toString();
		assertThat(s, is("0:0:0:1.0"));
		
		time = new SnmpTimeTicks(6000);
		s = time.toString();
		assertThat(s, is("0:0:1:0.0"));
		
		time = new SnmpTimeTicks(360000);
		s = time.toString();
		assertThat(s, is("0:1:0:0.0"));
		
		time = new SnmpTimeTicks(8640000);
		s = time.toString();
		assertThat(s, is("1:0:0:0.0"));
	}
}

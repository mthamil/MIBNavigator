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

import java.util.Arrays;

import org.junit.Test;

import snmp.datatypes.SnmpOctetString;

public class SnmpOctetStringTests
{
	@Test
	public void testIsPrintable()
	{
		byte[] b = new byte[] { 'h', 'e', 'l', 'l', 'o'};
		SnmpOctetString string = new SnmpOctetString(b);
		
		assertThat(string.isPrintable(), is(true));
		
		// Make the byte array a null-terminated string.
		b = Arrays.copyOf(b, b.length + 1);
		b[b.length - 1] = 0;
		string = new SnmpOctetString(b);
		
		assertThat(string.isPrintable(), is(true));
		
		// Now use an unprintable character earlier.
		b[0] = 0;
		string = new SnmpOctetString(b);
		
		assertThat(string.isPrintable(), is(false));
		
		b[0] = (byte)0x7f;
		string = new SnmpOctetString(b);
		
		assertThat(string.isPrintable(), is(false));
	}
	
	@Test
	public void testToString()
	{
		byte[] b = new byte[] { 'h', 'e', 'l', 'l', 'o'};
		SnmpOctetString string = new SnmpOctetString(b);
		
		assertThat(string.toString(), is("hello"));
		
		// Make the byte array a null-terminated string.
		b = Arrays.copyOf(b, b.length + 1);
		b[b.length - 1] = 0;
		string = new SnmpOctetString(b);
		
		assertThat(string.toString(), is("hello"));
		
		// Now use an unprintable character earlier.
		b[0] = 0;
		string = new SnmpOctetString(b);
		
		assertThat(string.toString(), is("00 65 6C 6C 6F 00"));
		
		b[0] = (byte)0x7f;
		string = new SnmpOctetString(b);
		
		assertThat(string.toString(), is("7F 65 6C 6C 6F 00"));
	}
}

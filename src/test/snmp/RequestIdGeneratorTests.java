/**
 * MIB Navigator
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

package tests.snmp;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Test;

import snmp.RequestIdGenerator;


public class RequestIdGeneratorTests
{
	@Test
	public void testGenerator() 
	{
		RequestIdGenerator generator = new RequestIdGenerator();
		Integer next = generator.next();
		
		assertThat(generator.current().intValue(), is(next.intValue()));
		assertThat(next.intValue(), is(1));
		
		next = generator.next();
		
		assertThat(generator.current().intValue(), is(next.intValue()));
		assertThat(next.intValue(), is(2));
	}
}

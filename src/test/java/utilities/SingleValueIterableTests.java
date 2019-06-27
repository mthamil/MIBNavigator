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

package utilities;

import org.junit.Test;

import utilities.iteration.Iterables;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

public class SingleValueIterableTests
{
	@Test
	public void testSingleValueIterable()
	{
		Iterable<String> iter = Iterables.toIterable("test");
		
		for (int i = 0; i < 2; i++)
		{
			int count = 0;
			for (String s : iter)
			{
				assertThat(s, is("test"));
				count++;
			}
			
			assertThat(count, is(1));
		}
	}
}

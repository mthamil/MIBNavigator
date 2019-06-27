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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static utilities.iteration.Iterables.filter;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import utilities.Predicate;

/**
 * @author matt
 *
 */
public class FilteredIterationTests
{
	@Test
	public void testFilteredIterable()
	{
		Predicate<String> pred = new Predicate<String>()
		{
			public boolean matches(String value) { return value.startsWith("a"); }
		};
		
		List<String> strings = Arrays.asList("abc", "acb", "921", "ade", "bcd", "zyx");
		
		Iterable<String> filtered = filter(strings, pred);
		
		for (int i = 0; i < 2; i++)
		{
			int count = 0;
			for (String s : filtered)
			{
				assertTrue(s.startsWith("a"));
				count++;
			}
			
			assertThat(count, is(3));
		}
	}
}

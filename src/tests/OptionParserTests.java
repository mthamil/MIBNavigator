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

package tests;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import utilities.Option;
import utilities.parsing.OptionParser;
import utilities.parsing.Parser;

/**
 * @author matt
 *
 */
public class OptionParserTests
{
	@Test
	public void testIntegerParser()
	{
		Parser<Option<Integer>> parser = new OptionParser<Integer>()
		{
			@Override
			public Option<Integer> parse(String stringValue)
			{
				try
				{
					int value = Integer.valueOf(stringValue);
					return Option.some(value);
				}
				catch (NumberFormatException e)
				{
					return Option.none(Integer.class);
				}
			}
		};
		
		Option<Integer> value = parser.parse("test");
		assertThat(value.hasValue(), is (false));
		
		value = parser.parse("1");
		assertThat(value.hasValue(), is(true));	
	}
}

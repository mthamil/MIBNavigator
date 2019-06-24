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
import static org.junit.Assert.*;

import org.junit.Test;

import utilities.Option;
import static tests.Assertions.assertThrows;

public class OptionTests
{
	@Test
	public void testOptionProperties()
	{
		final Option<String> stringNone = Option.none(String.class);
		assertThat(stringNone.hasValue(), is(false));
		assertThrows(new Runnable() { public void run() { stringNone.getValue(); } }, UnsupportedOperationException.class);
		
		Option<String> stringSome = Option.some("test");
		assertThat(stringSome.hasValue(), is(true));
		assertThat(stringSome.getValue(), is("test"));
	}
	
	@Test
	public void testOptionSomeEquality()
	{
		// Test self-equality.
		Option<String> stringSome = Option.some("test");
		assertThat(stringSome, is(stringSome));
		
		// Test that Some uses its value for equality.
		Option<String> stringSome2 = Option.some("test");
		assertThat(stringSome, is(stringSome2));
		
		stringSome2 = Option.some("test2");
		assertFalse(stringSome.equals(stringSome2));
		
		// Test that Somes of different generic types cannot be equal.
		Option<Integer> intSome = Option.some(1);
		assertFalse(stringSome.equals(intSome));
	}
	
	@Test
	public void testOptionNoneEquality()
	{
		// Test self-equality.
		Option<String> stringNone = Option.none(String.class);
		assertThat(stringNone, is(stringNone));
		
		Option<String> stringNone2 = Option.none(String.class);
		assertThat(stringNone, is(stringNone2));
		
		Option<Integer> intNone = Option.none(Integer.class);
		assertFalse(stringNone.equals(intNone));
	}
}

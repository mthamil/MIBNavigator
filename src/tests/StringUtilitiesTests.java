/**
 * MIB Navigator
 *
 * Copyright (C) 2009, Matt Hamilton <matthamilton@live.com>
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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import utilities.StringUtilities;

public class StringUtilitiesTests
{
	@Test
	public void testEmptyJoin() 
	{
		List<String> list = new ArrayList<String>();
		String joinedList = StringUtilities.join(list, ",");
		assertEquals("", joinedList);
	}
	
	@Test
	public void testSingleElementJoin()
	{
		List<String> list = Arrays.asList("test1");
		String joinedList = StringUtilities.join(list, ",");
		assertEquals("test1", joinedList);
	}

	@Test
	public void testMultiElementJoin()
	{
		List<String> list = Arrays.asList("test1", "test2");
		String joinedList = StringUtilities.join(list, ",");
		assertEquals("test1,test2", joinedList);
	}
		
	@Test
	public void testArrayJoin()
	{
		String[] array = new String[] { "test1", "test2", "test3" };
		String joinedList = StringUtilities.join(array, "+");
		assertEquals("test1+test2+test3", joinedList);
	}
	
	@Test
	public void testSublistJoin()
	{
		List<String> list = Arrays.asList("1", "2", "3", "4", "5");
		int maxItems = Math.min(list.size(), 3);
		String joinedSublist = StringUtilities.join(list.subList(0, maxItems), ",");
		assertEquals("1,2,3", joinedSublist);
		
		maxItems = Math.min(list.size(), 8);
		joinedSublist = StringUtilities.join(list.subList(0, maxItems), ",");
		assertEquals("1,2,3,4,5", joinedSublist);
		
		maxItems = Math.min(list.size(), 0);
		joinedSublist = StringUtilities.join(list.subList(0, maxItems), ",");
		assertEquals("", joinedSublist);
		
		list = new ArrayList<String>();
		maxItems = Math.min(list.size(), 3);
		joinedSublist = StringUtilities.join(list.subList(0, maxItems), ",");
		assertEquals("", joinedSublist);
	}
}

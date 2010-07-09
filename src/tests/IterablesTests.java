/**
 * Utilities
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
import static org.hamcrest.core.IsNull.*;
import static org.hamcrest.core.IsCollectionContaining.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import utilities.Mapper;
import utilities.Predicate;
import utilities.iteration.Grouping;
import utilities.iteration.Iterables;

import static utilities.iteration.Iterables.*;
import static tests.Assertions.*;

/**
 * @author matt
 *
 */
public class IterablesTests
{
	private Iterable<Integer> ints;
	private Iterable<Integer> evens;
	
	@Before
	public void setup()
	{
		ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		evens = filter(ints, new Predicate<Integer>() { public boolean matches(Integer value) { return value.intValue() % 2 == 0; } });
	}
	
	@Test
	public void testEmptyIterable()
	{
		Iterable<String> empty = Iterables.<String>empty();
		assertThat(empty.iterator().hasNext(), is(false));
	}
	
	@Test
	public void testFirst()
	{
		Integer first = first(evens);
		assertThat(first, is(2));
		
		
		final Iterable<Integer> nonExistent = filter(evens, 
				new Predicate<Integer>() { public boolean matches(Integer value) { return value.intValue() == 11; } });
		
		assertThrows(new Runnable() { public void run() { first(nonExistent); } }, NoSuchElementException.class);
	}
	
	@Test
	public void testFirstOrNull()
	{
		Integer first = firstOrNull(evens);
		assertThat(first, is(2));
		
		
		Iterable<Integer> nonExistent = filter(evens, 
				new Predicate<Integer>() { public boolean matches(Integer value) { return value.intValue() == 11; } });
		
		assertThat(firstOrNull(nonExistent), nullValue());
	}
	
	@Test
	public void testLast()
	{
		Integer last = last(evens);
		assertThat(last, is(10));
		
		
		final Iterable<Integer> nonExistent = filter(evens, 
				new Predicate<Integer>() { public boolean matches(Integer value) { return value.intValue() == 11; } });
		
		assertThrows(new Runnable() { public void run() { last(nonExistent); } }, NoSuchElementException.class);
	}
	
	@Test
	public void testLastOrNull()
	{
		Integer last = lastOrNull(evens);
		assertThat(last, is(10));
		
		
		Iterable<Integer> nonExistent = filter(evens, 
				new Predicate<Integer>() { public boolean matches(Integer value) { return value.intValue() == 11; } });
		
		assertThat(lastOrNull(nonExistent), nullValue());
	}
	
	@Test
	public void testLastWithList()
	{
		Integer last = last(ints);
		assertThat(last, is(10));
		
		
		final Iterable<Integer> empty = new ArrayList<Integer>();
		assertThrows(new Runnable() { public void run() { last(empty); } }, NoSuchElementException.class);
	}
	
	@Test
	public void testLastOrNullWithList()
	{
		Integer last = lastOrNull(ints);
		assertThat(last, is(10));
		
		
		Iterable<Integer> empty = new ArrayList<Integer>();
		assertThat(lastOrNull(empty), nullValue());
	}
	
	@Test
	public void testContains()
	{
		boolean exists = contains(evens, 4);
		assertThat(exists, is(true));
		
		exists = contains(evens, 3);
		assertThat(exists, is(false));
	}
	
	@Test
	public void testContainsWithComparator()
	{
		Iterable<String> strings = Arrays.asList("test1", "test2", "test3", "test4");
		
		boolean exists = contains(strings, "TEST3", new Comparator<String>() {
			public int compare(String s1, String s2) { return s1.compareToIgnoreCase(s2); } });
		
		assertThat(exists, is(true));
	}
	
	@Test
	public void testIsEmpty()
	{
		boolean empty = isEmpty(evens);
		assertThat(empty, is(false));
		
		Iterable<Integer> nonExistent = filter(evens, 
				new Predicate<Integer>() { public boolean matches(Integer value) { return value.intValue() == 11; } });
		
		empty = isEmpty(nonExistent);
		assertThat(empty, is(true));
	}
	
	@Test
	public void testIsEmptyWithCollection()
	{
		boolean empty = isEmpty(ints);
		assertThat(empty, is(false));

		empty = isEmpty(new ArrayList<Integer>());
		assertThat(empty, is(true));
	}
	
	@Test
	public void testSize()
	{
		int size = size(evens);
		assertThat(size, is(5));
	}
	
	@Test
	public void testSizeWithCollection()
	{
		int size = size(ints);
		assertThat(size, is(10));
	}
	
	@Test
	public void testAll()
	{
		boolean all = all(evens, new Predicate<Integer>() {
			public boolean matches(Integer value) { return value.intValue() < 11; } } );
		
		assertThat(all, is(true));
		
		all = all(evens, new Predicate<Integer>() {
			public boolean matches(Integer value) { return value.intValue() % 4 == 0; } } );
		
		assertThat(all, is(false));
	}
	
	@Test
	public void testCount()
	{
		int count = count(evens, new Predicate<Integer>() {
			public boolean matches(Integer value) { return value.intValue() < 11; } } );
		
		assertThat(count, is(size(evens)));
		
		count = count(evens, new Predicate<Integer>() {
			public boolean matches(Integer value) { return value.intValue() % 4 == 0; } } );
		
		assertThat(count, is(2));
	}
	
	@Test
	public void testGroupBy()
	{
		Iterable<String> strings = Arrays.asList("ab", "abcd", "bc", "efg", "hijk", "abc", "cd", "a", "de");
		Iterable<Grouping<Integer, String>> stringLengths = groupBy(strings, new Mapper<String, Integer>() { 
			public Integer map(String item) { return item.length(); } });
		
		int groupCount = 0;
		for (Grouping<Integer, String> group : stringLengths)
		{
			switch (group.getKey().intValue())
			{
				case 1:
					assertThat(size(group), is(1));
					assertThat(group, hasItem("a"));
					break;
					
				case 2:
					assertThat(size(group), is(4));
					assertThat(group, hasItems("ab", "bc", "cd", "de"));
					break;
					
				case 3:
					assertThat(size(group), is(2));
					assertThat(group, hasItems("abc", "efg"));
					break;
					
				case 4:
					assertThat(size(group), is(2));
					assertThat(group, hasItems("abcd", "hijk"));
					break;
				
			}
			
			groupCount++;
		}
		
		assertThat(groupCount, is(4));
	}
	
	@Test
	public void testGroupByWithNewGroupLast()
	{
		Iterable<String> strings = Arrays.asList("ab", "abcd", "bc", "efg", "hijk", "abc", "cd", "a");
		Iterable<Grouping<Integer, String>> stringLengths = groupBy(strings, new Mapper<String, Integer>() { 
			public Integer map(String item) { return item.length(); } });
		
		int groupCount = 0;
		for (Grouping<Integer, String> group : stringLengths)
		{
			switch (group.getKey().intValue())
			{
				case 1:
					assertThat(size(group), is(1));
					assertThat(group, hasItem("a"));
					break;
					
				case 2:
					assertThat(size(group), is(3));
					assertThat(group, hasItems("ab", "bc", "cd"));
					break;
					
				case 3:
					assertThat(size(group), is(2));
					assertThat(group, hasItems("abc", "efg"));
					break;
					
				case 4:
					assertThat(size(group), is(2));
					assertThat(group, hasItems("abcd", "hijk"));
					break;
				
			}
			
			groupCount++;
		}
		
		assertThat(groupCount, is(4));
	}
}

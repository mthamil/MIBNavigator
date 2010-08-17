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
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import utilities.Predicate;
import utilities.iteration.ConcatenatingIterable;
import utilities.iteration.DistinctIterable;
import utilities.iteration.Grouping;
import utilities.iteration.Iterables;
import utilities.iteration.RangeIterable;
import utilities.iteration.SlicesIterable;
import utilities.iteration.UnionIterable;
import utilities.iteration.ZipIterable;
import utilities.mappers.Mapper;
import utilities.mappers.NullMapper;
import utilities.mappers.ZipMapper;

import static utilities.iteration.Iterables.*;
import static tests.Assertions.*;

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
	public void testAny()
	{
		Iterable<Integer> items = Arrays.asList(1, 2, 3, 1, 2, 3, 4, 5, 2);
		
		boolean any = any(items, new Predicate<Integer>() { public boolean matches(Integer value) { return value == 1; } });
		assertThat(any, is(true));

		any = any(items, new Predicate<Integer>() { public boolean matches(Integer value) { return value == 6; } });
		assertThat(any, is(false));
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
	
	@Test
	public void testAsList()
	{
		List<Integer> evensList = toList(evens);
		
		assertThat(evensList, hasItems(2, 4, 6, 8, 10));
		assertThat(evensList.size(), is(size(evens)));
	}
	
	@Test
	public void testExactSlices()
	{
		Iterable<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		
		int sliceSize = 3;
		SlicesIterable<Integer> slices = new SlicesIterable<Integer>(items, sliceSize);
		
		int i = 0;
		for (Iterable<Integer> slice : slices)
		{
			int offset = 3 * i;
			assertThat(slice, hasItems(1 + offset, 2 + offset, 3 + offset));
			assertThat(size(slice), is(sliceSize));
			i++;
		}
		
		assertThat(i, is(3));
	}
	
	@Test
	public void testRemainderSlices()
	{
		Iterable<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		
		int sliceSize = 3;
		SlicesIterable<Integer> slices = new SlicesIterable<Integer>(items, sliceSize);
		
		int i = 0;
		for (Iterable<Integer> slice : slices)
		{
			int offset = 3 * i;
			if (i < 3)
			{
				assertThat(slice, hasItems(1 + offset, 2 + offset, 3 + offset));
				assertThat(size(slice), is(sliceSize));
			}
			else
			{
				assertThat(slice, hasItem(10));
				assertThat(size(slice), is(1));
			}
			
			i++;
		}
		
		assertThat(i, is(4));
	}
	
	@Test
	public void testEmptySlices()
	{
		Iterable<Integer> items = new ArrayList<Integer>();
		
		int sliceSize = 3;
		SlicesIterable<Integer> slices = new SlicesIterable<Integer>(items, sliceSize);
		
		assertThat(isEmpty(slices), is(true));
	}
	
	@Test
	public void testConcat()
	{
		Iterable<Integer> first = Arrays.asList(1, 2, 3);
		Iterable<Integer> second = Arrays.asList(4, 5, 6, 7);
		
		@SuppressWarnings("unchecked")
		Iterable<Integer> concatenated = new ConcatenatingIterable<Integer>(first, second);
		
		int i = 1;
		for (Integer item : concatenated)
		{
			assertThat(item, is(i));
			i++;
		}
		
		assertThat(i, is(8));
	}
	
	@Test
	public void testConcatWithEmptyIterable()
	{
		Iterable<Integer> first = Arrays.asList(1, 2, 3);
		Iterable<Integer> second = new ArrayList<Integer>();
		Iterable<Integer> third = Arrays.asList(4, 5, 6);
		
		@SuppressWarnings("unchecked")
		Iterable<Integer> concatenated = new ConcatenatingIterable<Integer>(first, second, third);
		
		int i = 1;
		for (Integer item : concatenated)
		{
			assertThat(item, is(i));
			i++;
		}
		
		assertThat(i, is(7));
	}
	
	@Test
	public void testZip()
	{
		Iterable<Integer> first = Arrays.asList(1, 2, 3, 4, 5);
		Iterable<String> second = Arrays.asList("1", "2", "3", "4", "5");
		
		Iterable<Integer> destination = new ZipIterable<Integer, String, Integer>(first, second, 
				new ZipMapper<Integer, String, Integer>()
				{
					public Integer map(Integer first, String second) { return first * Integer.valueOf(second); }
				});
		
		int i = 1;
		for (Integer d : destination)
		{
			assertThat(d, is(i * i));
			i++;
		}
		
		assertThat(i, is(6));
	}
	
	@Test
	public void testIncreasingRange()
	{
		int counter = 5;
		Iterable<Integer> range = new RangeIterable(5, 11);
		int lastNum = 0;
		for (Integer num : range)
		{
			assertThat(num, is(counter));
			counter++;
			lastNum = num.intValue();
		}
		
		assertThat(lastNum, is(11));
	}
	
	@Test
	public void testDecreasingRange()
	{
		int counter = 11;
		Iterable<Integer> range = new RangeIterable(11, 5);
		int lastNum = 0;
		for (Integer num : range)
		{
			assertThat(num, is(counter));
			counter--;
			lastNum = num.intValue();
		}
		
		assertThat(lastNum, is(5));
	}
	
	@Test
	public void testAsMap()
	{
		Iterable<String> strings = Arrays.asList("abc", "def", "efg", "hij");
		Map<Character, String> map = toMap(strings, 
				new Mapper<String, Character>() { public Character map(String item) { return item.charAt(0); } }, 
				new NullMapper<String>());
		
		for (Character c : map.keySet())
		{
			String value = map.get(c);
			assertThat(c, is(value.charAt(0)));
		}
		
		assertThat(map.keySet(), hasItems('a', 'd', 'e', 'h'));
		
		assertThat(map.size(), is(4));
	}
	
	@Test
	public void testUnion()
	{
		Iterable<Integer> first = Arrays.asList(1, 2, 3, 4, 5);
		Iterable<Integer> second = Arrays.asList(4, 6, 8, 9, 1);
		
		@SuppressWarnings("unchecked")
		Iterable<Integer> union = new UnionIterable<Integer>(first, second);
		
		int[] expected = new int[] {1, 2, 3, 4, 5, 6, 8, 9};
		int i = 0;
		for (Integer item : union)
		{
			assertThat(item.intValue(), is(expected[i]));
			i++;
		}

		assertThat(size(union), is(expected.length));
	}
	
	@Test
	public void testDistinct()
	{
		Iterable<Integer> source = Arrays.asList(5, 6, 1, 2, 2, 3, 4, 5, 6, 7);
		
		Iterable<Integer> distinct = new DistinctIterable<Integer>(source);
		
		int[] expected = new int[] {5, 6, 1, 2, 3, 4, 7};
		int i = 0;
		for (Integer item : distinct)
		{
			assertThat(item.intValue(), is(expected[i]));
			i++;
		}

		assertThat(size(distinct), is(expected.length));
	}
}

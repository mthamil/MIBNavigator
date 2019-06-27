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

package utilities;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import utilities.iteration.MultiMappingIterable;
import utilities.iteration.Iterables;
import utilities.iteration.MappingIterable;
import utilities.mappers.Mapper;
import utilities.mappers.NullMapper;


public class MapperTests
{
	private Mapper<String, Integer> intMapper;
	private Mapper<String, Iterable<String>> splitMapper;
	
	@Before
	public void setup()
	{
		intMapper = new Mapper<String, Integer>()
		{
			public Integer map(String item) { return Integer.valueOf(item); }
		};
		
		splitMapper = new Mapper<String, Iterable<String>>()
		{
			public Iterable<String> map(String item) { return Arrays.asList(item.split(",")); }
		};
	}
	
	
	@Test
	public void testMapping()
	{
		Iterable<String> strings = Arrays.asList("1", "2", "3", "4");
		Iterable<Integer> mapped = new MappingIterable<String, Integer>(strings, intMapper);
		
		int i = 1;
		for (Integer integer : mapped)
		{
			assertThat(integer, is(i));
			i++;
		}
		
		assertThat(i, is(5));
	}
	
	@Test
	public void testMultiMapping()
	{
		Iterable<String> strings = Arrays.asList("1,2,3,4", "5,6", "7,8,9,10,11");
		
		Iterable<String> mapped = new MultiMappingIterable<String, String, String>(strings, splitMapper, new NullMapper<String>());
		
		String[] expected = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
		
		int i = 0;
		for (String s : mapped)
		{
			assertThat(s, is(expected[i]));
			i++;
		}
		
		assertThat(i, is(11));
	}
	
	@Test
	public void testMultiMappingWithLazyMapper()
	{
		Mapper<String, Iterable<Integer>> splitConversionMapper = new Mapper<String, Iterable<Integer>>()
		{
			public Iterable<Integer> map(String item) 
			{ 
				return Iterables.map(Arrays.asList(item.split(",")), intMapper);
			}
		};
		
		Iterable<String> strings = Arrays.asList("1,2,3,4", "5,6", "7,8,9,10,11");
		
		Iterable<Integer> mapped = new MultiMappingIterable<String, Integer, Integer>(strings, splitConversionMapper, new NullMapper<Integer>());
		
		int i = 1;
		for (Integer integer : mapped)
		{
			assertThat(integer, is(i));
			i++;
		}
		
		assertThat(i, is(12));
	}
	
	@Test
	public void testIntermediaryMultiMapping()
	{
		Iterable<String> strings = Arrays.asList("1,2,3,4", "5,6", "7,8,9,10,11");
		
		Iterable<Integer> mapped = new MultiMappingIterable<String, String, Integer>(strings, splitMapper, intMapper);
		
		int i = 1;
		for (Integer integer : mapped)
		{
			assertThat(integer, is(i));
			i++;
		}
		
		assertThat(i, is(12));
	}
}

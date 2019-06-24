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

import java.util.Arrays;

import org.junit.Test;

import utilities.iteration.SeededAccumulator;
import utilities.iteration.SeedlessAccumulator;
import utilities.mappers.Mapper;
import utilities.mappers.Mapper2;
import utilities.mappers.NullMapper;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static utilities.iteration.Iterables.max;
import static utilities.iteration.Iterables.min;
import static utilities.iteration.Iterables.range;

public class AccumulatorTests
{
	@Test
	public void testAccumulatorNoSeed()
	{
		Iterable<String> strings = Arrays.asList("Hello", "this", "is", "a", "test.");
		
		SeedlessAccumulator<String, String> acc = new SeedlessAccumulator<String, String>(strings, new Mapper2<String, String, String>() {
				public String map(String first, String second)
				{
					return second + (second.length() > 0 ? " " : "") + first;
				} }, new NullMapper<String>());
		
		String result = acc.accumulate();
		assertThat(result, is("Hello this is a test."));
	}
	
	@Test
	public void testAccumulatorWithSeed()
	{
		Iterable<String> strings = Arrays.asList("Hello", "this", "is", "a", "test.");
		
		SeededAccumulator<String, String, String> acc = new SeededAccumulator<String, String, String>(strings, new Mapper2<String, String, String>() {
				public String map(String first, String second)
				{
					return second + (second.length() > 0 ? " " : "") + first;
						
				} }, new NullMapper<String>(), "");
		
		String result = acc.accumulate();
		assertThat(result, is("Hello this is a test."));
	}
	
	@Test
	public void testMinWithSelector()
	{
		Iterable<String> strings = Arrays.asList("Hello", "this", "is", "a", "test.");
		
		int min = min(strings, new Mapper<String, Integer>() {
					public Integer map(String item) { return item.length(); }
				});
		
		assertThat(min, is(1));
	}
	
	@Test
	public void testMaxWithSelector()
	{
		Iterable<String> strings = Arrays.asList("Hello", "this", "is", "a", "test.");
		
		int max = max(strings, new Mapper<String, Integer>() {
					public Integer map(String item) { return item.length(); }
				});
		
		assertThat(max, is(5));
	}
	
	@Test
	public void testMin()
	{
		int min = min(range(10, 1));
		assertThat(min, is(1));
		
		min = min(Arrays.asList(5, 6, 3, 4, 10, 7, 8, 2, 9, 1));
		assertThat(min, is(1));
	}
	
	@Test
	public void testMax()
	{
		int max = max(range(1, 10));
		assertThat(max, is(10));
		
		max = max(Arrays.asList(5, 6, 3, 4, 10, 7, 8, 2, 9, 1));
		assertThat(max, is(10));
	}
}

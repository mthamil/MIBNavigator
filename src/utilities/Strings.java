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

import java.util.Arrays;

import utilities.iteration.SeededAccumulator;
import utilities.mappers.Mapper2;
import utilities.mappers.NullMapper;


/**
 *  Class containing useful String utility methods.
 */
public final class Strings
{

	private Strings() { }
	
	/**
	 * Removes all leading and trailing instances of a character from a String.
	 * 
	 * @param untrimmed the String to trim the characters from
	 * @param trimChar the character to trim
	 * @return the trimmed String
	 */
	public static String trim(String untrimmed, char trimChar)
	{
	    String trimmed = untrimmed;
	    String stringToTrim = String.valueOf(trimChar);
	    
	    //Trim leading characters.
	    while (trimmed.startsWith(stringToTrim))
	        trimmed = trimmed.substring(trimmed.indexOf(stringToTrim) + 1);
	
	    //Trim trailing characters.
	    while (trimmed.endsWith(stringToTrim))
	        trimmed = trimmed.substring(0, trimmed.lastIndexOf(stringToTrim));
	
	    return trimmed;
	}
	
	/**
	 * Takes a collection of items and appends their string representations to each other
	 * using a delimiter between the elements to create a single string.
	 * 
	 * @param delimiter the delimiter to insert between elements
	 * @param items a collection of objects to join together
	 * @return
	 */
	public static <T> String join(final String delimiter, Iterable<T> items)
	{
		SeededAccumulator<T, T, StringBuilder> joiner = new SeededAccumulator<T, T, StringBuilder>(items, 
				new Mapper2<T, StringBuilder, StringBuilder>() {
					public StringBuilder map(T first, StringBuilder second)
					{
						if (second.length() > 0)
							second.append(delimiter);
						
						second.append(first);
						
						return second;
					}}, 
				new NullMapper<T>(), 
				new StringBuilder());
		
		return joiner.accumulate().toString();
	}

	/**
	 * Takes a collection of items and appends their string representations to each other
	 * using a delimiter between the elements to create a single string.
	 * 
	 * @param delimiter the delimiter to insert between elements
	 * @param items a collection of objects to join together
	 * @return
	 */
	public static <T> String join(String delimiter, T ... items)
	{
		return Strings.join(delimiter, Arrays.asList(items));
	}
	
	/**
	 * Takes an array of integers and appends their string representations to each other
	 * using a delimiter between the elements to create a single string.
	 * 
	 * @param delimiter the delimiter to insert between elements
	 * @param items an array of ints to join together
	 * @return
	 */
	public static String join(String delimiter, int[] items)
	{
		StringBuilder joinedString = new StringBuilder();
		for (int item : items)
		{
			if (joinedString.length() > 0)
				joinedString.append(delimiter);
			
			joinedString.append(item);
		}
		
		return joinedString.toString();
	}
	
	/**
	 * Takes an array of longs and appends their string representations to each other
	 * using a delimiter between the elements to create a single string.
	 * 
	 * @param delimiter the delimiter to insert between elements
	 * @param items an array of longs to join together
	 * @return
	 */
	public static String join(String delimiter, long[] items)
	{
		StringBuilder joinedString = new StringBuilder();
		for (long item : items)
		{
			if (joinedString.length() > 0)
				joinedString.append(delimiter);
			
			joinedString.append(item);
		}
		
		return joinedString.toString();
	}
}

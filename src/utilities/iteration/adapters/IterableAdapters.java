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

package utilities.iteration.adapters;

import java.io.Reader;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * A class containing methods to convert various objects into Iterable representations.
 */
public class IterableAdapters
{
	/**
	 * Converts an Iterator to an Iterable.
	 */
	public static <T> Iterable<T> toIterable(final Iterator<T> iterator)
	{
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return iterator;
			}
		};
	}
	
	/**
	 * Converts an Enumeration to an Iterable.
	 */
	public static <T> Iterable<T> toIterable(Enumeration<T> enumeration)
	{
		return new EnumerationIterable<T>(enumeration);
	}

	/**
	 * Presents the characters in a CharSequence as an Iterable.
	 */
	public static Iterable<Character> toIterable(CharSequence characters)
	{
		return new CharacterIterable(characters);
	}
	
	/**
	 * Presents the characters in a Reader as an Iterable.
	 */
	public static Iterable<Character> toIterable(Reader reader)
	{
		return new ReaderIterable(reader);
	}
}

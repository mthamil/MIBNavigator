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

package utilities.iteration.adapters;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import utilities.IOUtilities;
import utilities.iteration.ImmutableIterator;

/**
 * Presents the characters in a Reader as an Iterable.
 */
public class ReaderIterable implements Iterable<Character>
{
	private final Reader reader;
	
	public ReaderIterable(Reader reader)
	{
		this.reader = reader;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Character> iterator()
	{
		return new ReaderIterator(reader);
	}
	
	/**
	 * An iterator over the characters of a reader.
	 */
	private static class ReaderIterator extends ImmutableIterator<Character>
	{
		private final Reader reader;
		private int currentChar;
		
		public ReaderIterator(Reader reader)
		{
			this.reader = reader;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			boolean readSuccessful = true;
			try
			{
				currentChar = reader.read();
			}
			catch (IOException e) { readSuccessful = false; }
			
			boolean hasNext = readSuccessful && currentChar != -1;
			if (hasNext)
				return true;
			
			// If no more lines, clean up.
			IOUtilities.closeQuietly(reader);
			return false;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Character next()
		{
			return Character.valueOf((char)currentChar);
		}
	}
}

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import utilities.IOUtilities;
import utilities.iteration.ImmutableIterator;

/**
 * Presents the lines of a reader as an Iterable.  It is backed by a 
 * BufferedReader.
 */
public class BufferedLinesIterable implements Iterable<String>
{
	private final Reader in;
	
	public BufferedLinesIterable(Reader in)
	{
		this.in = in;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<String> iterator()
	{
		return new BufferedLinesIterator(new BufferedReader(in));
	}
	
	/**
	 * An Iterator over the lines of a BufferedReader.
	 */
	private static class BufferedLinesIterator extends ImmutableIterator<String>
	{
		private final BufferedReader reader;
		private String currentLine;
		
		public BufferedLinesIterator(BufferedReader reader)
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
				currentLine = reader.readLine();
			}
			catch (IOException e) { readSuccessful = false; }
			
			boolean hasNext = readSuccessful && currentLine != null;
			if (hasNext)
				return true;
			
			// If no more lines, clean up.
			IOUtilities.closeQuietly(reader);
			return false;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public String next()
		{
			return currentLine;
		}
	}
}

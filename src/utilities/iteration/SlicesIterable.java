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

package utilities.iteration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * An Iterable that divides a source iterable into sequential chunks of a given size.
 *
 */
public class SlicesIterable<T> implements Iterable<Iterable<T>>
{
	private final int sliceSize;
	private final Iterable<T> source;
	
	public SlicesIterable(Iterable<T> source, int sliceSize)
	{
		if (sliceSize < 1)
			throw new IllegalArgumentException("sliceSize must be greater than zero.");
		
		this.source = source;
		this.sliceSize = sliceSize;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Iterable<T>> iterator()
	{
		return new SlicesIterator<T>(source.iterator(), sliceSize);
	}
	
	private static class SlicesIterator<T> extends ImmutableIterator<Iterable<T>>
	{
		private final int sliceSize;
		private final Iterator<T> source;
		
		private Collection<T> currentSlice;
		
		public SlicesIterator(Iterator<T> source, int sliceSize)
		{
			this.source = source;
			this.sliceSize = sliceSize;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return source.hasNext();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Iterable<T> next()
		{
			currentSlice = new ArrayList<T>(sliceSize);
			
			int i = 1;
			while (source.hasNext())
			{
				currentSlice.add(source.next());
				
				if (i % sliceSize == 0)
					break;
				
				i++;
			}
			
			return currentSlice;
		}
	}
}

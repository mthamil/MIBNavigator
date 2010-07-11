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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Iterable that iterates over multiple Iterables one after another.
 */
public class ConcatenatingIterable<T> implements Iterable<T>
{
	private Collection<Iterable<T>> iterables;
	
	private ConcatenatingIterable()
	{
		iterables = new ArrayList<Iterable<T>>();
	}
	
	public ConcatenatingIterable(Iterable<T> ... iterables)
	{
		this();
		
		for (Iterable<T> iterable : iterables)
			this.iterables.add(iterable);
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator()
	{
		return new ConcatenatingIterator<T>(iterables.iterator());
	}
	
	private static class ConcatenatingIterator<T> extends ImmutableIterator<T>
	{
		private Iterator<Iterable<T>> iterables;
		
		private Iterator<T> currentIterator;
		
		public ConcatenatingIterator(Iterator<Iterable<T>> iterables)
		{
			this.iterables = iterables;
		}
		
		public ConcatenatingIterator(Iterable<T> ... iterables)
		{
			this.iterables = Arrays.asList(iterables).iterator();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			if (currentIterator == null)
			{
				// Find an iterable that has elements.
				while (iterables.hasNext())
				{
					currentIterator = iterables.next().iterator();
					if (currentIterator.hasNext())
						return true;
				}
			}
			else
			{
				if (currentIterator.hasNext())
					return true;
				
				// Find an iterable that has elements.
				while (iterables.hasNext())
				{
					currentIterator = iterables.next().iterator();
					if (currentIterator.hasNext())
						return true;
				}
			}
			
			return false;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public T next()
		{
			return currentIterator.next();
		}
	}

}

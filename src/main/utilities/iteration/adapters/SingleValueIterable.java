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

import java.util.Iterator;
import java.util.NoSuchElementException;

import utilities.iteration.ImmutableIterator;

/**
 * An Iteratable for a single value.
 */
public class SingleValueIterable<T> implements Iterable<T>
{
	private final T value;
	
	public SingleValueIterable(T value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator()
	{
		return new SingleValueIterator();
	}
	
	private class SingleValueIterator extends ImmutableIterator<T>
	{
		private boolean finished;

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return !finished;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public T next()
		{
			if (!finished)
			{
				finished = true;
				return value;
			}
			
			throw new NoSuchElementException(); 
		}
	}
}
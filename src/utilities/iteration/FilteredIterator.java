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

import java.util.Iterator;
import java.util.NoSuchElementException;

import utilities.Predicate;

/**
 * @author matt
 *
 */
public class FilteredIterator<T> extends ImmutableIterator<T>
{
	private Iterator<T> source;
	private Predicate<T> condition;
	
	private T nextItem;
	
	public FilteredIterator(Iterator<T> source, Predicate<T> condition)
	{
		this.source = source;
		this.condition = condition;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		// Search through the source iterator until an element that matches the predicate is found.
		boolean foundNext = false;
		while (source.hasNext())
		{
			T next = source.next();
			if (condition.matches(next))
			{
				nextItem = next;
				foundNext = true;
				break;
			}
		}
		
		return foundNext;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public T next()
	{
		if (nextItem != null)
			return nextItem;
			
		throw new NoSuchElementException(); 
	}
}

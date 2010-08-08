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

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An Iterator that only returns the unique elements from a source sequence.
 */
@LazilyEvaluated
public class DistinctIterator<T> extends ImmutableIterator<T>
{
	private Iterator<T> source;
	
	private Set<T> alreadySeen = new HashSet<T>();
	private T current;
	
	public DistinctIterator(Iterator<T> source)
	{
		this.source = source;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		while (source.hasNext())
		{
			T next = source.next();
			if (!alreadySeen.contains(next))
			{
				current = next;
				alreadySeen.add(current);
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
		if (current != null)
			return current;
		
		throw new NoSuchElementException(); 
	}

}

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

import java.util.Iterator;

/**
 * Class that wraps an Iterator in an InspectableIterator.
 * @param <E>
 */
public class InspectableIteratorWrapper<E> implements InspectableIterator<E>
{
	private Iterator<E> wrappedIterator;
	private E currentElement;
	
	/**
	 * Creates a new inspectable iterator wrapping an existing iterator.
	 * @param iterator
	 */
	public InspectableIteratorWrapper(Iterator<E> iterator)
	{
		wrappedIterator = iterator;
	}
	
	/**
	 * @see utilities.InspectableIterator#current
	 */
	public E current()
	{
		return currentElement;
	}

	/**
	 * @see java.util.Iterator#hasNext
	 */
	public boolean hasNext()
	{
		return wrappedIterator.hasNext();
	}

	/**
	 * @see java.util.Iterator#next
	 */
	public E next()
	{
		currentElement = wrappedIterator.next();
		return currentElement;
	}

	/**
	 * @see java.util.Iterator#remove
	 */
	public void remove()
	{
		wrappedIterator.remove();
	}
}

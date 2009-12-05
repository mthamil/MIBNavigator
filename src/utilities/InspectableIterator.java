/**
 * Utilities
 *
 * Copyright (C) 2009, Matt Hamilton <matthamilton@live.com>
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
 * A simple extension of <code>Iterator&lt;E&gt;</code> that allows access to
 * the current element without advancing to the next.
 *
 * @param <E> the type of object being iterated over
 */
public interface InspectableIterator<E> extends Iterator<E>
{
	/**
	 * Gets the current element. That is, the last element returned by
	 * a call to <code>next()</code>.
	 * @return the current element
	 */
	E current();
}

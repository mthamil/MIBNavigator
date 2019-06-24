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

import utilities.mappers.Mapper;

/**
 *  An iterator that takes an existing iterator and maps its elements
 *  using a given Mapper.
 */
@LazilyEvaluated
public class MappingIterator<S, D> extends ImmutableIterator<D>
{
	private Iterator<? extends S> source;
	private Mapper<? super S, D> mapper;
	
	public MappingIterator(Iterator<? extends S> source, Mapper<? super S, D> mapper)
	{
		this.source = source;
		this.mapper = mapper;
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
	public D next()
	{
		return mapper.map(source.next());
	}
}

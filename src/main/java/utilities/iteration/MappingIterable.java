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
 *  A lazy iterable that takes an existing iterable and maps its elements
 *  using a given Mapper.
 *  
 *  @param <S> The type of objects in the source iterable
 *  @param <D> The type of objects in the destination iterable
 */
@LazilyEvaluated
public class MappingIterable<S, D> implements Iterable<D>
{
	private Iterable<? extends S> source;
	private Mapper<? super S, D> mapper;
	
	public MappingIterable(Iterable<? extends S> source, Mapper<? super S, D> mapper)
	{
		this.source = source;
		this.mapper = mapper;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<D> iterator()
	{
		return new MappingIterator<S, D>(source.iterator(), mapper);
	}
}

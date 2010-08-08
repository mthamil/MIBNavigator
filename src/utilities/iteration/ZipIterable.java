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

import utilities.mappers.ZipMapper;

/**
 * An Iterable that combines two iterables into a single iterable
 * using a mapping function.  If the iterables are of different lengths,
 * the desination iterable will be as long as the shortest of the two, and
 * the remaining items will be lost.
 */
@LazilyEvaluated
public class ZipIterable<S1, S2, D> implements Iterable<D>
{
	private Iterable<S1> first;
	private Iterable<S2> second;
	private ZipMapper<S1, S2, D> mapper;
	
	public ZipIterable(Iterable<S1> first, Iterable<S2> second, ZipMapper<S1, S2, D> mapper)
	{
		this.first = first;
		this.second = second;
		this.mapper = mapper;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<D> iterator()
	{
		return new ZipIterator<S1, S2, D>(first.iterator(), second.iterator(), mapper);
	}

	private static class ZipIterator<S1, S2, D> extends ImmutableIterator<D>
	{
		private Iterator<S1> first;
		private Iterator<S2> second;
		private ZipMapper<S1, S2, D> mapper;
		
		public ZipIterator(Iterator<S1> first, Iterator<S2> second, ZipMapper<S1, S2, D> mapper)
		{
			this.first = first;
			this.second = second;
			this.mapper = mapper;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return first.hasNext() && second.hasNext();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public D next()
		{
			S1 firstNext = first.next();
			S2 secondNext = second.next();
			D result = mapper.map(firstNext, secondNext);
			return result;
		}
	}
}

/**
 * MIB Navigator
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
 * Maps each element of an iterable of one type onto an iterable of another type,
 * maps each element of the mapped iterables using the given result mapper, and
 * flattens the results.
 * 
 * @param <S> The type of objects in the source iterable
 * @param <I> The type of the intermediary result
 * @param <D> The type of objects in the destination iterable
 */
@LazilyEvaluated
public class MultiMappingIterable<S, I, D> implements Iterable<D>
{
	private Iterable<? extends S> source;
	private Mapper<? super S, Iterable<I>> mapper;
	private Mapper<I, D> resultMapper;
	
	public MultiMappingIterable(Iterable<? extends S> source, Mapper<? super S, Iterable<I>> mapper, Mapper<I, D> resultMapper)
	{
		this.source = source;
		this.mapper = mapper;
		this.resultMapper = resultMapper;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<D> iterator()
	{
		return new MultiMappingIterator<S, I, D>(source.iterator(), mapper, resultMapper);
	}
		
	private static class MultiMappingIterator<S, I, D> extends ImmutableIterator<D>
	{
		private Iterator<? extends S> source;
		private Mapper<? super S, Iterable<I>> mapper;
		private Mapper<I, D> resultMapper;
		
		private Iterator<I> currentMappedIterator;
		private boolean withinSubIterator;
		
		public MultiMappingIterator(Iterator<? extends S> source, Mapper<? super S, Iterable<I>> mapper, Mapper<I, D> resultMapper)
		{
			this.source = source;
			this.mapper = mapper;
			this.resultMapper = resultMapper;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			if (!withinSubIterator)
				return advance();
			
			if (currentMappedIterator != null && currentMappedIterator.hasNext())
				return true;
			
			return advance();
		}
		
		private boolean advance()
		{
			if (source.hasNext())
			{
				S next = source.next();
				Iterable<I> mapped = mapper.map(next);
				currentMappedIterator = mapped.iterator();
				withinSubIterator = currentMappedIterator.hasNext();
				return withinSubIterator;
			}
			
			return false;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public D next()
		{
			return resultMapper.map(currentMappedIterator.next());
		}
	}
}

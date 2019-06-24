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

/**
 * An iterable of integers between two numbers, inclusive. 
 * If <code>from</code> > <code>to</code>, the range is increasing.  
 * If <code>from</code> < <code>to</code>, the range is decreasing.
 */
@LazilyEvaluated
public class RangeIterable implements Iterable<Integer>
{
	private final int from;
	private final int to;

	public RangeIterable(int from, int to)
	{
		this.from = from;
		this.to = to;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Integer> iterator()
	{
		return new RangeIterator(from, to);
	}

	private static class RangeIterator extends ImmutableIterator<Integer>
	{
		private final int from;
		private final int to;
		
		private int current;

		private boolean increasing;

		public RangeIterator(int from, int to)
		{
			this.from = from;
			this.to = to;
			
			this.increasing = from < to;
			this.current = increasing ? from - 1 : from + 1;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return increasing ? current < to : current > to;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Integer next()
		{
			return increasing ? ++current : --current;
		}
	}
}

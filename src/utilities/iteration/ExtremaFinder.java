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

import utilities.mappers.Mapper;

/**
 * Finds the minimum or maximum value in an Iterable.
 */
public class ExtremaFinder<S, D extends Comparable<D>>
{
	public static enum ExtremaType 
	{ 
		Min(1), Max(-1); 
		
		private int comparisonOutcome;
		
		private ExtremaType(int comparisonOutcome)
		{
			this.comparisonOutcome = comparisonOutcome;
		}
		
		public int comparisonOutcome()
		{
			return comparisonOutcome;
		}
	}
	
	private Iterable<S> source; 
	private Mapper<S, D> selector; 
	private ExtremaType extremaType;
	
	public ExtremaFinder(Iterable<S> source, Mapper<S, D> selector, ExtremaType extremaType)
	{
		this.source = source;
		this.selector = selector;
		this.extremaType = extremaType;
	}
	
	public D find()
	{
		D extrema = null;
		for (S item : source)
		{
			D current = selector.map(item);
			if (extrema == null)
			{
				extrema = current;
			}
			else
			{
				if (extrema.compareTo(current) == extremaType.comparisonOutcome())
					extrema = current;
			}
		}
		
		return extrema;
	}
}

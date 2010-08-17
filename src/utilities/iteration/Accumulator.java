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
import utilities.mappers.Mapper2;

/**
 * An object that applies an accumulation function to an Iterable.
 */
public class Accumulator<S, R>
{
	private final Iterable<S> items;
	private final Mapper<S, R> selector;
	private final Mapper2<R, R, R> accumulator;
	
	private R accumulation;
	
	public Accumulator(Iterable<S> items, Mapper2<R, R, R> accumulator, Mapper<S, R> selector)
	{
		this.items = items;
		this.accumulator = accumulator;
		this.selector = selector;
	}
	
	public Accumulator(Iterable<S> items, Mapper2<R, R, R> accumulator, Mapper<S, R> selector, S seed)
	{
		this(items, accumulator, selector);
		accumulation = selector.map(seed);
		seedChosen = true;
	}
	
	private boolean seedChosen;
	
	public R accumulate()
	{
		for (S item : items)
		{
			R next = selector.map(item);
			if (!seedChosen)
			{
				accumulation = next;
				seedChosen = true;
			}
			else
			{
				accumulation = accumulator.map(next, accumulation);
			}
		}
		
		return accumulation;
	}
}

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
 * 
 * @param <S> The type of the source sequence
 * @param <I> The type of the intermediary value
 * @param <R> The type of the accumulated result
 */
public class SeededAccumulator<S, I, R> implements Accumulator<R>
{
	private final Iterable<? extends S> items;
	private final Mapper<? super S, I> selector;
	private final Mapper2<I, R, R> accumulator;
	
	private R accumulation;
	
	/**
	 * Creates a new accumulator.
	 * 
	 * @param items The sequence to apply the accumulation function to
	 * @param accumulator A function that takes an intermediary object and the current accumulated 
	 *                    value and returns a new accumulation
	 * @param selector A function that maps source items into intermediate values
	 * @param seed The initial seed value
	 */
	public SeededAccumulator(Iterable<? extends S> items, Mapper2<I, R, R> accumulator, Mapper<? super S, I> selector, R seed)
	{
		this.items = items;
		this.selector = selector;
		this.accumulator = accumulator;
		accumulation = seed;
	}
	
	public R accumulate()
	{
		for (S item : items)
		{
			I next = selector.map(item);
			accumulation = accumulator.map(next, accumulation);
		}
		
		return accumulation;
	}
}

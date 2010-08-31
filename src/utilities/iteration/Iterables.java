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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import utilities.Predicate;
import utilities.iteration.ExtremaType;
import utilities.iteration.InfiniteSequenceUnsafe.Likelihood;
import utilities.iteration.adapters.SingleValueIterable;
import utilities.mappers.Mapper;
import utilities.mappers.Mapper2;
import utilities.mappers.NullMapper;

/**
 * Class containing useful Iterable utility methods.
 */
public final class Iterables
{
	private Iterables() { }
	
	/**
	 * Returns an empty iterable.
	 */
	public static <T> Iterable<T> empty()
	{
		return new EmptyIterable<T>();
	}
	
	/**
	 * Converts a single value to an Iterable.
	 */
	public static <T> Iterable<T> toIterable(T value)
	{
		return new SingleValueIterable<T>(value);
	}
	
	/**
	 * Converts an Iterable into a List. 
	 * If the source Iterable is an infinite sequence, this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	public static <T> List<T> toList(Iterable<T> iterable)
	{
		List<T> list = new ArrayList<T>();
		for (T item : iterable)
			list.add(item);
		
		return list;
	}
	
	/**
	 * Converts an Iterable to a Map.
	 * If the source Iterable is an infinite sequence, this method will not return.
	 * 
	 * @param <K> The type of key
	 * @param <V> The type of value
	 * @param <S> The source object type
	 * @param source The iterable to convert to a map
	 * @param keyMapper The key selector
	 * @param valueMapper The value selector
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	public static <K, V, S> Map<K, V> toMap(Iterable<S> source, Mapper<S, K> keyMapper, Mapper<S, V> valueMapper)
	{
		Map<K, V> map = new HashMap<K, V>();
		for (S item : source)
		{
			K key = keyMapper.map(item);
			V value = valueMapper.map(item);
			
			map.put(key, value);
		}
		
		return map;
	}
	
	/**
	 * Maps an iterable of one type onto an iterable of another type.
	 * 
	 * @param <S> The type of objects in the source iterable
	 * @param <D> The type of objects in the destination iterable
	 * @param source The iterable to map
	 * @param mapper The mapping function
	 * @return An iterable of the destination type
	 */
	@LazilyEvaluated
	public static <S, D> Iterable<D> map(Iterable<S> source, Mapper<S, D> mapper)
	{
		return new MappingIterable<S, D>(source, mapper);
	}

	/**
	 * Maps each element of an iterable of one type onto an iterable of another type and
	 * flattens the results.
	 * 
	 * @param <S> The type of objects in the source iterable
	 * @param <D> The type of objects in the destination iterable
	 * @param source The iterable to map
	 * @param mapper The mapping function
	 * @return An iterable of the destination type
	 */
	@LazilyEvaluated
	public static <S, D> Iterable<D> multiMap(Iterable<S> source, Mapper<S, Iterable<D>> mapper)
	{
		return new MultiMappingIterable<S, D, D>(source, mapper, new NullMapper<D>());
	}
	
	/**
	 * Maps each element of an iterable of one type onto an iterable of another type,
	 * maps each element of the mapped iterables using the given result mapper, and
	 * flattens the results.
	 * 
	 * @param <S> The type of objects in the source iterable
	 * @param <I> The type of the intermediary result
	 * @param <D> The type of objects in the destination iterable
	 * @param source The iterable to map
	 * @param mapper The iterable mapping function
	 * @param resultMapper maps the intermediary results to the final destination type
	 * @return An iterable of the destination type
	 */
	@LazilyEvaluated
	public static <S, I, D> Iterable<D> multiMap(Iterable<S> source, Mapper<S, Iterable<I>> mapper, Mapper<I, D> resultMapper)
	{
		return new MultiMappingIterable<S, I, D>(source, mapper, resultMapper);
	}
	
	/**
	 * Creates a grouping of the given items according to the keys created using the given
	 * Mapper.
	 */
	@LazilyEvaluated
	public static <K, V> Iterable<Grouping<K, V>> groupBy(Iterable<V> source, Mapper<V, K> keyMapper)
	{
		return new GroupingIterable<K, V>(source, keyMapper);
	}
	
	/**
	 * Returns an Iterable over the elements from <code>source</code> where <code>condition</code> is true.
	 * @param source The items to filter
	 * @param condition The filter criteria
	 * @return
	 */
	@LazilyEvaluated
	public static <T> Iterable<T> filter(Iterable<T> source, Predicate<T> condition)
	{
		return new FilteredIterable<T>(source, condition);
	}
	
	/**
	 * Returns the first item in an iterable. If no elements exist, an exception is thrown.
	 */
	public static <T> T first(Iterable<T> items)
	{
		Iterator<T> iterator = items.iterator();
		if (iterator.hasNext())
			return iterator.next();
		
		throw new NoSuchElementException(); 
	}
	
	/**
	 * Returns the first item in an iterable or null if no items exist.
	 */
	public static <T> T firstOrNull(Iterable<T> items)
	{
		Iterator<T> iterator = items.iterator();
		if (iterator.hasNext())
			return iterator.next();
		
		return null;
	}
	
	/**
	 * Returns the last item in a sequence. If no elements exist,
	 * an exception is thrown.
	 * If the source Iterable is an infinite sequence, this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	@SuppressWarnings("unchecked")
	public static <T> T last(Iterable<T> items)
	{
		if (items instanceof List<?>)
		{
			List<?> list = (List<?>)items;
			if (list.isEmpty())
				throw new NoSuchElementException();
			
			return (T)list.get(list.size() - 1);
		}
		
		T last = null;
		for (T item : items)
			last = item;
		
		if (last == null)
			throw new NoSuchElementException();
		
		return last;
	}
	
	/**
	 * Returns the last item in a sequence. If no elements exist,
	 * null is returned.
	 * If the source Iterable is an infinite sequence, this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	@SuppressWarnings("unchecked")
	public static <T> T lastOrNull(Iterable<T> items)
	{
		if (items instanceof List<?>)
		{
			List<?> list = (List<?>)items;
			return list.isEmpty() ? null : (T)list.get(list.size() - 1);
		}
		
		T last = null;
		for (T item : items)
			last = item;
		
		return last;
	}
	
	/**
	 * Determines whether an iterable contains an item.
	 * If the source Iterable is an infinite sequence and the desired item is not contained in it, 
	 * this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Sometimes)
	public static <T> boolean contains(Iterable<T> items, T expected)
	{
		for (T item : items)
		{
			if (item.equals(expected))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Determines whether an iterable contains an item.
 	 * If the source Iterable is an infinite sequence and the desired item is not contained in it, 
	 * this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Sometimes)
	public static <T> boolean contains(Iterable<T> items, T expected, Comparator<T> comparator)
	{
		for (T item : items)
		{
			if (comparator.compare(expected, item) == 0)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Returns whether an iterable has any elements.
	 */
	public static <T> boolean isEmpty(Iterable<T> items)
	{
		if (items instanceof Collection<?>)
		{
			Collection<?> collection = (Collection<?>)items;
			return collection.isEmpty();
		}
		
		return !items.iterator().hasNext();
	}
	
	/**
	 * Returns true if all elements of an iterable meet a condition.
	 * If the source Iterable is an infinite sequence and every element meets the criteria, 
	 * this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Sometimes)
	public static <T> boolean all(Iterable<T> items, Predicate<T> condition)
	{
		for (T item : items)
		{
			if (!condition.matches(item))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Returns true if any element of an iterable meets a condition.
	 * If the source Iterable is an infinite sequence and no elements meet the criteria, 
	 * this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Sometimes)
	public static <T> boolean any(Iterable<T> items, Predicate<T> condition)
	{
		for (T item : items)
		{
			if (condition.matches(item))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the number of elements in an Iterable.
	 * Contains optimizations for collections with O(1)
	 * size access.
	 * If the source Iterable is an infinite sequence, this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	public static <T> int size(Iterable<T> items)
	{
		if (items instanceof Collection<?>)
		{
			Collection<?> collection = (Collection<?>)items;
			return collection.size();
		}
		
		int i = 0;
		for (Iterator<T> iterator = items.iterator(); iterator.hasNext();)
			i++;
		
		return i;
	}
	
	/**
	 * Returns the number of elements in an Iterable that satisfy a condition.
	 * If the source Iterable is an infinite sequence, this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	public static <T> int count(Iterable<T> items, Predicate<T> condition)
	{
		int i = 0;
		for (T item : items)
		{
			if (condition.matches(item))
				i++;
		}
		
		return i;
	}
	
	/**
	 * Returns the unique elements from the source sequence.
	 */
	@LazilyEvaluated
	public static <T> Iterable<T> distinct(Iterable<T> source)
	{
		return new DistinctIterable<T>(source);
	}

	/**
	 * Returns the set union of the given sequences.
	 */
	public static <T> Iterable<T> union(Iterable<T> ... iterables)
	{
		return new UnionIterable<T>(iterables);
	}
	
	/**
	 * Splits an Iterable into multiple Iterables of the given slice size.  If there are remaining
	 * items numbering less than the slice size, the final Iterable will have whatever items are left.
	 */
	@LazilyEvaluated
	public static <T> Iterable<Iterable<T>> slices(Iterable<T> items, int sliceSize)
	{
		return new SlicesIterable<T>(items, sliceSize);
	}
	
	/**
	 * Joins multiple iterables together.
	 */
	@LazilyEvaluated
	public static <T> Iterable<T> concat(Iterable<T> ... iterables)
	{
		return new ConcatenatingIterable<T>(iterables);
	}
	
	/**
	 * Joins two iterables into a single iterable using the given mapping function.
	 * If the iterables are of different lengths, the desination iterable will be as 
	 * long as the shortest of the two, and the remaining items will be lost.
	 */
	@LazilyEvaluated
	public static <S1, S2, D> Iterable<D> zip(Iterable<S1> first, Iterable<S2> second, Mapper2<S1, S2, D> mapper)
	{
		return new ZipIterable<S1, S2, D>(first, second, mapper);
	}
	
	/**
	 * Returns an iterable over a range of numbers. If <code>from</code> > <code>to</code>, the range
	 * is increasing.  If <code>from</code> < <code>to</code>, the range is decreasing.
	 * @param from The start of the range
	 * @param to The end of the range (inclusive)
	 */
	@LazilyEvaluated
	public static Iterable<Integer> range(int from, int to)
	{
		return new RangeIterable(from, to);
	}
	
	/**
	 * Applies an accumulator function to a sequence. The first element acts as the seed.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	public static <S, R> R aggregate(Iterable<S> source, Mapper2<R, R, R> accumulator, Mapper<S, R> selector)
	{
		SeedlessAccumulator<S, R> acc = new SeedlessAccumulator<S, R>(source, accumulator, selector);
		return acc.accumulate();
	}
	
	/**
	 * Applies an accumulator function to a sequence with the given seed.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	public static <S, I, R> R aggregate(Iterable<S> source, Mapper2<I, R, R> accumulator, Mapper<S, I> selector, R seed)
	{
		SeededAccumulator<S, I, R> acc = new SeededAccumulator<S, I, R>(source, accumulator, selector, seed);
		return acc.accumulate();
	}
	
	/**
	 * Returns the minimum value in an Iterable.
	 * If the source Iterable is an infinite sequence, this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	public static <S, D extends Comparable<D>> D min(Iterable<S> source, Mapper<S, D> selector)
	{
		return extrema(source, selector, ExtremaType.Min);
	}
	
	/**
	 * Returns the minimum value in an Iterable.
	 * If the source Iterable is an infinite sequence, this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	public static <T extends Comparable<T>> T min(Iterable<T> source)
	{
		return min(source, new NullMapper<T>());
	}
	
	/**
	 * Returns the maximum value in an Iterable.
	 * If the source Iterable is an infinite sequence, this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	public static <S, D extends Comparable<D>> D max(Iterable<S> source, Mapper<S, D> selector)
	{
		return extrema(source, selector, ExtremaType.Max);
	}
	
	/**
	 * Returns the maximum value in an Iterable.
	 * If the source Iterable is an infinite sequence, this method will not return.
	 */
	@InfiniteSequenceUnsafe(Likelihood.Always)
	public static <T extends Comparable<T>> T max(Iterable<T> source)
	{
		return max(source, new NullMapper<T>());
	}
	
	private static <S, D extends Comparable<D>> D extrema(Iterable<S> source, Mapper<S, D> selector, final ExtremaType extremaType)
	{
		return aggregate(source, new Mapper2<D, D, D>() {
			public D map(D first, D second)
			{
				if (second.compareTo(first) == extremaType.comparisonOutcome())
					return first;
				
				return second;
			}
		}, selector);
	}

}

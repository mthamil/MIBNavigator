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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import utilities.Mapper;
import utilities.Predicate;
import utilities.iteration.adapters.SingleValueIterable;

/**
 * Class containing useful Iterable utility methods.
 */
public class Iterables
{
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
	public static <T> Iterable<T> asIterable(T value)
	{
		return new SingleValueIterable<T>(value);
	}
	
	/**
	 * Converts an Iterable into a List. If the Iterable does not terminate, 
	 * this method will not return.
	 */
	public static <T> List<T> asList(Iterable<T> iterable)
	{
		List<T> list = new ArrayList<T>();
		for (T item : iterable)
			list.add(item);
		
		return list;
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
	public static <S, I, D> Iterable<D> multiMap(Iterable<S> source, Mapper<S, Iterable<I>> mapper, Mapper<I, D> resultMapper)
	{
		return new MultiMappingIterable<S, I, D>(source, mapper, resultMapper);
	}
	
	/**
	 * Creates a grouping of the given items according to the keys created using the given
	 * Mapper.
	 */
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
	 */
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
	 */
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
	 */
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
	 */
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
	 */
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
	 */
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
	 */
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
	 */
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
	 * Splits an Iterable into Iterables of the given slice size.  If there are remaining
	 * items numbering less than the slice size, the final Iterable will have whatever items are left.
	 */
	public static <T> Iterable<Iterable<T>> slices(Iterable<T> items, int sliceSize)
	{
		if (sliceSize < 1)
			throw new IllegalArgumentException("sliceSize must be greater than zero.");
		
		return new SlicesIterable<T>(items, sliceSize);
	}
	
	/**
	 * Joins multiple iterables together.
	 */
	public static <T> Iterable<T> concat(Iterable<T> ... iterables)
	{
		return new ConcatenatingIterable<T>(iterables);
	}
	
	/**
	 * Joins two iterables together.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> concat(Iterable<T> first, Iterable<T> second)
	{
		return new ConcatenatingIterable<T>(first, second);
	}
	
}

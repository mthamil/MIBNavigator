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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utilities.Mapper;
import utilities.Predicate;

/**
 * An iterable that groups items lazily by a common key.
 * @param <K> The type of the keys
 * @param <V> The type of the values
 */
public class GroupingIterable<K, V> implements Iterable<Grouping<K, V>>
{
	private Iterable<V> source;
	private Mapper<V, K> keyMapper;
	
	public GroupingIterable(Iterable<V> source, Mapper<V, K> keyMapper)
	{
		this.source = source;
		this.keyMapper = keyMapper;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Grouping<K, V>> iterator()
	{
		return new GroupingIterator<K, V>(source, keyMapper);
	}

	private static class GroupingIterator<K, V> extends ImmutableIterator<Grouping<K, V>>
	{
		private Map<K, Grouping<K, V>> internalMap = new HashMap<K, Grouping<K, V>>();
		
		private Iterable<V> source;
		private Iterator<V> iterator;
		private Mapper<V, K> keyMapper;
		
		private Grouping<K, V> nextGroup;
		
		public GroupingIterator(Iterable<V> source, Mapper<V, K> keyMapper)
		{
			this.source = source;
			this.iterator = source.iterator();
			this.keyMapper = keyMapper;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			if (!iterator.hasNext())
				return false;
			
			// If the next item's key has never been seen before, then we definitely have another group.
			V next = iterator.next();
			K key = keyMapper.map(next);
			if (!internalMap.containsKey(key))
			{
				newGroup(key);
				return true;
			}
			
			// Search for the next unseen key.
			while (internalMap.containsKey(key))
			{
				if (iterator.hasNext())
				{
					next = iterator.next();
					key = keyMapper.map(next);
				}
				else
				{
					next = null;
					key = null;
					break;
				}
			}

			if (next != null && key != null)
			{
				newGroup(key);
				return true;
			}
		
			return false;
		}
		
		private void newGroup(K key)
		{
			Grouping<K, V> group = new GroupingImpl<K, V>(key, source, keyMapper);
			nextGroup = group;
			internalMap.put(key, group);
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Grouping<K, V> next()
		{
			return nextGroup;
		}
	}
	
	private static class GroupingImpl<K, V> implements Grouping<K, V>
	{
		private K key;
		private Iterable<V> source;
		private Mapper<V, K> keyMapper;
		
		public GroupingImpl(K key, Iterable<V> source, Mapper<V, K> keyMapper)
		{
			this.key = key;
			this.source = source;
			this.keyMapper = keyMapper;
		}

		/* (non-Javadoc)
		 * @see utilities.iteration.Grouping#getKey()
		 */
		public K getKey()
		{
			return key;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		public Iterator<V> iterator()
		{
			return new FilteredIterator<V>(source.iterator(), new GroupingFilter<K, V>(key, keyMapper));
		}
		
		private static class GroupingFilter<K, V> implements Predicate<V>
		{
			private Mapper<V, K> keyMapper;
			private K key;
			
			public GroupingFilter(K desiredKey, Mapper<V, K> keyMapper)
			{
				this.keyMapper = keyMapper;
				key = desiredKey;
			}
			
			/* (non-Javadoc)
			 * @see utilities.Predicate#matches(java.lang.Object)
			 */
			public boolean matches(V value)
			{
				return keyMapper.map(value).equals(key);
			}
		}
	}
}

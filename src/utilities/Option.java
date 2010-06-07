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

package utilities;

import java.util.HashMap;
import java.util.Map;

public abstract class Option<T>
{
	private static Map<Class<?>, None<?>> noneInstances = new HashMap<Class<?>, None<?>>(); 
	
	/**
	 * Returns a None instance of the given type.
	 */
	public static <T> Option<T> none(Class<T> c)
	{
		@SuppressWarnings("unchecked")
		None<T> instance = (None<T>)noneInstances.get(c);
		if (instance == null)
		{
			instance = new None<T>();
			noneInstances.put(c, instance);
		}
		
		return instance;
	}
	
	/**
	 * Returns a Some instance of the given type.
	 */
	public static <T> Option<T> some(T value)
	{
		return new Some<T>(value);
	}
	
	/**
	 * Whether an Option has a value.
	 */
	public abstract boolean hasValue();
	
	/**
	 * An Option's value if it has one.
	 */
	public abstract T getValue();

	private static class Some<T> extends Option<T>
	{
		private T value;

		public Some(T value)
		{
			this.value = value;
		}
		
		@Override
		public T getValue()
		{
			return value;
		}

		@Override
		public boolean hasValue()
		{
			return true;
		}
		
		@Override
		public boolean equals(Object other)
		{
			if (this == other)
				return true;
			
			if (other == null)
				return false;
			
			if (other instanceof Some<?>)
			{
				Some<?> some = (Some<?>)other;
				if (some.getValue().getClass().equals(this.getValue().getClass()))
				{
					return some.getValue().equals(this.getValue());
				}
			}
			
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return value.hashCode();
		}
	}
	
	private static class None<T> extends Option<T>
	{
		@Override
		public T getValue()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasValue()
		{
			return false;
		}
	}
	
}

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

package utilities.parsing;

/**
 * A parser that returns a default value if parsing fails.
 * 
 * @param <T>
 */
public abstract class DefaultValueParser<T> implements Parser<T>
{
	/** The value that may be used as a fallback if parsing fails. */
	private T defaultValue;
	
	public DefaultValueParser(T defaultValue)
	{
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Returns the value that may be used as a fallback if parsing fails.
	 * @return
	 */
	public T getDefault()
	{
		return defaultValue;
	}
	
	/**
	 * Sets the value that may be used as a fallback if parsing fails.
	 * @param defaultValue
	 */
	public void setDefault(T defaultValue)
	{
		this.defaultValue = defaultValue;
	}
	
	public abstract T parse(String stringValue);
}

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
 * A generic interface for objects that can parse strings
 * into a value of another type.
 *
 * @param <T> The type of value desired from parsing.
 */
public interface Parser<T>
{
	/**
	 * Parses a string to a value of the given type.
	 * @param stringValue the string to parse
	 * @return A value of type T
	 */
	public T parse(String stringValue);
}

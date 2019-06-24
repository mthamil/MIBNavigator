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

package utilities.mappers;

/**
 *	Interface for a function that maps an object of one type to another.
 *
 *  @param <S> The type of the parameter
 *  @param <R> The type of the result
 */
public interface Mapper<S, R>
{
	/**
	 * Maps an object of type <code>S</code> onto an
	 * object of type <code>R</code>.
	 * @param item The object to map
	 * @return A result of type <code>R</code>
	 */
	R map(S item);
}

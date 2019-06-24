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
 *	Interface for a function that maps two objects onto a single result.
 *
 *  @param <S1> The type of the first parameter
 *  @param <S2> The type of the second parameter
 *  @param <R> The type of the result
 */
public interface Mapper2<S1, S2, R>
{
	/**
	 * Maps an object of type <code>S1</code> and an object of type <code>S2</code>
	 * onto an object of type <code>R</code>.
	 * @param first The first parameter
	 * @param second The second parameter
	 * @return A result of type <code>R</code>
	 */
	R map(S1 first, S2 second);
}

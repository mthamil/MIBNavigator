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
 *	Interface for a class that maps two objects to a single object.
 *
 *  @param <S1> The type of the first source object
 *  @param <S2> The type of the second source object
 *  @param <D> The type of the destination object
 */
public interface ZipMapper<S1, S2, D>
{
	/**
	 * Maps an object of type <code>S1</code> and an object of type 
	 * <code>S2</code> onto an object of type <code>D</code>.
	 * @param first The first object to map
	 * @param second The second object to map
	 * @return An object of type <code>D</code>
	 */
	D map(S1 first, S2 second);
}

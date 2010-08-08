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

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An annotation indicating an Iterable or Iterator operation that may
 * never terminate if provided a source sequence that is infinite.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface InfiniteSequenceUnsafe 
{ 
	/**
	 * Describes the chance that an unsafe operation will not terminate if given an infinite sequence.
	 */
	public static enum Likelihood
	{
		/**
		 * Indicates that an operation is guaranteed not to terminate if provided with
		 * an infinite sequence.
		 */
		Always, 
		
		/**
		 * Indicates that an operation may or may not terminate depending on certain conditions
		 * when provided with an infinite sequence.
		 */
		Sometimes
	}
	
	Likelihood value();
}

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

package utilities.iteration.adapters;

import java.util.Iterator;
import java.util.NoSuchElementException;

import utilities.iteration.ImmutableIterator;

/**
 * Presents the characters in a CharSequence as an Iterable.
 */
public class CharacterIterable implements Iterable<Character>
{
	private final CharSequence sequence;
	
	public CharacterIterable(CharSequence sequence)
	{
		this.sequence = sequence;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Character> iterator()
	{
		return new CharacterIterator(sequence);
	}

	/**
	 * An Iterator over the characters in a sequence.
	 */
	private static class CharacterIterator extends ImmutableIterator<Character>
	{
		private final CharSequence sequence;
		private int index;
		
		public CharacterIterator(CharSequence sequence)
		{
			this.sequence = sequence;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return index < sequence.length();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Character next()
		{
			if (index < sequence.length())
			{
				Character next = sequence.charAt(index);
				index++;
				return next;
			}
			
			throw new NoSuchElementException(); 
		}
	}
}

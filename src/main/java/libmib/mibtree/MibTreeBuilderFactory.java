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

package libmib.mibtree;

import filefilters.SmiFilenameFilter;
import libmib.format.MibFormat;

/**
 * Class that creates an appropriate tree builder for
 * a MIB format type.
 */
public class MibTreeBuilderFactory
{
	/**
	 * Returns the MibTreeBuilder corresponding to the given MIB format type.
	 * @param mibFormat
	 * @return a tree builder for the MIB format
	 * @throws TreeBuilderCreationException 
	 */
	public MibTreeBuilder createTreeBuilder(MibFormat mibFormat) throws TreeBuilderCreationException
	{
		switch(mibFormat)
		{
			case SMI:
			{
				MibTreeBuilder builder = new MibTreeBuilderSmi();
				builder.setFileFilter(new SmiFilenameFilter());
				return builder;
			}
		}
		
		return null;
	}
}

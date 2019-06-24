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

package settings;

import utilities.parsing.Parser;

/**
 * A Setting implementation for Integer values
 * that are greater than or equal to 0.
 */
public class PositiveIntegerSetting extends ApplicationSetting<Integer>
{
	/**
	 * Creates a new IntegerSetting.
	 * @param name The Setting's name.
	 * @param parser
	 */
	protected PositiveIntegerSetting(String name, Parser<Integer> parser)
	{
		super(name, parser);
		
		// Initialize the value.
		if (getValue() == null)
			setValue(Integer.valueOf(0));
	}

	@Override
	public void setValue(Integer newValue)
	{
		if (newValue < 0)
			newValue = Integer.valueOf(0);

		if (!newValue.equals(getValue()))
			super.setValue(newValue);
	}

}

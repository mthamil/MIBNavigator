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

package tests;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Contains additional assertions for unit tests.
 */
public final class Assertions
{
	/**
	 * Asserts that a block of code throws a certain Throwable.
	 * @param code The code to test.
	 * @param throwableClass The desired Throwable type.
	 */
	public static void assertThrows(Runnable code, Class<? extends Throwable> throwableClass)
	{
		Throwable e = null;
		try
		{
			code.run();
		}
		catch (Throwable ex)
		{
			e = ex;
		}

		assertThat(e, notNullValue());	// something must have been thrown
		assertTrue(e.getClass().equals(throwableClass));	// the throwable must be the right type
	}
	
	/**
	 * Asserts that a block of code does not throw any Throwable.
	 * @param code The code to test.
	 */
	public static void assertDoesNotThrow(Runnable code)
	{
		Throwable e = null;
		try
		{
			code.run();
		}
		catch (Throwable ex)
		{
			e = ex;
		}

		assertThat(e, nullValue());	// nothing should have been thrown
	}
}

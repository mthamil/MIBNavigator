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

import org.junit.Test;

import static org.junit.Assert.*;
import static assertions.Assertions.assertThrows;
import static assertions.Assertions.assertDoesNotThrow;

public class CustomAssertionTests
{
	@Test
	public void testAssertThrowsDifferentException()
	{
		boolean failed = false;
		try
		{
			// Test exact Throwable type matching.
			assertThrows(new Runnable() { public void run() { throw new IllegalArgumentException(); } }, Exception.class);
		}
		catch (AssertionError e)
		{
			failed = true;
		}
		
		assertTrue(failed);
	}
	
	@Test
	public void testAssertThrowsNoException()
	{
		boolean failed = false;
		try
		{
			// Test exact Throwable type matching.
			assertThrows(new Runnable() { public void run() { } }, Exception.class);
		}
		catch (AssertionError e)
		{
			failed = true;
		}
		
		assertTrue(failed);
	}
	
	@Test
	public void testAssertThrows()
	{
		boolean failed = false;
		try
		{
			// Test exact Throwable type matching.
			assertThrows(new Runnable() { public void run() { throw new IllegalArgumentException(); } }, IllegalArgumentException.class);
		}
		catch (AssertionError e)
		{
			failed = true;
		}
		
		assertFalse(failed);
	}
	
	@Test
	public void testAssertDoesNotThrow()
	{
		boolean failed = false;
		try
		{
			// Test exact Throwable type matching.
			assertDoesNotThrow(new Runnable() { public void run() { } });
		}
		catch (AssertionError e)
		{
			failed = true;
		}
		
		assertFalse(failed);
	}
	
	@Test
	public void testAssertDoesNotThrowFailure()
	{
		boolean failed = false;
		try
		{
			// Test exact Throwable type matching.
			assertDoesNotThrow(new Runnable() { public void run() { throw new IllegalArgumentException(); } });
		}
		catch (AssertionError e)
		{
			failed = true;
		}
		
		assertTrue(failed);
	}
}

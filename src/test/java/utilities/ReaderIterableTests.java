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

package utilities;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import utilities.iteration.adapters.BufferedLinesIterable;
import utilities.iteration.adapters.CharacterIterable;
import utilities.iteration.adapters.ReaderIterable;

public class ReaderIterableTests
{
	@Test
	public void testBufferedIterable()
	{
		String s = "Hey\nthis\nis\na\ntest";
		String[] expectedLines = s.split("\\n");
		Reader in = new StringReader(s);
		
		int i = 0;
		for (String line : new BufferedLinesIterable(in))
		{
			assertThat(line, is(expectedLines[i]));
			
			i++;
		}
		
		assertThat(i, is(expectedLines.length));
	}
	
	@Test
	public void testReaderIterable()
	{
		String s = "Hey_this_is_a_test";
		Reader in = new StringReader(s);
		
		int i = 0;
		for (Character c : new ReaderIterable(in))
		{
			assertThat(c, is(s.charAt(i)));
			
			i++;
		}
		
		assertThat(i, is(s.length()));
	}
	
	@Test
	public void testCharacterIterable()
	{
		String s = "Hey_this_is_a_test";
		
		int i = 0;
		for (Character c : new CharacterIterable(s))
		{
			assertThat(c, is(s.charAt(i)));
			
			i++;
		}
		
		assertThat(i, is(s.length()));
	}
}

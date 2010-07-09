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

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import utilities.iteration.InspectableIterator;
import utilities.iteration.InspectableIteratorAdapter;

public class InspectableIteratorTests
{
	@Test
	public void testInspectableWrapper()
	{
		List<Integer> testValues = Arrays.asList(1, 2, 3, 4, 5, 6);
		
		Iterator<Integer> iterator = testValues.iterator();
		InspectableIterator<Integer> wrapper = new InspectableIteratorAdapter<Integer>(iterator);
		
		assertThat(wrapper.current(), is((Integer)null));
		assertThat(wrapper.hasNext(), is(true));
		
		while (wrapper.hasNext())
		{
			// The result of next() should be the current element.
			Integer next = wrapper.next();
			assertThat(wrapper.current(), is(next));
		}
		
		assertThat(wrapper.current(), is(6));
	}

}

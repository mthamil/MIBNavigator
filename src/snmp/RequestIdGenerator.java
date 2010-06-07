/*
 * SNMP Package
 *
 * Copyright (C) 2010, Matt Hamilton <matthamilton@live.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package snmp;

import utilities.InspectableIterator;

/**
 * Class that creates sequential SNMP Request IDs.
 */
public class RequestIdGenerator implements InspectableIterator<Integer>
{
	private int requestId;
	
	/**
	 * Creates a new Request ID generator starting with 1.
	 */
	public RequestIdGenerator()
	{
		requestId = 0;
	}
	
	/**
	 * Creates a new Request ID generator starting with the given ID.
	 * @param startingId the Request ID to start from
	 */
	public RequestIdGenerator(int startingId)
	{
		if (startingId < 0)
			throw new IllegalArgumentException("startingId must be at least 0.");
		
		requestId = startingId - 1;
	}
	
	/**
	 * Always returns true since a new Request ID can always be generated.
	 */
	public boolean hasNext()
	{
		return true;
	}

	/**
	 * Returns the next Request ID.
	 */
	public Integer next()
	{
		requestId++;
		return requestId;
	}

	/**
	 * Backtracks to the previous Request ID.
	 */
	public void remove()
	{
		requestId--;
	}

	/**
	 * Returns the current Request ID.
	 */
	public Integer current()
	{
		return requestId;
	}

}

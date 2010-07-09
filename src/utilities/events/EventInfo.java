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

package utilities.events;

/**
 * A simple class primarily used as the base class of event
 * objects with more information about an event. However, it 
 * can be used on its own for events that do not have additional 
 * information.
 */
public class EventInfo 
{
	private static EventInfo emptyInfo = new EventInfo();
	
	/**
	 * Represents an event with no event information.
	 */
	public static EventInfo empty()
	{
		return emptyInfo;
	}
}

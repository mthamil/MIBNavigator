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

package utilities.events;

/**
 * Interface for an event that can be subscribed to or unsubscribed from.
 *
 * @param <S> The type of object that owns an event
 * @param <E> The type of event info for the event
 */
public interface SubscribableEvent<S, E extends EventInfo>
{
	/**
	 * Adds an event listener.
	 * @param listener The event subsrciber
	 */
	void addListener(GenericEventListener<S, E> listener);
	
	/**
	 * Removes an event listener if it exists.
	 * @param listener The event subscriber
	 */
	void removeListener(GenericEventListener<S, E> listener);
}

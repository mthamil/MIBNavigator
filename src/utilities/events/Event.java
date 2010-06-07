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

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class that provides the basis for a generic event framework.
 *
 * @param <S> The type of object that owns an event
 * @param <E> The type of event info for the event
 */
public class Event<S, E extends EventInfo> implements SubscribableEvent<S, E>, RaisableEvent<E>
{
	/** A list of all event subscribers */
	private CopyOnWriteArrayList<GenericEventListener<S, E>> listeners;
	
	/** The object that is considered the owner of the event. */
	private S source;
	
	/**
	 * Creates a new event.
	 */
	public Event(S source)
	{
		this.source = source;
	
		// Use a CopyOnWriteArrayList instead of making a copy of the listener list before iteration
		// since iteration will likely occur much more frequently than add or remove operations.
		listeners = new CopyOnWriteArrayList<GenericEventListener<S, E>>();
	}
	
	/**
	 * Convenient factory method for creating new events.
	 * @param <S>
	 * @param <T>
	 * @param source
	 * @return
	 */
	public static <S, T extends EventInfo> Event<S, T> createEvent(S source)
	{
		return new Event<S, T>(source);
	}
	
	/**
	 * Adds an event listener.
	 * @param listener The event subsrciber
	 */
	public void addListener(GenericEventListener<S, E> listener)
	{
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null");
		
		listeners.addIfAbsent(listener);
	}
	
	/**
	 * Removes an event listener if it exists.
	 * @param listener The event subscriber
	 */
	public void removeListener(GenericEventListener<S, E> listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Iterates through all listeners and invokes them with the given arguments.
	 * @param eventInfo The event information.
	 */
	public void raise(E eventInfo)
	{
		if (listeners.isEmpty())
			return;
		
		for (GenericEventListener<S, E> listener : listeners)
		{
			listener.eventRaised(source, eventInfo);
		}
	}
}

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

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import org.junit.Test;

import utilities.events.Event;
import utilities.events.EventInfo;
import utilities.events.EventListener;

public class EventTests
{
	private boolean eventFired = false;
	private boolean eventFired2 = false;
	private int eventFiredCount;
	
	@Test
	public void testEventSubscription()
	{
		Event<Object, EventInfo> event = new Event<Object, EventInfo>(this);
		
		event.addListener(new EventListener<Object, EventInfo>()
		{
			@Override
			public void handleEvent(Object source, EventInfo eventInfo)
			{
				eventFired = true;
			}
		});
		
		event.raise(EventInfo.empty());
		
		assertThat(eventFired, is(true));
	}
	
	@Test
	public void testEventUnsubscription()
	{
		Event<Object, EventInfo> event = new Event<Object, EventInfo>(this);
		
		EventListener<Object, EventInfo> listener = new EventListener<Object, EventInfo>()
		{
			@Override
			public void handleEvent(Object source, EventInfo eventInfo)
			{
				eventFired = true;
			}
		};
		
		event.addListener(listener);
		event.raise(EventInfo.empty());
		assertThat(eventFired, is(true));
		
		eventFired = false;
		event.removeListener(listener); // Unsubscribe
		event.raise(EventInfo.empty());
		assertThat(eventFired, is(false));
	}
	
	@Test
	public void testEventSelfUnsubscription()
	{
		final Event<Object, EventInfo> event = new Event<Object, EventInfo>(this);
		
		EventListener<Object, EventInfo> listener = new EventListener<Object, EventInfo>()
		{
			@Override
			public void handleEvent(Object source, EventInfo eventInfo)
			{
				eventFired = true;
				event.removeListener(this);	// Unsubscribe from within the listener.
			}
		};
		
		event.addListener(listener);
		event.raise(EventInfo.empty());
		assertThat(eventFired, is(true));
		
		eventFired = false;
		event.raise(EventInfo.empty());	// There should be no listeners.
		assertThat(eventFired, is(false));
	} 
	
	@Test
	public void testEventSameSubscription()
	{
		Event<Object, EventInfo> event = new Event<Object, EventInfo>(this);
		
		EventListener<Object, EventInfo> listener = new EventListener<Object, EventInfo>()
		{
			@Override
			public void handleEvent(Object source, EventInfo eventInfo)
			{
				eventFiredCount++;
			}
		};
		
		// Add the same listener twice.
		event.addListener(listener);
		event.addListener(listener);
		
		event.raise(EventInfo.empty());
		assertThat(eventFiredCount, is(1));	// The listener should only have been added once.
	}
	
	@Test
	public void testEventMultipleListeners()
	{
		Event<Object, EventInfo> event = new Event<Object, EventInfo>(this);
		
		// Add 2 different listeners.
		event.addListener(new EventListener<Object, EventInfo>()
		{
			@Override
			public void handleEvent(Object source, EventInfo eventInfo)
			{
				eventFired = true;
			}
		});
		
		
		event.addListener(new EventListener<Object, EventInfo>()
		{
			@Override
			public void handleEvent(Object source, EventInfo eventInfo)
			{
				eventFired2 = true;
			}
		});
		
		event.raise(EventInfo.empty());
		assertThat(eventFired, is(true));
		assertThat(eventFired2, is(true));
	} 
}

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

package utilities.property;

import utilities.events.Event;
import utilities.events.SubscribableEvent;

/**
 *	Property that fires an event when its value changes.
 */
public class BasicProperty<T> implements Property<T>
{
	/** The current value of the property. */
	private T value;
	private static final String PROPERTY_NAME = "value";
	
	private final Event<Property<T>, PropertyChangeEventInfo<T>> propertyChangedEvent = new Event<Property<T>, PropertyChangeEventInfo<T>>(this);
	
	/**
	 * Event raised when a property's value changes.
	 */
	public final SubscribableEvent<Property<T>, PropertyChangeEventInfo<T>> PropertyChangedEvent = propertyChangedEvent;
	
	/* (non-Javadoc)
	 * @see utilities.Property#getValue()
	 */
	public T getValue()
	{
		return value;
	}

	/**
	 * Sets a Property's value.
	 * The value is only updated if the new value differs from the
	 * old value based on the <code>equals()</code> method.
	 * @param newValue The Property's new value
	 */
	public void setValue(T newValue)
	{
		T oldValue = value;
		
		boolean changingFromNull = oldValue == null && newValue != null;
		boolean changingToNull = oldValue != null && newValue == null;
		
		if (changingFromNull || changingToNull || !oldValue.equals(newValue))
		{
			value = newValue;
			propertyChangedEvent.raise(PropertyChangeEventInfo.create(PROPERTY_NAME, oldValue, newValue));
		}
	}
}

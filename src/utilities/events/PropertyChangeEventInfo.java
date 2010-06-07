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
 * An event object that uses a generic type for a property value.
 * 
 * @param <T>
 *            The type of the property that changed.
 */
public class PropertyChangeEventInfo<T> extends EventInfo
{
	private String propertyName;
	private T oldValue;
	private T newValue;

	/**
	 * Constructs a new <code>PropertyChangeEventInfo</code>.
	 * 
	 * @param propertyName
	 *            The name of the property that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 */
	public PropertyChangeEventInfo(String propertyName, T oldValue, T newValue)
	{
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	/**
	 * Factory method that creates a new <code>PropertyChangeEventInfo</code>.
	 * 
	 * @param propertyName
	 *            The name of the property that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 */
	public static <T> PropertyChangeEventInfo<T> createInfo(String propertyName, T oldValue, T newValue)
	{
		return new PropertyChangeEventInfo<T>(propertyName, oldValue, newValue);
	}

	/**
	 * Gets the new value for the property.
	 * 
	 * @return The new value for the property.
	 */
	public T getNewValue()
	{
		return newValue;
	}

	/**
	 * Gets the old value for the property.
	 * 
	 * @return The old value for the property.
	 */
	public T getOldValue()
	{
		return oldValue;
	}
	
	/**
	 * Gets the name of the property that changed.
	 * @return the property name
	 */
	public String getPropertyName()
	{
		return propertyName;
	}
}

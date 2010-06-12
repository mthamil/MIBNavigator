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

package settings;

import utilities.events.Event;
import utilities.events.PropertyChangeEventInfo;
import utilities.events.SubscribableEvent;
import utilities.parsing.Parser;

/**
 * Class that provides encapsulation for application settings.
 * 
 * @param <T> The type of the value that this setting represents.
 */
public abstract class ApplicationSetting<T>
{	
    /** Tracks the ordinal of the next setting to be created. */
    private static int nextOrdinal = 0;
   
    /** Assign an ordinal to the current setting. */
	private final int ordinal = nextOrdinal++;
	
	/** The name of a setting. */
	private final String name;
	
	/** The current value of the setting. */
	private T value;
	
	private final Event<ApplicationSetting<T>, PropertyChangeEventInfo<T>> propertyChangedEvent = Event.create(this);
	
	/**
	 * Event raised when a setting's value changes.
	 */
	public final SubscribableEvent<ApplicationSetting<T>, PropertyChangeEventInfo<T>> PropertyChangedEvent = propertyChangedEvent;
	
	private static final String PROPERTY_NAME = "value";
	
	private final Parser<T> parser;

	/**
	 * Creates a new Setting.
	 * @param name The Setting's name.
	 * @param parser An object that can parse a Setting's value from a string.
	 */
	protected ApplicationSetting(String name, Parser<T> parser)
	{
        this.name = name;
        this.parser = parser;
    }
	
	
	/**
	 * Attempts to set a Setting's value by parsing a string.
	 * @param stringValue
	 */
	protected void parse(String stringValue)
	{
		setValue(parser.parse(stringValue));
	}
	
	/**
	 * Sets a Setting's value.
	 * The value is only updated if the new value differs from the
	 * old value based on the <code>equals()</code> method.
	 * @param newValue The Setting's new value
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
	
	/**
	 * Gets a Setting's current value.
	 * @return the Setting's value
	 */
	public T getValue()
	{
		return value;
	}
	
	/**
	 * Outputs a Setting's name.
	 */
    public String toString()
    {
        return name;
    }
}

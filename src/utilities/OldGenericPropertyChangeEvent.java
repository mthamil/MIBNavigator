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

package utilities;

import java.beans.PropertyChangeEvent;

/**
 * An adapter for PropertyChangeEvent that uses a generic type for the property value.
 *
 * @param <S> The type of the object that fired the event.
 * @param <T> The type of the property that changed.
 */
public class OldGenericPropertyChangeEvent<S, T> extends PropertyChangeEvent
{
	/**
     * Constructs a new <code>GenericPropertyChangeEvent</code>.
     *
     * @param source The object that fired the event.
     * @param propertyName The name of the property that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     */
	public OldGenericPropertyChangeEvent(S source, String propertyName,
			T oldValue, T newValue)
	{
		super(source, propertyName, oldValue, newValue);
	}
	
   /**
    * Gets the new value for the property.
    *
    * @return  The new value for the property.
    */
   @SuppressWarnings("unchecked")
   public T getNewValue() 
   {
	   return (T)super.getNewValue();
   }

   /**
    * Gets the old value for the property.
    *
    * @return  The old value for the property.
    */
   @SuppressWarnings("unchecked")
   public T getOldValue() 
   {
	   return (T)super.getOldValue();
   }
   
   /**
    * The object that fired the event.
    *
    * @return The object that fired the event.
    */
   @SuppressWarnings("unchecked")
   public S getSource()
   {
	   return (S)super.getSource();
   }

}

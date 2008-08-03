/**
 * libmib - Java SNMP Management Information Base Library
 *
 * Copyright (C) 2005, Matt Hamilton <matthew.hamilton@washburn.edu>
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

package libmib.oid;

/**
 * This is a class representing an entry in a list of enumerated values for a 
 * MIB object.  It consists of an integer and a corresponding 'alias' for
 * that number.  Essentially it is a just a name-value pair.
 */
public class MibValueListItem 
{
    private int valueNumber;
    private String valueName;
    
    /**
     * Constructs a default value list item.
     */
    public MibValueListItem()
    {
        valueNumber = 0;
        valueName = "";
    }
    
    /**
     * Constructs a new value list item.
     * 
     * @param newValueName
     * @param newValueNumber
     */
    public MibValueListItem(String newValueName, int newValueNumber)
    {
        valueNumber = newValueNumber;
        valueName = newValueName;
    }
    
    /**
     * Returns this item's integer value.
     * 
     * @return this item's integer value
     */
    public int getValueNumber()
    {
        return valueNumber;
    }
    
    /**
     * Sets the item's integer value.
     * 
     * @param newValue the integer value
     * of this item
     */
    public void setValueNumber(int newValue)
    {
        valueNumber = newValue;
    }
    
    /**
     * Returns this item's name.
     * 
     * @return this item's String name
     */
    public String getValueName()
    {
        return valueName;
    }
    
    /**
     * Sets the name of this item.
     * 
     * @param newName the String name of this
     * value
     */
    public void setValueName(String newName)
    {
        valueName = newName;
    }
    
    
}

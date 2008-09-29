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

package libmib;

/**
 * This is a class representing an entry in a list of enumerated values for a 
 * MIB object.  It consists of an integer value and a corresponding 'alias' for
 * that value.  Essentially it is a just a name-value pair.
 */
public class NameValuePair
{
    private int value;
    private String name;
    
    /**
     * Constructs a default name-value pair.
     */
    public NameValuePair()
    {
        name = "";
        value = 0;
    }
    
    /**
     * Constructs a new name-value pair.
     * 
     * @param newName
     * @param newValue
     */
    public NameValuePair(String newName, int newValue)
    {
        value = newValue;
        name = newName;
    }
    
    /**
     * Returns a pair's value.
     * 
     * @return an integer value
     */
    public int getValue()  { return value; }
    
    /**
     * Sets a pair's value.
     * 
     * @param newValue a new value
     */
    public void setValue(int newValue)  { value = newValue; }
    
    /**
     * Returns a pair's name.
     * 
     * @return a pair's String name
     */
    public String getName() { return name; }
    
    /**
     * Sets the name of this pair.
     * 
     * @param newName a new pair name
     */
    public void setName(String newName) { name = newName; }
    
    
}

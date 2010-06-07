/**
 * libmib - Java SNMP Management Information Base Library
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

package libmib;

import java.util.Map;

/**
 * This class is a logical grouping of attributes that define the syntax of a
 * MIB Object. This includes a data type name and the optional default value and value list.
 */
public class MibSyntax
{
    private String dataType;
    private String defaultValue;
    private Map<Integer, String> pairs;
    
    public MibSyntax()
    {
        dataType = "";
        defaultValue = "";
        pairs = null;
    }
    
    public MibSyntax(String newType)
    {
        dataType = newType;
        defaultValue = "";
        pairs = null; 
    }
    
    
    /**
     * Gets the MIB object data type.
     * 
     * @return the MIB object data type
     */
    public String getDataType()
    {
        return dataType;
    }
    
    /**
     * Sets the data type of the MIB object.
     * 
     * @param newType the data type of the MIB object
     */
    public void setDataType(String newType)
    {
        dataType = newType;
    }
    
    
    /**
     * Gets the default value of this object.
     * 
     * @return a string naming the default value of this object
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }
    
    /**
     * Sets the default value of this object.
     * 
     * @param newDefault a string naming the default value of this object
     */
    public void setDefaultValue(String newDefault)
    {
        defaultValue = newDefault;
    }
    
    
    /**
     * Gets the list of possible values for this MIB object.
     * 
     * @return a list of possible values
     */
    public Map<Integer, String> getValuePairs()
    {
        return pairs;
    }
    
    /**
     * Sets the list of possible values for this MIB object.
     * 
     * @param newPairs name-value pairs
     */
    public void setValuePairs(Map<Integer, String> newPairs)
    {
        if (newPairs == null)
            throw new IllegalArgumentException("Values cannot be set to null.");
        
        pairs = newPairs;
    }
    
    /**
     * Returns whether this MIB object has an initialized 
     * list of name-value pairs.
     * 
     * @return false if no list has been created
     */
    public boolean hasValues()
    {
        if (pairs == null)
            return false;
        
        return true;
    }
    
    
    /**
     * Searches the MibSyntax's enumerated name-value pairs for the 
     * first instance of an integer value and if it is found, 
     * returns the associated name.
     * 
     * @param value the integer value to search for
     * 
     * @return the String name associated with the integer value or an empty String if the 
     *         value wasn't found
     */
    @SuppressWarnings("boxing")
	public String findValueName(int value)
    {
        if (pairs == null)
            throw new NullPointerException("This syntax has no name-value pairs.");
        
        String name = pairs.get(value);
        return (name == null) ? "" : name;
    }
    
    

}

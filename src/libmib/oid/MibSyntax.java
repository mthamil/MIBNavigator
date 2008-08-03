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

import java.util.List;

/**
 * This class is a logical grouping of attributes that define the syntax of a
 * MIB Object. This includes a data type name and the optional default value and value list.
 */
public class MibSyntax 
{
    private String dataType;
    private String defaultValue;
    private List<MibValueListItem> valuesList;
    
    public MibSyntax()
    {
        dataType = "";
        defaultValue = "";
        valuesList = null;
    }
    
    public MibSyntax(String newType)
    {
        dataType = newType;
        defaultValue = "";
        valuesList = null; 
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
    public List<MibValueListItem> getValuesList()
    {
        return valuesList;
    }
    
    /**
     * Sets the list of possible values for this MIB object.
     * 
     * @param newList a list of possible values
     */
    public void setValuesList(List<MibValueListItem> newList)
    {
        if (newList == null)
            throw new IllegalArgumentException("Values list cannot be set to null.");
        
        valuesList = newList;
    }
    
    /**
     * Returns whether this MIB object has an initialized list of specific possible values.
     * 
     * @return false if no list has been created
     */
    public boolean hasValueList()
    {
        if (valuesList == null)
            return false;
        
        //if not null
        return true;

    }
    
    
    /**
     * Searches the MibSyntax's enumerated value list for the first instance of an integer value and 
     * if it is found, returns the associated String name.
     * 
     * @param searchValue the integer value to search the list for
     * 
     * @return the String name associated with the integer value or an empty String if the 
     *         value wasn't found
     */
    public String matchValueName(int searchValue)
    {
        if (valuesList == null)
            throw new NullPointerException("This syntax has no values list.");
        
        //Use a simple O(N) search since value lists shouldn't be very long.
        int i = 0;
        boolean found = false;
        String valueName = "";
        
        while (i < valuesList.size() && !found)
        {
            MibValueListItem curItem = valuesList.get(i);
            if (searchValue == curItem.getValueNumber())
            {
                valueName = curItem.getValueName();
                found = true; //value found, stop searching
            }
            
            i++;
        }
        
        return valueName;
    }
    
    

}

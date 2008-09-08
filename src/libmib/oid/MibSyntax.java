/**
 * libmib - Java SNMP Management Information Base Library
 *
 * Copyright (C) 2008, Matt Hamilton <mhamilton2383@comcast.net>
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
 * This class is a logical grouping of attributes that define the type of a
 * MIB Object. This includes a data type name and the optional default 
 * value and list of allowed name-value pairs.
 */
public class MibSyntax 
{
    private String dataType;
    private String defaultValue;
    private List<MibNameValuePair> values;
    
    public MibSyntax()
    {
        dataType = "";
        defaultValue = "";
        values = null;
    }
    
    public MibSyntax(String newType)
    {
        dataType = newType;
        defaultValue = "";
        values = null; 
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
    public List<MibNameValuePair> getValues()
    {
        return values;
    }
    
    /**
     * Sets the list of possible values for this MIB object.
     * 
     * @param newValues a list of possible values
     */
    public void setValues(List<MibNameValuePair> newValues)
    {
        if (newValues == null)
            throw new IllegalArgumentException("Values list cannot be set to null.");
        
        values = newValues;
    }
    
    /**
     * Returns whether this MIB object has an initialized list of specific possible values.
     * 
     * @return false if no list has been created
     */
    public boolean hasValues()
    {
        if (values == null)
            return false;
        
        // If not null
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
    public String findValueName(int searchValue)
    {
        if (!this.hasValues())
            throw new NullPointerException("This syntax has no values list.");
        
        //Use a simple O(N) search since value lists shouldn't be very long.
        String valueName = "";
        
        for (int i = 0; i < values.size(); i++)
        {
            MibNameValuePair curItem = values.get(i);
            if (searchValue == curItem.getValue())
            {
                valueName = curItem.getName();
                break; // value found, stop searching
            }
        }
        
        return valueName;
    }
    
    

}

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

/**
 *  This class represents a MIB OBJECT IDENTIFIER macro.  This structure 
 *  follows the format below:
 * <pre>
 * <code>
 *   name OBJECT IDENTIFIER ::= { parentName id }
 * </code>
 * </pre>
 * 
 */
public class MibObjectIdentifier 
{
    private int id;
    private String name;
    private String mibName;

    /**
     * Constructs a new, default empty MibObjectIdentifier.
     */
    public MibObjectIdentifier() 
    {
        this.id = 0;
        this.name = "";
        this.mibName = "";
    }
    
    /**
     * Constructs a new MibObjectIdentifier.
     * 
     * @param newName the name of the MIB object identifier
     * @param newId the id of the MIB object identifier. This value is only unique
     * relative to its parent.
     */
    public MibObjectIdentifier(String newName, int newId)
    {
        if (newId < 0)
            throw new IllegalArgumentException("Id value must be positive.");
        
        this.id = newId;
        this.name = newName;
        this.mibName = "";
    }
    
    /**
     * Sets the MIB object identifier's numeric id. This value is only unique
     * relative to its parent.
     * 
     * @param newId the MIB object identifier's numeric id
     */
    public void setId(int newId)
    {
        if (newId < 0)
            throw new IllegalArgumentException("ID value must be positive.");
        
        id = newId;
    }
    
    
    /**
     * Gets the MIB object identifier's numeric id. This value is only unique
     * relative to its parent.
     * 
     * @return the MIB object identifier's numeric id
     */
    public int getId()
    {
        return id;
    }
    
    /**
     * Gets the MIB object identifier's name.
     * 
     * @return the name of the MIB object identifier
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Sets the MIB object identifier's name.
     * 
     * @param newName the name of the MIB object identifier
     */
    public void setName(String newName)
    {
        name = newName;
    }
    
    
    /**
     * Gets the name of the MIB module that defines this MIB object.
     * 
     * @return the name of the defining MIB
     */
    public String getMibName()
    {
        return mibName;
    }
    
    /**
     * Sets the name of the MIB module that defines this MIB object.
     * 
     * @param newMIBName the name of the defining MIB
     */
    public void setMibName(String newMIBName)
    {
        mibName = newMIBName;
    }
    
    
    /* 
     * For rendering in a visual component and searching, only return the name.
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

}

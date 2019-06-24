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

/**
 *  This class represents a MIB MODULE-COMPLIANCE macro.  This structure 
 *  follows the format below:
 * <pre>
 * <code>
 *   name MODULE-COMPLIANCE
 *     STATUS object status
 *     DESCRIPTION
 *              "description"
 *     MANDATORY-GROUPS {        --not currently implemented
 *                         group name1,
 *                         group name2,
 *                         etc.
 *                      }
 *     --One or more module compliance group items of the following format: (not currently implemented)
 *     GROUP module-compliance group name
 *     DESCRIPTION
 *              "group description"
 *     --End of a module compliance group item.
 *   ::= { parentName id }
 * </code>
 * </pre>
 */
public class MibModuleCompliance extends MibObjectIdentifier 
{
    private String status;
    private String description;

    /**
     * Constructs a new, default empty MibModuleCompliance object.
     */
    public MibModuleCompliance() 
    {
        super();
        status = "";
        description = "";
    }

    /**
     * Constructs a new MibModuleCompliance object.
     * 
     * @param newName the name of the MIB module-compliance
     * @param newId the id of the MIB module-compliance. This value is only unique
     * relative to its parent.
     */
    public MibModuleCompliance(String newName, int newId) 
    {
        super(newName, newId);
        status = "";
        description = "";
    }
    
    
    /**
     * Gets the status of the MIB module-compliance.
     * 
     * @return the MIB module-compliance status
     */
    public String getStatus()
    {
        return status;
    }
    
    /**
     * Sets the status of the MIB module-compliance.
     * 
     * @param newStatus the MIB module-compliance status
     */
    public void setStatus(String newStatus)
    {
        status = newStatus;
    }
    
    
    /**
     * Gets the description of the MIB module-compliance.
     * 
     * @return the String description of the MIB module-compliance
     */
    public String getDescription()
    {
        return description;
    }
    
    /**
     * Sets the description of the MIB module-compliance.
     * 
     * @param newDesc the String description of the MIB module-compliance
     */
    public void setDescription(String newDesc)
    {
        description = newDesc;
    }

}

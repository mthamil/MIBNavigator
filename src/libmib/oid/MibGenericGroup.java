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
 * This class is a generic representation of a macro structure several MIB objects use.
 * It consists of a status, description, and a list of object member names.
 * Specific MIB object structures that use this format should extend this class 
 * since it provides most if not all of the functionality they may need.
 */
public class MibGenericGroup extends MibObjectIdentifier 
{
    private String status;
    private String description;
    private List<String> groupMemberList;

    /**
     * Constructs a new, default empty MIB group structure.
     */
    public MibGenericGroup() 
    {
        super();
        status = "";
        description = "";
        groupMemberList = null;
    }

    /**
     * Constructs a new MIB group structure.
     * 
     * @param newName the name of the MIB group structure
     * @param newId the id of the MIB group structure. This value is only unique
     * relative to its parent.
     */
    public MibGenericGroup(String newName, int newId) 
    {
        super(newName, newId);
        status = "";
        description = "";
        groupMemberList = null;
    }
    
    
    /**
     * Gets the status of the MIB group object.
     * 
     * @return the MIB group object status
     */
    public String getStatus()
    {
        return status;
    }
    
    /**
     * Sets the status of the MIB group object.
     * 
     * @param newStatus the MIB group object status
     */
    public void setStatus(String newStatus)
    {
        status = newStatus;
    }
    
    
    /**
     * Gets the description of the MIB group object.
     * 
     * @return the String description of the MIB group object
     */
    public String getDescription()
    {
        return description;
    }
    
    /**
     * Sets the description of the MIB group object.
     * 
     * @param newDesc the String description of the MIB group object
     */
    public void setDescription(String newDesc)
    {
        description = newDesc;
    }
    
    
    /**
     * Gets the list of object names in the MIB group object.
     * 
     * @return a List containing the names of the members of the MIB group object.
     */
    public List<String> getGroupMembers()
    {
        return groupMemberList;
    }
    
    /**
     * Sets the list of object names in the MIB group object.
     * 
     * @param newMemberList a List containing the names of members of the MIB group object.
     */
    public void setGroupMembers(List<String> newMemberList)
    {
        if (newMemberList == null)
            throw new IllegalArgumentException("Group member list cannot be set to null.");
        
        groupMemberList = newMemberList;
    }
    
    /**
     * Adds the name of a MIB object to the group member List.
     * 
     * @param newMemberName the name of a MIB object to add
     * to the group
     */
    public void addGroupMember(String newMemberName)
    {
        groupMemberList.add(newMemberName);
    }

}

/**
 * libmib - Java SNMP Management Information Base Library
 *
 * Copyright (C) 2009, Matt Hamilton <matthamilton@live.com>
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

import java.util.List;

/**
 * This class is an extended version of the MibObjectType class.  It can contain
 * more MIB object attributes found in ASN.1 module definitions.
 */
public class MibObjectExtended extends MibObjectType 
{

    private String objectType;
    private String parent;
    private String lastUpdated;
    private String organization;
    private String contact;
    private List<String> groupMembers;
    private List<MibModuleIdRevision> revisions;
    
    /**
     *  Creates a default MibObjectExtended object.
     */
    public MibObjectExtended()
    {
        super();
        
        objectType = "";
        parent = "";
        lastUpdated = "";
        organization = "";
        contact = "";
        groupMembers = null;
        revisions = null;
    }
    
    /**
     * Creates a MibObjectExtended object with a name and index.
     * 
     * @param newName the String name of the object
     * @param newId the integer id of this object.  This value is only unique
     * relative to its parent.
     */
    public MibObjectExtended(String newName, int newId) 
    {
        super(newName, newId);
        
        objectType = "";
        parent = "";
        lastUpdated = "";
        organization = "";
        contact = "";
        groupMembers = null;
        revisions = null;
    }
    
    /**
     * Returns this MIB object's object type.
     * Note: in the future this may be changed to support
     * Java enumerations.
     * 
     * @return a String containing this MIB object's type
     */
    public String getObjectType()
    {
        return objectType;
    }
    
    /**
     * Sets this MIB object's object type. Possible values
     * are: "OBJECT IDENTIFIER", "OBJECT-TYPE", "OBJECT-GROUP",
     * "MODULE-IDENTITY", "MODULE-COMPLIANCE", and "NOTIFICATION-TYPE".
     * Note: in the future this may be changed to support
     * Java enumerations.
     * 
     * @param newObjType a String containing this MIB object's type
     */
    public void setObjectType(String newObjType)
    {
        objectType = newObjType;
    }
    
    
    
    /**
     * Returns the name of this MIB object's parent object.
     * 
     * @return the String name of this MIB object's parent
     */
    public String getParent()
    {
        return parent;
    }
    
    /**
     * Sets the name of this MIB object's parent object.
     * 
     * @param newParent the name of this MIB object's parent
     */
    public void setParent(String newParent)
    {
        parent = newParent;
    }

    
    
    /**
     * Returns the last time this object's definition was updated.
     * 
     * @return a String containing the last time this MIB object's
     * definition was updated
     */
    public String getLastUpdated()
    {
        return lastUpdated;
    }
    
    /**
     * Sets the last time this object's definition was updated.
     * 
     * @param newUpdated the last time this MIB object's definition
     * was updated
     */
    public void setLastUpdated(String newUpdated)
    {
        lastUpdated = newUpdated;
    }
    
    
    
    /**
     * Returns the organization that created or modified this
     * MIB object's definition.
     * 
     * @return a String naming the organization that created or
     * modified this MIB object's definition
     */
    public String getOrganization()
    {
        return organization;
    }
    
    /**
     * Sets the organization that created or modified this
     * MIB object's definition.
     * 
     * @param newOrg the name of the organization that created or
     * modified this MIB object's definition
     */
    public void setOrganization(String newOrg)
    {
        organization = newOrg;
    }
    
    
    /**
     * Returns information on how to contact this MIB object's
     * organization.
     * 
     * @return a String detailing contact information for
     * this MIB object's organization
     */
    public String getContactInfo()
    {
        return contact;
    }
    
    /**
     * Sets information on how to contact this MIB object's
     * organization.
     * 
     * @param newContact contact information for this
     * MIB object's organization 
     */
    public void setContactInfo(String newContact)
    {
        contact = newContact;
    }
    
    /**
     * Gets the list of object names in an object group.
     * 
     * @return a List containing the names of the members of an object group
     */
    public List<String> getGroupMembers()
    {
        return groupMembers;
    }
    
    /**
     * Sets the list of object names in an object group.
     * 
     * @param newGroup a List containing the names of members of an object group
     */
    public void setGroupMembers(List<String> newGroup)
    {
        if (newGroup == null)
            throw new IllegalArgumentException("Object Group list cannot be set to null.");
        
        groupMembers = newGroup;
    }

    /**
     * Returns whether this MIB object is an object group.
     * 
     * @return false if it is not an object group (does not have an object group List)
     */
    public boolean hasGroupMembers()
    {
        if (groupMembers == null)
            return false;
 
        //if not null
        return true;
    }
    
    
    /**
     * Gets the list of revisions to a module identity object.
     * 
     * @return a List containing revisions to a module identity object
     */
    public List<MibModuleIdRevision> getRevisions()
    {
        return revisions;
    }
    
    /**
     * Sets the list of revisions to a module identity object.
     * 
     * @param newRevisions a List containing revisions to a module identity object
     */
    public void setRevisions(List<MibModuleIdRevision> newRevisions)
    {
        if (newRevisions == null)
            throw new IllegalArgumentException("Revisions list cannot be set to null.");
        
        revisions = newRevisions;
    }

    /**
     * Returns whether this MIB object has revisions.
     * 
     * @return false if there are no revisions (does not have a revision list)
     */
    public boolean hasRevisions()
    {
        if (revisions == null)
            return false;
 
        //if not null
        return true;
    }

}

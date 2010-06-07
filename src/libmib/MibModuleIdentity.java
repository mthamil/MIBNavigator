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

import java.util.List;

/**
 *  This class represents a MIB MODULE-IDENTITY macro.  This structure 
 *  follows the format below:
 * <pre>
 * <code>
 *   name MODULE-IDENTITY
 *     LAST-UPDATED "last update date"
 *     ORGANIZATION "organization name"
 *     CONTACT-INFO
 *               "contact info"
 *     DESCRIPTION
 *              "description"
 *     --Zero or more revisions of the following format:
 *     REVISION "revision date"
 *     DESCRIPTION
 *              "revision description"
 *     --End of a revision.
 *   ::= { parentName id }
 * </code>
 * </pre>
 */
public class MibModuleIdentity extends MibObjectIdentifier 
{ 
    private String lastUpdated;
    private String organization;
    private String contact;
    private String description;
    private List<MibModuleIdRevision> revisions;

    
    /**
     * Constructs a new, default empty MibModuleIdentity object.
     */
    public MibModuleIdentity() 
    {
        super();
        lastUpdated = "";
        organization = "";
        contact = "";
        description = "";
        revisions = null;
    }

    /**
     * Constructs a new MibModuleIdentity object.
     * 
     * @param newName the name of this MIB module identity
     * @param newId the id of this MIB module identity. This value is only unique
     * relative to its parent.
     */
    public MibModuleIdentity(String newName, int newId) 
    {
        super(newName, newId);
        lastUpdated = "";
        organization = "";
        contact = "";
        description = "";
        revisions = null;
    }
    
    
    /**
     * Returns the last time the MIB module identity's definition was updated.
     * 
     * @return a String containing the last time the MIB module identity's
     * definition was updated
     */
    public String getLastUpdated()
    {
        return lastUpdated;
    }
    
    /**
     * Sets the last time the MIB module identity's definition was updated.
     * 
     * @param newUpdated the last time the MIB module identity's definition
     * was updated
     */
    public void setLastUpdated(String newUpdated)
    {
        lastUpdated = newUpdated;
    }
    
    
    /**
     * Returns the organization that created or modified the MIB 
     * module the object identifies.
     * 
     * @return a String naming the organization that created or
     * modified the MIB module the object identifies
     */
    public String getOrganization()
    {
        return organization;
    }
    
    /**
     * Sets the organization that created or modified the MIB module 
     * the object identifies.
     * 
     * @param newOrg the name of the organization that created or
     * modified the MIB module the object identifies
     */
    public void setOrganization(String newOrg)
    {
        organization = newOrg;
    }
    
    
    /**
     * Returns information on how to contact the organization, person, 
     * or people responsible for the MIB module the object identifies.
     * 
     * @return a String detailing contact information for the organization
     * responsible for the MIB module the object identifies.
     */
    public String getContactInfo()
    {
        return contact;
    }
    
    /**
     * Sets information on how to contact the organization, person, 
     * or people responsible for the MIB module the object identifies.
     * 
     * @param newContact a String containing contact information for the 
     * organization responsible for the MIB module the object identifies.
     */
    public void setContactInfo(String newContact)
    {
        contact = newContact;
    }
    
    
    /**
     * Gets the MIB module identity description.
     * 
     * @return the String description of the MIB module identity
     */
    public String getDescription()
    {
        return description;
    }
    
    /**
     * Sets the MIB module identity description.
     * 
     * @param newDesc the String description of the MIB module identity
     */
    public void setDescription(String newDesc)
    {
        description = newDesc;
    }
    
    
    /**
     * Gets the list of revisions to the MIB module identity.
     * 
     * @return a List containing revisions to the MIB module identity
     */
    public List<MibModuleIdRevision> getRevisions()
    {
        return revisions;
    }
    
    /**
     * Sets the list of revisions to the MIB module identity.
     * 
     * @param newRevisions a List containing revisions to the MIB module identity
     */
    public void setRevisions(List<MibModuleIdRevision> newRevisions)
    {
        if (newRevisions == null)
            throw new IllegalArgumentException("Revisions list cannot be set to null.");
        
        revisions = newRevisions;
    }
    

    /**
     * Returns whether the MIB module identity has revisions.  This check is required
     * because not all module-identities contain revisions.
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

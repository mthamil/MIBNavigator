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
 * This class represents a simple MIB OBJECT-TYPE macro.
 * Its structure follows the ASN.1 format below:
 * <pre>
 * <code>
 *   name OBJECT-TYPE
 *     SYNTAX data type { value1(1), value2(2), etc. } --an enumeration is not always included as a part of SYNTAX
 *     ACCESS/MAX-ACCESS access level
 *     STATUS object status
 *     DESCRIPTION
 *              "description"
 *     DEFVAL default value
 *     REFERENCE
 *              "reference information"
 *     INDEX { index1, index2, etc.}
 *   ::= { parentName id }
 * </code>
 * </pre>
 */
public class MibObjectType extends MibObjectIdentifier
{
    private Status status;
    private Access accessLevel;
    private String description;
    private String reference;
    private List<String> indices;
    private MibSyntax syntax;
    
    public enum Access 
    {
    	NOT_ACCESSIBLE, READ_ONLY, READ_WRITE, ACCESSIBLE_FOR_NOTIFY, READ_CREATE;
    
        /**
         * Returns a more properly formatted String version of the constant.
         */
        public String toString()
        {
            return super.toString().toLowerCase().replaceAll("_", "-");
        }
    }
    
    public enum Status 
    {
    	CURRENT, OPTIONAL, DEPRECATED, MANDATORY, OBSOLETE;
    	
        /**
         * Returns a more properly formatted String version of the constant.
         */
        public String toString()
        {
            return super.toString().toLowerCase();
        }
    }
    
    
    /**
     * Constructs a new, empty MibObjectType.
     */
    public MibObjectType()
    {
        super();
        description = "";
        status = null;
        accessLevel = null;
        syntax = null;
        reference = "";
        indices = null;
    }
    
    /**
     * Constructs a new MibObjectType.
     * 
     * @param newName the name of this MIB object
     * @param newId the id of this MIB object. This value is only unique
     * relative to its parent.
     */
    public MibObjectType(String newName, int newId)
    {
        super(newName, newId);
        
    	description = "";
    	status = null;
        accessLevel = null;
        syntax = null;
        reference = "";
        indices = null;
    }
    
    /**
     * Gets the MIB object description.
     * 
     * @return the MIB object description
     */
    public String getDescription()
    {
    	return description;
    }
    
    /**
     * Sets the MIB object description.
     * 
     * @param newDesc the description of this MIB object
     */
    public void setDescription(String newDesc)
    {
    	description = newDesc;
    }
    
    
    /**
     * Gets the MIB object access level.
     * 
     * @return the MIB object access level
     */
    public Access getAccess()
    {
    	return accessLevel;
    }
    
    /**
     * Sets the MIB object access level.
     * 
     * @param newAccess the MIB object access level
     */
    public void setAccess(Access newAccess)
    {
    	accessLevel = newAccess;
    }
    
    /**
     * Gets the MIB object status.
     * 
     * @return the MIB object status
     */
    public Status getStatus()
    {
    	return status;
    }
    
    /**
     * Sets the MIB object status.
     * 
     * @param newStatus the MIB object status
     */
    public void setStatus(Status newStatus)
    {
    	status = newStatus;
    }

    
    /**
     * Gets the syntax definition for this MIB object.
     * The syntax object contains the oid type, default value,
     * and list of possible values.
     * 
     * @return the syntax of this MIB oid
     */
    public MibSyntax getSyntax() 
    {
        return syntax;
    }

    /**
     * Sets the syntax definition for this MIB object.
     * The syntax object contains the oid type, default value,
     * and list of possible values.
     * 
     * @param newSyntax the syntax of this MIB oid
     */
    public void setSyntax(MibSyntax newSyntax) 
    {
        if (newSyntax == null)
            throw new IllegalArgumentException("Syntax cannot be set to null.");
        
        syntax = newSyntax;
    }
    
    /**
     * Returns whether this MIB object's syntax defines a list of specific possible values.
     * This convenience method accesses the MIB object's internal syntax object.
     * 
     * @return false if no list has been created
     */
    public boolean hasNameValuePairs()
    {
        if (syntax == null) //no syntax, no value list
            return false;
            
        return syntax.hasValues();
    }

    
    /**
     * Sets the reference string of this object.
     * 
     * @param newRef the reference string of this object
     */
    public void setReference(String newRef)
    {
        reference = newRef;
    }
    
    /**
     * Gets the reference string of this object.
     * 
     * @return the reference string of this object
     */
    public String getReference()
    {
        return reference;
    }
    
    
    /**
     * Gets the list of index object names for this MIB object.
     * 
     * @return a list of index object names
     */
    public List<String> getIndices()
    {
        return indices;
    }
    
    /**
     * Sets the list of index object names for this MIB object.
     * 
     * @param newIndices a list of index object names
     */
    public void setIndices(List<String> newIndices)
    {
        if (newIndices == null)
            throw new IllegalArgumentException("Index list cannot be set to null.");
        
        indices = newIndices;
    }
    
    /**
     * Returns whether this MIB object has an initialized list of indices.
     * 
     * @return false if no list has been created
     */
    public boolean hasIndices()
    {
        if (indices == null)
            return false;

        // if not null
        return true;

    }
    
}
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

/**
 *  This class represents a MIB NOTIFICATION-TYPE macro.  This structure 
 *  follows the format below:
 * <pre>
 * <code>
 *   name NOTIFICATION-TYPE
 *     OBJECTS {
 *               name1,
 *               name2,
 *               etc.
 *             }  --this group is optional for notification types
 *     STATUS object status
 *     DESCRIPTION
 *              "description"
 *   ::= { parentName id }
 * </code>
 * </pre>
 */
public class MibNotificationType extends MibGroup 
{
    /**
     * Constructs a new, default empty MibNotificationType.
     */
    public MibNotificationType() 
    {
        super();
    }

    /**
     * Constructs a new MibNotificationType.
     * 
     * @param newName the name of the MIB notification-type
     * @param newId the id of the MIB notification-type. This value is only unique
     * relative to its parent.
     */
    public MibNotificationType(String newName, int newId) 
    {
        super(newName, newId);
    }
    
    /**
     * Returns whether the MibNotificationType has an initialized list of member names.
     * This check is required because a group of object names is not always specified.
     * 
     * @return false if the List is null
     */
    public boolean hasGroupMembers()
    {
        if (getGroupMembers() == null)
            return false;
 
        //if not null
        return true;
    }

}

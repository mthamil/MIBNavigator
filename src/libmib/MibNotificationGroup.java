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
 *  This class represents a MIB NOTIFICATION-GROUP macro.  This structure 
 *  follows the format below:
 * <pre>
 * <code>
 *   name NOTIFICATION-GROUP
 *     NOTIFICATIONS {
 *                      name1,
 *                      name2,
 *                      etc.
 *                   }
 *     STATUS object status
 *     DESCRIPTION
 *              "description"
 *   ::= { parentName id }
 * </code>
 * </pre>
 */
public class MibNotificationGroup extends MibGenericGroup 
{
    /**
     * Constructs a new, default empty MibNotificationGroup.
     */
    public MibNotificationGroup() 
    {
        super();
    }

    /**
     * Constructs a new MibNotificationGroup.
     * 
     * @param newName the name of the MIB notification-group
     * @param newId the id of the MIB notification-group. This value is only unique
     * relative to its parent.
     */
    public MibNotificationGroup(String newName, int newId) 
    {
        super(newName, newId);
    }
}

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

package libmib.format;

/**
 * This class simply provides static access to SMI format keyword constants.
 */
public class SmiTokens
{
    // SMI MIB format string constants
	public static final String CAPABILITIES = "AGENT-CAPABILITIES";
    public static final String MODULE_ID = "MODULE-IDENTITY";
    public static final String MODULE_COMP = "MODULE-COMPLIANCE";
    public static final String NOTIF = "NOTIFICATION-TYPE";
    public static final String NOTIF_GRP = "NOTIFICATION-GROUP";
    public static final String OBJECT_ID = "OBJECT IDENTIFIER";
    public static final String OBJECT_TYPE = "OBJECT-TYPE";
    public static final String OBJECT_GRP = "OBJECT-GROUP";
    public static final String TRAP = "TRAP-TYPE";
    
    public static final String SYNTAX = "SYNTAX";
    public static final String ACCESS = "ACCESS";
    public static final String STATUS = "STATUS";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String DEFAULT = "DEFVAL";
    public static final String INDICES = "INDEX";
    public static final String REFERENCE = "REFERENCE";
    
    public static final String MODULE_LAST_UPDATED = "LAST-UPDATED";
    public static final String MODULE_ORGANIZATION = "ORGANIZATION";
    public static final String MODULE_CONTACT = "CONTACT-INFO";
    public static final String MODULE_REVISION = "REVISION";
    public static final String OBJECTS = "OBJECTS";
    public static final String ENT = "ENTERPRISE";
    public static final String NOTIFS = "NOTIFICATIONS";
    
    public static final String MIB_BEGIN = "DEFINITIONS ::= BEGIN";
    public static final String MIB_END = "END";
    public static final String IMPORTS = "IMPORTS";
    public static final String SOURCE = "FROM";
}

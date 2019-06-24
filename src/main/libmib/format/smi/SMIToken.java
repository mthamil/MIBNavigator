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
" */

package libmib.format.smi;

/**
 * This class simply provides static access to SMI format keyword constants.
 */
public enum SMIToken
{
	/** "AGENT-CAPABILITIES" */
	CAPABILITIES("AGENT-CAPABILITIES"),
	
	/** "MODULE-IDENTITY" */
	MODULE_ID("MODULE-IDENTITY"),
	
	/** "MODULE-COMPLIANCE" */
	MODULE_COMP("MODULE-COMPLIANCE"),
	
	/** "NOTIFICATION-TYPE" */
    NOTIF("NOTIFICATION-TYPE"),
    
    /** "NOTIFICATION-GROUP" */
    NOTIF_GROUP("NOTIFICATION-GROUP"),
    
    /** "OBJECT IDENTIFIER" */
    OBJECT_ID("OBJECT IDENTIFIER"),
    
    /** "OBJECT-TYPE" */
    OBJECT_TYPE("OBJECT-TYPE"),
    
    /** "OBJECT-GROUP" */
    OBJECT_GROUP("OBJECT-GROUP"),
    
    /** "TRAP-TYPE" */
    TRAP("TRAP-TYPE"),
   
    
    /** "SYNTAX" */
    SYNTAX("SYNTAX"),
    
    /** "ACCESS" */
    ACCESS("ACCESS"),
    
    /** "STATUS" */
    STATUS("STATUS"),
    
    /** "DESCRIPTION" */
    DESCRIPTION("DESCRIPTION"),
    
    /** "DEFVAL" */
    DEFAULT("DEFVAL"),
    
    /** "INDEX" */
    INDICES("INDEX"),
    
    /** "REFERENCE" */
    REFERENCE("REFERENCE"),

    
    /** "LAST-UPDATED" */
    MODULE_LAST_UPDATED("LAST-UPDATED"),
    
    /** "ORGANIZATION" */
    MODULE_ORGANIZATION("ORGANIZATION"),
    
    /** "CONTACT-INFO" */
    MODULE_CONTACT("CONTACT-INFO"),
    
    /** "REVISION" */
    MODULE_REVISION("REVISION"),
    
    /** "OBJECTS" */
    OBJECTS("OBJECTS"),
    
    /** "ENTERPRISE" */
    ENT("ENTERPRISE"),
    
    /** "NOTIFICATIONS" */
    NOTIFS("NOTIFICATIONS"),
   
    
    /** "DEFINITIONS ::= BEGIN" */
    MIB_BEGIN("DEFINITIONS ::= BEGIN"),
    
    /** "END" */
    MIB_END("END"),
    
    /** "MACRO ::=" */
    MACRO("MACRO ::="),
    
    /** "IMPORTS" */
    IMPORTS("IMPORTS"),
    
    /** "FROM" */
    SOURCE("FROM"),
	
    /** "--" */
	COMMENT("--");
	
	private String tokenValue;
	
	private SMIToken(String token)
	{
		this.tokenValue = token;
	}
	
	/**
	 * Gets the token's text value.
	 * @return
	 */
	public String token()
	{
		return tokenValue;
	}
}

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

package libmib.format;

/**
 * Contains constant string tokens for use in XML MIB elements.
 */
public class ElementNames
{
	public static String NAME_ATTR = "name";
	public static String ROOT = "mib";
	public static String IMPORTS = "imports";
		public static String SOURCE = "source";
			public static String IMPORT = "import";
	
	public static String DATATYPES = "dataTypes";
		public static String DATATYPE = "dataType";
			public static String DATA_NAME = "dataName";
    	    	public static String MIN_SIZE_ATTR = "minSize";
    	    	public static String MAX_SIZE_ATTR = "maxSize";
				public static String BASE_TYPE ="base";
	
	public static String OBJECTS = "objects";
		public static String OBJECT = "object";
			public static String OBJECT_ID = "objectIdentifier";
				public static String NAME = "objectName";
				public static String ID = "objectId";
				public static String PARENT = "parent";
				
			public static String OBJECT_TYPE = "objectType";
    			public static String SYNTAX = "syntax";
    				public static String TYPE = "type";
    				public static String SEQUENCE = "sequence";
    				public static String PAIRS = "nameValuePairs";
    					public static String PAIR = "nameValuePair";
    					public static String PAIR_NAME = "pairName";
    					public static String PAIR_VALUE = "pairValue";
    				public static String DEFAULT = "default";
    				
    			public static String ACCESS = "access";
    			public static String STATUS = "status";
    			public static String DESCRIPTION = "description";
    			public static String REF = "reference";
    			public static String INDICES = "indices";
    				public static String INDEX = "index";
			
			public static String OBJECT_GROUP = "objectGroup";
				public static String MEMBERS = "members";
					public static String MEMBER = "member";

			public static String MODULE_ID = "moduleIdentity";
				public static String UPDATED = "lastUpdated";
				public static String ORG = "organization";
				public static String CONTACT = "contactInfo";
				public static String REVISION = "revision";
					public static String REV_ID_ATTR = "revisionId";
			
			public static String MODULE_COMPLIANCE = "moduleCompliance";
			public static String NOTIFICATION = "notification";
			public static String NOTIFICATION_GROUP = "notificationGroup";
}

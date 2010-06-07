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

package libmib.format.xml;

/**
 * Contains constant string tokens for use in XML MIB elements.
 */
public class ElementNames
{
	public static final String NAME_ATTR = "name";
	public static final String ROOT = "mib";
	public static final String IMPORTS = "imports";
		public static final String SOURCE = "source";
			public static final String IMPORT = "import";
	
	public static final String DATATYPES = "dataTypes";
		public static final String DATATYPE = "dataType";
			public static final String DATA_NAME = "dataName";
    	    	public static final String MIN_SIZE_ATTR = "minSize";
    	    	public static final String MAX_SIZE_ATTR = "maxSize";
				public static final String BASE_TYPE ="base";
	
	public static final String OBJECTS = "objects";
		public static final String OBJECT = "object";
			public static final String OBJECT_ID = "objectIdentifier";
				public static final String NAME = "objectName";
				public static final String ID = "objectId";
				public static final String PARENT = "parent";
				
			public static final String OBJECT_TYPE = "objectType";
    			public static final String SYNTAX = "syntax";
    				public static final String TYPE = "type";
    				public static final String SEQUENCE = "sequence";
    				public static final String PAIRS = "nameValuePairs";
    					public static final String PAIR = "nameValuePair";
    					public static final String PAIR_NAME = "pairName";
    					public static final String PAIR_VALUE = "pairValue";
    				public static final String DEFAULT = "default";
    				
    			public static final String ACCESS = "access";
    			public static final String STATUS = "status";
    			public static final String DESCRIPTION = "description";
    			public static final String REF = "reference";
    			public static final String INDICES = "indices";
    				public static final String INDEX = "index";
			
			public static final String OBJECT_GROUP = "objectGroup";
				public static final String MEMBERS = "members";
					public static final String MEMBER = "member";

			public static final String MODULE_ID = "moduleIdentity";
				public static final String UPDATED = "lastUpdated";
				public static final String ORG = "organization";
				public static final String CONTACT = "contactInfo";
				public static final String REVISION = "revision";
					public static final String REV_ID_ATTR = "revisionId";
			
			public static final String MODULE_COMPLIANCE = "moduleCompliance";
			public static final String NOTIFICATION = "notification";
			public static final String NOTIFICATION_GROUP = "notificationGroup";
}

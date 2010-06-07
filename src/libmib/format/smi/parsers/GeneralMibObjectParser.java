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

package libmib.format.smi.parsers;

import static libmib.format.smi.SMIToken.ACCESS;
import static libmib.format.smi.SMIToken.COMMENT;
import static libmib.format.smi.SMIToken.DEFAULT;
import static libmib.format.smi.SMIToken.DESCRIPTION;
import static libmib.format.smi.SMIToken.INDICES;
import static libmib.format.smi.SMIToken.MODULE_COMP;
import static libmib.format.smi.SMIToken.MODULE_CONTACT;
import static libmib.format.smi.SMIToken.MODULE_ID;
import static libmib.format.smi.SMIToken.MODULE_LAST_UPDATED;
import static libmib.format.smi.SMIToken.MODULE_ORGANIZATION;
import static libmib.format.smi.SMIToken.MODULE_REVISION;
import static libmib.format.smi.SMIToken.NOTIF;
import static libmib.format.smi.SMIToken.NOTIFS;
import static libmib.format.smi.SMIToken.NOTIF_GROUP;
import static libmib.format.smi.SMIToken.OBJECTS;
import static libmib.format.smi.SMIToken.OBJECT_GROUP;
import static libmib.format.smi.SMIToken.OBJECT_TYPE;
import static libmib.format.smi.SMIToken.REFERENCE;
import static libmib.format.smi.SMIToken.SOURCE;
import static libmib.format.smi.SMIToken.STATUS;
import static libmib.format.smi.SMIToken.SYNTAX;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import libmib.MibModuleIdRevision;
import libmib.MibObjectExtended;
import libmib.MibSyntax;
import libmib.MibObjectType.Access;
import libmib.MibObjectType.Status;
import libmib.format.smi.SMIStructureHandler;
import libmib.format.smi.SMIToken;
import libmib.format.smi.SMIStructureHandler.HierarchyData;

/**
 * Parses MIB structures into a single object type containing a superset of all
 * known MIB Object properties.
 */
public class GeneralMibObjectParser extends AbstractParser<MibObjectExtended>
{
	@Override
	public MibObjectExtended parse(BufferedReader reader, String currentLine) throws IOException
	{
		// test for other object "types"
	    SMIToken objectType = null;
	    if (currentLine.contains(OBJECT_TYPE.token()))
	        objectType = OBJECT_TYPE;
	    else if (currentLine.contains(OBJECT_GROUP.token()))
	        objectType = OBJECT_GROUP;
	    else if (currentLine.contains(NOTIF.token()))
	        objectType = NOTIF;
	    else if (currentLine.contains(MODULE_COMP.token()))
	        objectType = MODULE_COMP;
	    else if (currentLine.contains(MODULE_ID.token()))
	        objectType = MODULE_ID;
        else if (currentLine.contains(NOTIF_GROUP.token()))
            objectType = NOTIF_GROUP;
	    						
		if ( objectType != null && !currentLine.trim().equalsIgnoreCase(objectType.token()) 
		        && !currentLine.contains(SOURCE.token()) && !currentLine.contains(",") )
		{ 
			// Get the node name.
	        String name = "";
	        int index = currentLine.indexOf(objectType.token());
	        name = currentLine.substring(0, index).trim();

	        // Initialize properties.
	        String defaultValue = "";
	        Access access = null;
	        Status status = null;
	        String ref = "";
	        StringBuilder description = new StringBuilder();
	        
	        String lastUpdated = "";
	        String organization = "";
	        StringBuilder contact = new StringBuilder();
	        
	        MibSyntax nodeSyntax = null;
	        List<String> indices = null;
	        List<String> group = null;  
	        List<MibModuleIdRevision> revisions = null;
	        
	        // read until the end of the object definition, retrieving relevant information
	        currentLine = reader.readLine();
	        while (currentLine != null && !currentLine.trim().startsWith("::="))
	        {
	        	currentLine = currentLine.trim();
	        	
	            // strip comments
	            if (currentLine.contains(COMMENT.token()))
	            {
	                int commentIndex = currentLine.indexOf(COMMENT.token());
	                currentLine = currentLine.substring(0, commentIndex).trim();
	            }
	            
	            if (!currentLine.equals(""))
	            {
	                // SYNTAX
	                if (currentLine.contains(SYNTAX.token()) && !objectType.equals(MODULE_COMP))
	                	nodeSyntax = SMIParserFactory.<AbstractParser<MibSyntax>>getParser(SYNTAX).parse(reader, currentLine);
	                
	                // ACCESS
	                else if (currentLine.contains(ACCESS.token()) && !objectType.equals(MODULE_COMP))   
	                	access = SMIParserFactory.<AbstractParser<Access>>getParser(ACCESS).parse(reader, currentLine);
	                
	                // STATUS
	                else if (currentLine.contains(STATUS.token()))
	                	status = SMIParserFactory.<AbstractParser<Status>>getParser(STATUS).parse(reader, currentLine);
	                
	                // LAST-UPDATED
	                else if (currentLine.contains(MODULE_LAST_UPDATED.token()))
	                    lastUpdated = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")).trim();
	                
	                
	                // ORGANIZATION
	                else if (currentLine.contains(MODULE_ORGANIZATION.token()))
	                    organization = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")).trim();
	                
	                
	                // DEFAULT VALUE (DEFVAL)
	                else if (currentLine.contains(DEFAULT.token()))
	                {
	                    if (currentLine.contains("{") && currentLine.contains("}"))
	                        defaultValue = currentLine.substring(currentLine.indexOf("{") + 1, currentLine.indexOf("}")).trim();
	                }
	                
	                
	                // OBJECT-GROUP members or NOTIFICATION-TYPE member
	                else if ( (currentLine.contains(OBJECTS.token()) && objectType.equals(OBJECT_GROUP)) || 
	                         (currentLine.contains(NOTIFS.token()) && objectType.equals(NOTIF_GROUP)) )
	                {
	                    if (currentLine.contains("{"))
	                        group = SMIStructureHandler.readList(reader, currentLine, OBJECTS);
	                }

	                                    
	                // CONTACT-INFO
	                else if (currentLine.contains(MODULE_CONTACT.token()))
	                    contact.append(SMIStructureHandler.readQuotedSection(reader, currentLine, MODULE_CONTACT));

	                
	                // DESCRIPTION
	                else if (currentLine.contains(DESCRIPTION.token()))
	                    description.append(SMIParserFactory.<AbstractParser<String>>getParser(DESCRIPTION).parse(reader, currentLine));

	                
	                // REFERENCE
	                else if (currentLine.contains(REFERENCE.token()))
	                    ref = SMIStructureHandler.readQuotedSection(reader, currentLine, REFERENCE);

	                
	                // INDICES
	                else if (currentLine.contains(INDICES.token()))
	                {
	                    if (currentLine.contains("{"))
	                        indices = SMIStructureHandler.readList(reader, currentLine, INDICES);
	                }

	                
	                // REVISION
	                else if (currentLine.contains(MODULE_REVISION.token()))
	                {                                      
	                    MibModuleIdRevision revision = SMIParserFactory.<AbstractParser<MibModuleIdRevision>>getParser(MODULE_REVISION).parse(reader, currentLine);
	                    if (revision != null)
	                    {
	                        if (revisions == null)
	                            revisions = new ArrayList<MibModuleIdRevision>();
	                        
	                    	revisions.add(revision);
	                    }
	                }
	                
	                currentLine = reader.readLine();
	            }
	            else
	                currentLine = reader.readLine();

	        }

	        if (currentLine != null)
	        {
		        // the currentLine with ::= should be processed here
	        	HierarchyData objectInfo = SMIStructureHandler.parseHierarchyData(currentLine);
	        	//System.out.println(objectInfo.getParent() + " " + objectInfo.getIndex());;
		
		        // set basic properties
		        MibObjectExtended mibObject = new MibObjectExtended(name, objectInfo.getIndex());
		        
		        mibObject.setDescription(description.toString());
		        
		        if (access != null)
		            mibObject.setAccess(access);
		
		        if (status != null)
		            mibObject.setStatus(status);
		        
		        mibObject.setReference(ref);
		        
		        if (nodeSyntax != null) 
	            {
		        	nodeSyntax.setDefaultValue(defaultValue);               
	                mibObject.setSyntax(nodeSyntax);
	            }
		        
		        if (indices != null)
		            mibObject.setIndices(indices);
		        
		        // set extended properties
		        mibObject.setObjectType(objectType.token());
		        mibObject.setParent(objectInfo.getParent());
		        mibObject.setLastUpdated(lastUpdated);
		        mibObject.setOrganization(organization);
		        mibObject.setContactInfo(contact.toString());
		        
		        if (group != null)
		            mibObject.setGroupMembers(group);
		        
		        if (revisions != null)
		            mibObject.setRevisions(revisions);
		        
		        return mibObject;
	        }
		}
		
		return null;
	}
}

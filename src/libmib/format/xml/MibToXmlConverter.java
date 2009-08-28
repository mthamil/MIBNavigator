/**
 * MIB To XML Converter
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
 */

package libmib.format.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utilities.IOUtilities;

import static libmib.format.smi.SMIToken.*;
import libmib.MibImport;
import libmib.MibModuleIdRevision;
import libmib.MibObjectExtended;
import libmib.MibSyntax;
import libmib.MibObjectType.Access;
import libmib.MibObjectType.Status;
import libmib.format.smi.InvalidSmiMibFormatException;
import libmib.format.smi.SMIParsers;
import libmib.format.smi.SMIStructureHandler;
import libmib.format.smi.SMIToken;
import libmib.format.smi.SMIStructureHandler.HierarchyData;

/**
 * Class for converting ASN.1 MIB definition files to an XML file format.
 * This format in no way tries to replace ASN.1 in its entirety, it simply 
 * provides an easier format to work with MIB files in applications since 
 * XML libraries are in no short supply these days.
 * <br><br>
 * NOTE:  Apparently there are already projects for converting ASN.1 to
 * XML and vice versa.  ASN.1 is supposed to be easily parsable and can be 
 * verified against in a way similar to XML schemas.  There are also ASN.1 
 * parsers, but none that I have found in Java.  I admit that my experience 
 * in parsing is VERY limited, but I simply don't see how SMI, which uses 
 * no standard tokens to identify certain elements, can be more easily 
 * parseable than XML.
 * <br><br>
 * However, the format I am using is more specific to SNMP SMI module 
 * definitions, and I have checked out another similar project and have 
 * seen that their XML MIBS are quite similar in structure to my own, so I
 * conclude that I am on the right track in that regard.  Since Java has many
 * standard methods for parsing and using XML, I will stick with it.
 * <br><br>
 * Currently, the converter implements all OID attributes except for Module-Compliance
 * compliance sections.  Imports are implemented, but data types are not.
 */
public class MibToXmlConverter 
{ 
    //private ArrayList<String> introComments = null;
    private MibDocumentBuilder mibDocFactory;

    public MibToXmlConverter()
    {
        //introComments = new ArrayList<String>();    
        mibDocFactory = new MibDocumentBuilder();
    }
    
    @SuppressWarnings("unchecked")
	public void readMIB(File inputMIBFile) throws InvalidSmiMibFormatException
    {   
    	BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(inputMIBFile));
			String line;

			// If the file contained in the BufferedReader does not contain the correct MIB 'header', 
            // throw an exception. (it's game over, man)
			String mibName = SMIStructureHandler.readMibName(reader);
            if (mibName.equals(""))
                throw new InvalidSmiMibFormatException(inputMIBFile);
            
            mibDocFactory.addDefinitionAttribute(mibName);

            System.out.println(mibName + ": Valid MIB");
            
			while ( ((line = reader.readLine()) != null) && !line.trim().equals(MIB_END.token()))
			{
			    // Strip comments.
                if (line.contains(COMMENT.token()))
                {
                    int commentIndex = line.indexOf(COMMENT.token());
                    line = line.substring(0, commentIndex).trim();
                }

				// Ignore empty lines.
				if (line.trim().equals(""))
				    continue;		
                    
                // Read the 'IMPORTS' section.
                if (line.contains(IMPORTS.token()))
                {
                    List<MibImport> imports = (List<MibImport>)SMIParsers.getParser(IMPORTS).parse(reader, line);
                    for (MibImport importItem : imports)
                    	mibDocFactory.addImportElement(importItem);
                }
                
                
                // Skip 'MACRO' definitions.
                if (line.contains(MACRO.token()))
                {
                    while ( (line = reader.readLine()) != null )
                    {
                        if (line.trim().equals(MIB_END.token()))
                            break;
                    }
                    continue;
                }
                
                
                // READ MIB OBJECTS
                if ( line.contains(OBJECT_ID.token()) && !line.contains(SYNTAX.token()) && !line.contains(",") 
                        && !line.contains(SOURCE.token()) && !line.trim().equals(OBJECT_ID.token()))
                {
                    
                    StringBuilder oidDef = new StringBuilder();
                    oidDef.append(line);

                    // check whether the entire definition is on this line
                    if (oidDef.indexOf("}") < 0) 
                    {
                        char c = ' ';  
                        do
                        {
                            c = (char)reader.read();
                            oidDef.append(c);
                        } while (c != '}' && c != -1);
                    }
                    String oidString = oidDef.toString().replaceAll("\\s+", " ").trim(); //simplify whitespace

                    if (oidString.contains("::=") && oidString.contains("{"))
                    {
                        // get the mib object name
                        String objName = "";
                        int index = oidString.indexOf(OBJECT_ID.token());
                        objName = oidString.substring(0, index).trim();
                        //System.out.println(objName);
                        
                        String objInfo = oidString.substring(index + OBJECT_ID.token().length());
                        HierarchyData objData = SMIStructureHandler.parseHierarchyData(objInfo);                      

                        if ( !objName.equals("") && !objData.getParent().equals("") && objData.getIndex() != -1 )
                        {
							//System.out.println(objData.getParent() + " " + objData.getIndex());
							MibObjectExtended mibObject = new MibObjectExtended(objName, objData.getIndex());
							mibObject.setObjectType(OBJECT_ID.token());
							mibObject.setParent(objData.getParent());

						    mibDocFactory.addObjectElement(mibObject);
                        }
                    }
				}
                
				// test for other object "types"
			    SMIToken objectType = null;
			    if (line.contains(OBJECT_TYPE.token()))
			        objectType = OBJECT_TYPE;
			    else if (line.contains(OBJECT_GROUP.token()))
			        objectType = OBJECT_GROUP;
			    else if (line.contains(NOTIF.token()))
			        objectType = NOTIF;
			    else if (line.contains(MODULE_COMP.token()))
			        objectType = MODULE_COMP;
			    else if (line.contains(MODULE_ID.token()))
			        objectType = MODULE_ID;
                else if (line.contains(NOTIF_GROUP.token()))
                    objectType = NOTIF_GROUP;
			    						
				// read other object types
				if ( objectType != null && !line.trim().equalsIgnoreCase(objectType.token()) 
				        && !line.contains(SOURCE.token()) && !line.contains(",") )
				{ 
                    MibObjectExtended mibObject = readObject(reader, line, objectType);
                    
                    // add this object to the list of MIB objects
                    mibDocFactory.addObjectElement(mibObject);
				}
			}

		}
		catch (IOException e)
		{
			System.out.println(e.getMessage() + " " + e.getCause());
            e.printStackTrace();
		}
		finally
		{
			IOUtilities.closeQuietly(reader);
		}
        
    }

    
    private MibObjectExtended readObject(final BufferedReader reader, String line, SMIToken objectType) throws IOException
    { 
        // Get the node name.
        String name = "";
        int index = line.indexOf(objectType.token());
        name = line.substring(0, index).trim();

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
        line = reader.readLine();
        while (line != null && !line.trim().startsWith("::="))
        {
        	line = line.trim();
        	
            // strip comments
            if (line.contains(COMMENT.token()))
            {
                int commentIndex = line.indexOf(COMMENT.token());
                line = line.substring(0, commentIndex).trim();
            }
            
            if (!line.equals(""))
            {
                // SYNTAX
                if (line.contains(SYNTAX.token()) && !objectType.equals(MODULE_COMP))
                {
                	nodeSyntax = (MibSyntax)SMIParsers.getParser(SYNTAX).parse(reader, line);
                }
                
                // ACCESS
                else if (line.contains(ACCESS.token()) && !objectType.equals(MODULE_COMP))
                {      
                	access = (Access)SMIParsers.getParser(ACCESS).parse(reader, line);
                }
                
                // STATUS
                else if (line.contains(STATUS.token()))
                {
                	status = (Status)SMIParsers.getParser(STATUS).parse(reader, line);
                }
                
                // LAST-UPDATED
                else if (line.contains(MODULE_LAST_UPDATED.token()))
                    lastUpdated = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).trim();
                
                
                // ORGANIZATION
                else if (line.contains(MODULE_ORGANIZATION.token()))
                    organization = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).trim();
                
                
                // DEFAULT VALUE (DEFVAL)
                else if (line.contains(DEFAULT.token()))
                {
                    if (line.contains("{") && line.contains("}"))
                        defaultValue = line.substring(line.indexOf("{") + 1, line.indexOf("}")).trim();
                }
                
                
                // OBJECT-GROUP members or NOTIFICATION-TYPE member
                else if ( (line.contains(OBJECTS.token()) && objectType.equals(OBJECT_GROUP)) || 
                         (line.contains(NOTIFS.token()) && objectType.equals(NOTIF_GROUP)) )
                {
                    if (line.contains("{"))
                        group = SMIStructureHandler.readList(reader, line, OBJECTS);
                }

                                    
                // CONTACT-INFO
                else if (line.contains(MODULE_CONTACT.token()))
                    contact.append(SMIStructureHandler.readQuotedSection(reader, line, MODULE_CONTACT));

                
                // DESCRIPTION
                else if (line.contains(DESCRIPTION.token()))
                    description.append((String)SMIParsers.getParser(DESCRIPTION).parse(reader, line));

                
                // REFERENCE
                else if (line.contains(REFERENCE.token()))
                    ref = SMIStructureHandler.readQuotedSection(reader, line, REFERENCE);

                
                // INDICES
                else if (line.contains(INDICES.token()))
                {
                    if (line.contains("{"))
                        indices = SMIStructureHandler.readList(reader, line, INDICES);
                }

                
                // REVISION
                else if (line.contains(MODULE_REVISION.token()))
                {                                      
                    MibModuleIdRevision revision = (MibModuleIdRevision)SMIParsers.getParser(MODULE_REVISION).parse(reader, line);
                    if (revision != null)
                    {
                        if (revisions == null)
                            revisions = new ArrayList<MibModuleIdRevision>();
                        
                    	revisions.add(revision);
                    }
                }
                
                line = reader.readLine();
            }
            else
                line = reader.readLine();

        }

        if (line != null)
        {
	        // the line with ::= should be processed here
        	HierarchyData objectInfo = SMIStructureHandler.parseHierarchyData(line);
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

    	return null;
    }
    
    
    /*private String escapeString(String unescaped)
    {
        String escaped = unescaped;
        escaped = escaped.replaceAll("&", "&amp;");
        escaped = escaped.replaceAll(">", "&gt;");
        escaped = escaped.replaceAll("<", "&lt;");
        
        return escaped;
    }*/
    
    
    
    /**
     * Writes the constructed document to an XML file.
     */
    public void writeXML(File outputXMLFile)
    {    
        mibDocFactory.writeDocument(outputXMLFile);
    }
}

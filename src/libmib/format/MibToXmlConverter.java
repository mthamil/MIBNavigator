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

package libmib.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utilities.Utilities;

import libmib.MibImport;
import libmib.MibModuleIdRevision;
import libmib.MibObjectExtended;
import libmib.MibSyntax;
import libmib.MibObjectType.Access;
import libmib.MibObjectType.Status;

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
    private SmiStructureHandler handler;
    //private ArrayList<String> introComments = null;
    private MibDocumentBuilder mibDocFactory;

    public MibToXmlConverter()
    {
        //introComments = new ArrayList<String>();    
        mibDocFactory = new MibDocumentBuilder();
    }
    
    public void readMIB(File inputMIBFile) throws InvalidSmiMibFormatException
    {   
    	BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(inputMIBFile));
            handler = new SmiStructureHandler(in);
			String line;

			// If the file contained in the BufferedReader does not contain the correct MIB 'header', 
            // throw an exception. (it's game over, man)
			String mibName = handler.readMibName();
            if (mibName.equals(""))
                throw new InvalidSmiMibFormatException(inputMIBFile);
            
            mibDocFactory.addDefinitionAttribute(mibName);

            System.out.println(mibName + ": Valid MIB");
            
			while ( ((line = in.readLine()) != null) && !line.trim().equals(SmiTokens.MIB_END))
			{
			    // Strip comments.
                if (line.contains("--"))
                {
                    int commentIndex = line.indexOf("--");
                    line = line.substring(0, commentIndex).trim();
                }

				// Ignore empty lines.
				if (line.trim().equals(""))
				    continue;		
                    
                // Read the 'IMPORTS' section.
                if (line.contains(SmiTokens.IMPORTS))               
                    readImports(in);     
                
                
                // Skip 'MACRO' definitions.
                if (line.contains("MACRO ::="))
                {
                    while ( (line = in.readLine()) != null )
                    {
                        if (line.trim().equals(SmiTokens.MIB_END))
                            break;
                    }
                    continue;
                }
                
                
                // READ MIB OBJECTS
                if ( line.contains(SmiTokens.OBJECT_ID) && !line.contains(SmiTokens.SYNTAX) && !line.contains(",") 
                        && !line.contains(SmiTokens.SOURCE) && !line.trim().equals(SmiTokens.OBJECT_ID))
                {
                    
                    StringBuilder oidDef = new StringBuilder();
                    oidDef.append(line);

                    // check whether the entire definition is on this line
                    if (oidDef.indexOf("}") < 0) 
                    {
                        char c = ' ';  
                        do
                        {
                            c = (char)in.read();
                            oidDef.append(c);
                        } while (c != '}' && c != -1);
                    }
                    String oidString = oidDef.toString().replaceAll("\\s+", " ").trim(); //simplify whitespace

                    if (oidString.contains("::=") && oidString.contains("{"))
                    {
                        // get the mib object name
                        String objName = "";
                        int index = oidString.indexOf(SmiTokens.OBJECT_ID);
                        objName = oidString.substring(0, index).trim();
                        //System.out.println(objName);
                        
                        // initializations
                        String objInfo = "";
                        String objParent = "";
                        String objIndex = "";;  
                        
					    objInfo = oidString.substring(index + SmiTokens.OBJECT_ID.length());
                        objInfo = objInfo.substring(objInfo.indexOf("{") + 1, objInfo.indexOf("}")).trim();

                        String parents = "";
                        if (objInfo.contains("(") && objInfo.contains(")"))
                        {   // these have the form :={test(1) test2(6) test3(2) 1}
                            
                            parents = objInfo.substring(objInfo.indexOf(" "), objInfo.lastIndexOf(")"));
                            parents = parents.substring(parents.lastIndexOf(" "), parents.lastIndexOf("("));
                            objParent = parents.trim();
                        }
                        else
                        {   // these have the regular form := {test 1}
                            index = objInfo.indexOf(" ");
                            objParent = objInfo.substring(0, index);
                        }
                        
                        int index2 = objInfo.lastIndexOf(" ") + 1;
                        objIndex = objInfo.substring(index2, objInfo.length());

                        if ( !objName.equals("") && !objParent.equals("") && !objIndex.equals("") )
                        {
							//System.out.println(objParent + " " + objIndex);
							MibObjectExtended mibObject = new MibObjectExtended(objName, Integer.parseInt(objIndex));
							mibObject.setObjectType(SmiTokens.OBJECT_ID);
							mibObject.setParent(objParent);

						    mibDocFactory.addObjectElement(mibObject);
                        }
                    }
				}
                
				// test for other object "types"
			    String objectType = "";
			    if (line.contains(SmiTokens.OBJECT_TYPE))
			        objectType = SmiTokens.OBJECT_TYPE;
			    else if (line.contains(SmiTokens.OBJECT_GRP))
			        objectType = SmiTokens.OBJECT_GRP;
			    else if (line.contains(SmiTokens.NOTIF))
			        objectType = SmiTokens.NOTIF;
			    else if (line.contains(SmiTokens.MODULE_COMP))
			        objectType = SmiTokens.MODULE_COMP;
			    else if (line.contains(SmiTokens.MODULE_ID))
			        objectType = SmiTokens.MODULE_ID;
                else if (line.contains(SmiTokens.NOTIF_GRP))
                    objectType = SmiTokens.NOTIF_GRP;
			    						
				// read other object types
				if ( !objectType.equals("") && !line.trim().equalsIgnoreCase(objectType) 
				        && !line.contains(SmiTokens.SOURCE) && !line.contains(",") )
				{ 
                    MibObjectExtended mibObject = readObject(in, line, objectType);
                    
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
			Utilities.closeQuietly(in);
		}
        
    }

    
    /**
     * Reads the MIB file's IMPORTS section.
     */
    private void readImports(final BufferedReader in) throws IOException
    {
        // Read the entire IMPORTS section into a StringBuilder.
        char c = 0;
        StringBuilder importSection = new StringBuilder("");
        while (c != -1 && c != ';')
        {
            c = (char)in.read();
            importSection.append(c);
        }
        
        String[] importLines = importSection.toString().trim().split(SmiStructureHandler.NEWLINE_REGEX + "|,");
        
        MibImport importItem = new MibImport();
        for (int i = 0; i < importLines.length; i++) // iterate through tokens
        {
            boolean endOfImport = false;
            while (!endOfImport && i < importLines.length)
            {
                String element = importLines[i].trim();
                
                // Strip comments.
                if (element.contains("--"))
                {
                    int commentIndex = element.indexOf("--");
                    element = element.substring(0, commentIndex).trim();
                }
                
                // Skip empty lines.
                if (element.equals(""))
                {
                    i++;
                    continue;
                }
                
                if (element.contains(SmiTokens.SOURCE)) // identify source MIBS
                {
                    String source = element.trim();
                    
                    // take care of imports that get put in the same string as the FROM statement
                    if (!source.startsWith(SmiTokens.SOURCE))
                    {
                        String item = source.substring(0, source.indexOf(SmiTokens.SOURCE)).trim();
                        importItem.addImport(item);
                    }
                    
                    // don't include the ';' at the end
                    if (source.endsWith(";"))
                        source = source.substring(0, source.length() - 1);
                    
                    source = source.substring(source.indexOf(SmiTokens.SOURCE) 
                             + SmiTokens.SOURCE.length()).trim();
                    importItem.setSource(source);
                    
                    endOfImport = true;
                    mibDocFactory.addImportElement(importItem);
                    importItem = new MibImport();
                }
                else
                {
                    importItem.addImport(element.trim());
                }
                
                if (!endOfImport)
                    i++;
            }
        }
    }
   
    
    
    private MibObjectExtended readObject(final BufferedReader in, String line, String objectType) throws IOException
    { 
        // Get the node name.
        String name = "";
        int index = line.indexOf(objectType);
        name = line.substring(0, index).trim();

        // Initialize properties.
        String dataType = "";
        String defaultValue = "";
        String access = "";
        String status = "";
        String ref = "";
        StringBuilder description = new StringBuilder();
        
        String lastUpdated = "";
        String organization = "";
        StringBuilder contact = new StringBuilder();
        
        Map<Integer, String> pairs = null;
        List<String> indices = null;
        List<String> group = null;  
        List<MibModuleIdRevision> revisions = null;
        
        // read until the end of the object definition, retrieving relevant information
        line = in.readLine();
        while (line != null && !line.trim().startsWith("::="))
        {
        	line = line.trim();
        	
            // strip comments
            if (line.contains("--"))
            {
                int commentIndex = line.indexOf("--");
                line = line.substring(0, commentIndex).trim();
            }
            
            if (!line.equals(""))
            {
                // SYNTAX
                if (line.contains(SmiTokens.SYNTAX) && !objectType.equals(SmiTokens.MODULE_COMP))
                {
                    if (line.equals(SmiTokens.SYNTAX))
                        line = in.readLine();
        
                    // if the OID has a list of specific integer values
                    if (line != null)
                    {
                    	line = line.trim();
	                    if (line.contains("{"))
	                    {
	                        index = line.indexOf("{");
	                        dataType = line.substring(SmiTokens.SYNTAX.length(), index).trim();
	                        
	                        pairs = handler.readPairs(line, SmiTokens.SYNTAX);
	                    }
	                    else
	                        dataType = line.substring(SmiTokens.SYNTAX.length()).trim(); 
                    }
                }
                
                // ACCESS
                else if (line.contains(SmiTokens.ACCESS) && !objectType.equals(SmiTokens.MODULE_COMP))
                {      
                    index = line.indexOf(SmiTokens.ACCESS) + SmiTokens.ACCESS.length();
                    access = line.substring(index).trim();
                }
                
                // STATUS
                else if (line.contains(SmiTokens.STATUS))
                {
                    index = line.indexOf(SmiTokens.STATUS) + SmiTokens.STATUS.length();
                    status = line.substring(index).trim();
                }
                
                // LAST-UPDATED
                else if (line.contains(SmiTokens.MODULE_LAST_UPDATED))
                    lastUpdated = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).trim();
                
                
                // ORGANIZATION
                else if (line.contains(SmiTokens.MODULE_ORGANIZATION))
                    organization = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).trim();
                
                
                // DEFAULT VALUE (DEFVAL)
                else if (line.contains(SmiTokens.DEFAULT))
                {
                    if (line.contains("{") && line.contains("}"))
                        defaultValue = line.substring(line.indexOf("{") + 1, line.indexOf("}")).trim();
                }
                
                
                // OBJECT-GROUP members or NOTIFICATION-TYPE member
                else if ( (line.contains(SmiTokens.OBJECTS) && objectType.equals(SmiTokens.OBJECT_GRP)) || 
                         (line.contains(SmiTokens.NOTIFS) && objectType.equals(SmiTokens.NOTIF_GRP)) )
                {
                    if (line.contains("{"))
                        group = handler.readList(line, SmiTokens.OBJECTS);
                }

                                    
                // CONTACT-INFO
                else if (line.contains(SmiTokens.MODULE_CONTACT))
                    contact.append(handler.readQuotedSection(line, SmiTokens.MODULE_CONTACT));

                
                // DESCRIPTION
                else if (line.contains(SmiTokens.DESCRIPTION))
                    description.append(handler.readQuotedSection(line, SmiTokens.DESCRIPTION));

                
                // REFERENCE
                else if (line.contains(SmiTokens.REFERENCE))
                    ref = handler.readQuotedSection(line, SmiTokens.REFERENCE);

                
                // INDICES
                else if (line.contains(SmiTokens.INDICES))
                {
                    if (line.contains("{"))
                        indices = handler.readList(line, SmiTokens.INDICES);
                }

                
                // REVISION
                else if (line.contains(SmiTokens.MODULE_REVISION))
                {
                    // testing rev
                    String revisionId = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).trim();
                    
                    // As far as I've seen, a REVISION is always followed by a DESCRIPTION block, 
                    // but this may not always be true.  If it's not, there could be consequences 
                    // and repercussions.
                    line = in.readLine();
                    if (line != null)
                    {
                    	line = line.trim();
	                    String revDesc = "";
	                    if (line.contains(SmiTokens.DESCRIPTION))
	                        revDesc = handler.readQuotedSection(line, SmiTokens.DESCRIPTION);
	                    
	                    if (revisions == null)
	                        revisions = new ArrayList<MibModuleIdRevision>();
	                    
	                    MibModuleIdRevision revision = new MibModuleIdRevision(revisionId, revDesc);
	                    revisions.add(revision);
                    }
                }
                
                line = in.readLine();
            }
            else
                line = in.readLine();

        }

        if (line != null)
        {
	        // the line with ::= should be processed here
	        String objInfo = line.substring(line.indexOf("{") + 1, line.indexOf("}")).trim();
	        String parent = "";
	
	        if (objInfo.contains("(") && objInfo.contains(")"))
	        {   // these have the form :={test(1) test2(6) test3(2) 1}
	            
	            String parents = "";
	            parents = objInfo.substring(objInfo.indexOf(" "), objInfo.lastIndexOf(")"));
	            parents = parents.substring(parents.lastIndexOf(" "), parents.lastIndexOf("("));
	            parent = parents.trim();
	        }
	        else
	        {   // these have the regular form :={test 1}
	            index = objInfo.indexOf(" ");
	            parent = objInfo.substring(0, index);
	        }
	        
	        int index2 = objInfo.lastIndexOf(" ") + 1;
	        String objIndex = "";
	        objIndex = objInfo.substring(index2, objInfo.length());
	
	        //System.out.println(nodeParent + " " + nodeIndex);
	
	        // set basic properties
	        MibObjectExtended mibObject = new MibObjectExtended(name, Integer.parseInt(objIndex));
	        
	        mibObject.setDescription(description.toString());
	        
	        if (!access.equals(""))
	            mibObject.setAccess(Access.valueOf(access.toUpperCase().replaceAll("-", "_")));
	
	        if (!status.equals(""))
	            mibObject.setStatus(Status.valueOf(status.toUpperCase()));
	        
	        mibObject.setReference(ref);
	        
	        // construct the syntax object
	        // -the object's type is the only thing required for the existence of a syntax element
	        if (!dataType.equals("")) 
	        {
	            MibSyntax syntax = new MibSyntax(dataType);
	            syntax.setDefaultValue(defaultValue);
	            if (pairs != null)
	                syntax.setValuePairs(pairs);
	            
	            mibObject.setSyntax(syntax);
	        }
	        
	        if (indices != null)
	            mibObject.setIndices(indices);
	        
	        // set extended properties
	        mibObject.setObjectType(objectType);
	        mibObject.setParent(parent);
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

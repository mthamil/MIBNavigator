/**
 * MIB To XML Converter
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
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import libmib.MibDocumentBuilder;
import libmib.MibImport;
import libmib.InvalidSmiMibFormatException;
import libmib.SmiTokens;
import libmib.SmiStructureHandler;
import libmib.oid.MibModuleIdRevision;
import libmib.oid.MibObjectType.Access;
import libmib.oid.MibObjectType.Status;
import libmib.oid.MibObjectExtended;
import libmib.oid.MibSyntax;
import libmib.oid.MibNameValuePair;

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
    private ArrayList<String> introComments = null;
    private MibDocumentBuilder mibDocFactory;

    public MibToXmlConverter()
    {
        introComments = new ArrayList<String>();    
        mibDocFactory = new MibDocumentBuilder();
    }
    
    public void readMIB(File inputMIBFile) throws InvalidSmiMibFormatException
    {   
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(inputMIBFile));
            handler = new SmiStructureHandler(in);
			String line;

			// If the file contained in the BufferedReader does not contain the correct MIB 'header', 
            // throw an exception. (it's game over, man)
			String mibName = handler.readMibName();
            if (mibName.equals(""))
                throw new InvalidSmiMibFormatException(inputMIBFile);
            
            mibDocFactory.addDefinitionAttribute(mibName);

            System.out.println("Valid MIB");
            
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
                if (line.contains(SmiTokens.IMPORTS_BEGIN))               
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
                if ( line.contains(SmiTokens.OBJECT_ID) && !line.contains(SmiTokens.OID_SYNTAX) && !line.contains(",") 
                        && !line.contains(SmiTokens.IMPORT_FROM) && !line.trim().equals(SmiTokens.OBJECT_ID))
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
							MibObjectExtended curMIBObject = new MibObjectExtended(objName, Integer.parseInt(objIndex));
                            curMIBObject.setObjectType(SmiTokens.OBJECT_ID);
                            curMIBObject.setParent(objParent);

						    mibDocFactory.addObjectElement(curMIBObject);
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
				        && !line.contains(SmiTokens.IMPORT_FROM) && !line.contains(",") )
				{ 
                    MibObjectExtended mibObject = readObject(in, line, objectType);
                    
                    // add this object to the list of MIB objects
                    mibDocFactory.addObjectElement(mibObject);
				}
			}

			in.close();  // close the input file before doing anything else
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage() + " " + e.getCause());
            e.printStackTrace();
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
        while ( (c != -1) && (c != ';'))
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
                String curElement = importLines[i].trim();
                
                // Strip comments.
                if (curElement.contains("--"))
                {
                    int commentIndex = curElement.indexOf("--");
                    curElement = curElement.substring(0, commentIndex).trim();
                }
                
                // Skip empty lines.
                if (curElement.equals(""))
                {
                    i++;
                    continue;
                }
                
                if (curElement.contains(SmiTokens.IMPORT_FROM)) // identify source MIBS
                {
                    String source = curElement.trim();
                    
                    // take care of imports that get put in the same string as the FROM statement
                    if (!source.startsWith(SmiTokens.IMPORT_FROM))
                    {
                        String item = source.substring(0, source.indexOf(SmiTokens.IMPORT_FROM)).trim();
                        importItem.addImportItem(item);
                    }
                    
                    // don't include the ';' at the end
                    if (source.endsWith(";"))
                        source = source.substring(0, source.length() - 1);
                    
                    source = source.substring(source.indexOf(SmiTokens.IMPORT_FROM) 
                             + SmiTokens.IMPORT_FROM.length()).trim();
                    importItem.setSource(source);
                    
                    endOfImport = true;
                    mibDocFactory.addImportItemElement(importItem);
                    importItem = new MibImport();
                }
                else
                {
                    importItem.addImportItem(curElement.trim());
                }
                
                if (!endOfImport)
                    i++;
            }
        }
    }
   
    
    
    private MibObjectExtended readObject(final BufferedReader in, String line, String objectType) throws IOException
    { 
        // get the node name
        String objName = "";
        int index = line.indexOf(objectType);
        objName = line.substring(0, index).trim();

        // initialize properties
        String objDataType = "";
        String defaultValue = "";
        String objAccess = "";
        String objStatus = "";
        String objRef = "";
        StringBuilder objDesc = new StringBuilder();
        
        String objLastUpdated = "";
        String objOrg = "";
        StringBuilder objContact = new StringBuilder();
        
        List<MibNameValuePair> objValues = null;  //synchronization not needed
        List<String> objIndices = null;
        List<String> objGroup = null;  
        List<MibModuleIdRevision> objRevList = null;
        
        // read until the end of the object definition, retrieving relevant information
        line = in.readLine().trim();
        while (!line.startsWith("::=") && line != null)
        {
            // strip comments
            if (line.contains("--"))
            {
                int commentIndex = line.indexOf("--");
                line = line.substring(0, commentIndex).trim();
            }
            
            if (!line.trim().equals(""))
            {
                // SYNTAX
                if (line.contains(SmiTokens.OID_SYNTAX) && !objectType.equals(SmiTokens.MODULE_COMP))
                {
                    if (line.trim().equals(SmiTokens.OID_SYNTAX))
                        line = in.readLine().trim();
        
                    // if the OID has a list of specific integer values
                    if (line.contains("{"))
                    {
                        index = line.indexOf("{");
                        objDataType = line.substring(SmiTokens.OID_SYNTAX.length(), index).trim();
                        
                        objValues = handler.readValueList(line, SmiTokens.OID_SYNTAX);
                    }
                    else
                        objDataType = line.substring(SmiTokens.OID_SYNTAX.length()).trim(); 
                }
                
                // ACCESS
                else if (line.contains(SmiTokens.OID_ACCESS) && !objectType.equals(SmiTokens.MODULE_COMP))
                {      
                    index = line.indexOf(SmiTokens.OID_ACCESS) + SmiTokens.OID_ACCESS.length();
                    objAccess = line.substring(index).trim();
                }
                
                // STATUS
                else if (line.contains(SmiTokens.OID_STATUS))
                {
                    index = line.indexOf(SmiTokens.OID_STATUS) + SmiTokens.OID_STATUS.length();
                    objStatus = line.substring(index).trim();
                }
                
                // LAST-UPDATED
                else if (line.contains(SmiTokens.MODULE_LAST_UPDATED))
                    objLastUpdated = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).trim();
                
                
                // ORGANIZATION
                else if (line.contains(SmiTokens.MODULE_ORGANIZATION))
                    objOrg = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).trim();
                
                
                // DEFAULT VALUE (DEFVAL)
                else if (line.contains(SmiTokens.OID_DEFAULT_VALUE))
                {
                    if (line.contains("{") && line.contains("}"))
                        defaultValue = line.substring(line.indexOf("{") + 1, line.indexOf("}")).trim();
                }
                
                
                // OBJECT-GROUP members or NOTIFICATION-TYPE member
                else if ( (line.contains(SmiTokens.OBJS) && objectType.equals(SmiTokens.OBJECT_GRP)) || 
                         (line.contains(SmiTokens.NOTIFS) && objectType.equals(SmiTokens.NOTIF_GRP)) )
                {
                    if (line.contains("{"))
                        objGroup = handler.readList(line, SmiTokens.OBJS);
                }

                                    
                // CONTACT-INFO
                else if (line.contains(SmiTokens.MODULE_CONTACT))
                    objContact.append(handler.readQuotedSection(line, SmiTokens.MODULE_CONTACT));

                
                // DESCRIPTION
                else if (line.contains(SmiTokens.OID_DESCRIPTION))
                    objDesc.append(handler.readQuotedSection(line, SmiTokens.OID_DESCRIPTION));

                
                // REFERENCE
                else if (line.contains(SmiTokens.OID_REFERENCE))
                    objRef = handler.readQuotedSection(line, SmiTokens.OID_REFERENCE);

                
                // INDICES
                else if (line.contains(SmiTokens.OID_INDICES))
                {
                    if (line.contains("{"))
                        objIndices = handler.readList(line, SmiTokens.OID_INDICES);
                }

                
                // REVISION
                else if (line.contains(SmiTokens.MODULE_REVISION))
                {
                    // testing rev
                    String revisionId = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).trim();
                    
                    // As far as I've seen, a REVISION is always followed by a DESCRIPTION block, 
                    // but this may not always be true.  If it's not, there could be consequences 
                    // and repercussions.
                    line = in.readLine().trim();
                    
                    String revDesc = "";
                    if (line.contains(SmiTokens.OID_DESCRIPTION))
                        revDesc = handler.readQuotedSection(line, SmiTokens.OID_DESCRIPTION);
                    
                    if (objRevList == null)
                        objRevList = new ArrayList<MibModuleIdRevision>();
                    
                    MibModuleIdRevision revision = new MibModuleIdRevision(revisionId, revDesc);
                    objRevList.add(revision);
                }
                
                line = in.readLine().trim();
            }
            else
                line = in.readLine().trim();

        }

        // the line with ::= should be processed here
        String objInfo = line.substring(line.indexOf("{") + 1, line.indexOf("}")).trim();
        String objParent = "";

        if (objInfo.contains("(") && objInfo.contains(")"))
        {   // these have the form :={test(1) test2(6) test3(2) 1}
            
            String parents = "";
            parents = objInfo.substring(objInfo.indexOf(" "), objInfo.lastIndexOf(")"));
            parents = parents.substring(parents.lastIndexOf(" "), parents.lastIndexOf("("));
            objParent = parents.trim();
        }
        else
        {   // these have the regular form :={test 1}
            index = objInfo.indexOf(" ");
            objParent = objInfo.substring(0, index);
        }
        
        int index2 = objInfo.lastIndexOf(" ") + 1;
        String objIndex = "";
        objIndex = objInfo.substring(index2, objInfo.length());

        //System.out.println(nodeParent + " " + nodeIndex);

        // set basic properties
        MibObjectExtended mibObject = new MibObjectExtended(objName, Integer.parseInt(objIndex));
        
        mibObject.setDescription(objDesc.toString());
        
        if (!objAccess.equals(""))
            mibObject.setAccess(Access.valueOf(objAccess.toUpperCase().replaceAll("-", "_")));

        if (!objStatus.equals(""))
            mibObject.setStatus(Status.valueOf(objStatus.toUpperCase()));
        
        mibObject.setReference(objRef);
        
        // construct the syntax object
        // -the object's type is the only thing required for the existence of a syntax element
        if (!objDataType.equals("")) 
        {
            MibSyntax objSyntax = new MibSyntax(objDataType);
            objSyntax.setDefaultValue(defaultValue);
            if (objValues != null)
                objSyntax.setValues(objValues);
            
            mibObject.setSyntax(objSyntax);
        }
        
        if (objIndices != null)
            mibObject.setIndices(objIndices);
        
        // set extended properties
        mibObject.setObjectType(objectType);
        mibObject.setParent(objParent);
        mibObject.setLastUpdated(objLastUpdated);
        mibObject.setOrganization(objOrg);
        mibObject.setContactInfo(objContact.toString());
        
        if (objGroup != null)
            mibObject.setObjectGroup(objGroup);
        
        if (objRevList != null)
            mibObject.setRevisions(objRevList);
        
        return mibObject;
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
    
    
	public static void main(String args[])
	{   
        // some basic code to execute the converter
        File mibFile = null;
        
        if (args.length > 0)
            mibFile = new File(args[0].trim());  //use the first option on the command line as the file name   
        else  
        {
            // if there were no command line options, pop up a file dialog
            JFileChooser fc = new JFileChooser(new File("."));
            int retVal = fc.showOpenDialog(null);
    
            if (retVal == JFileChooser.APPROVE_OPTION)
                mibFile = fc.getSelectedFile();
        }
        
        if (mibFile != null)
        {
            if (mibFile.exists())
            {
                try
                {
                    MibToXmlConverter converter = new MibToXmlConverter();
                    
                    // construct the output xml file's name to be the same as the input file but with a different extension
                    File xmlFile;
                    String mibFilename = mibFile.getName();
                    
                    if (mibFilename.contains(".")) 
                        mibFilename = mibFilename.substring(0, mibFilename.lastIndexOf("."));
                    
                    xmlFile = new File(mibFilename + ".xml");
    
                    converter.readMIB(mibFile);
                    converter.writeXML(xmlFile);
                }
                catch (InvalidSmiMibFormatException e)
                {
                    System.out.println(e.getMessage());
                }
            }
            else
                System.out.println("Error Opening File: " + mibFile.getName() + " not found.");
        }
    }
    
}

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

import static libmib.format.smi.SMIToken.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import libmib.MibImport;
import libmib.MibObjectExtended;
import libmib.format.smi.InvalidSmiMibFormatException;
import libmib.format.smi.SMIStructureHandler;
import libmib.format.smi.SMIStructureHandler.HierarchyData;
import libmib.format.smi.parsers.SMIParserFactory;
import utilities.IOUtilities;

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
                    List<MibImport> imports = (List<MibImport>)SMIParserFactory.getParser(IMPORTS).parse(reader, line);
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
                			
				 MibObjectExtended mibObject = (MibObjectExtended)SMIParserFactory.getParser(OBJECT_GROUP).parse(reader, line);
				 if (mibObject != null)
					 mibDocFactory.addObjectElement(mibObject);
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

    
    /**
     * Writes the constructed document to an XML file.
     */
    public void writeXML(File outputXMLFile)
    {    
        mibDocFactory.writeDocument(outputXMLFile);
    }
}

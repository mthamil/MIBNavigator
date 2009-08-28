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

package libmib.mibtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

import utilities.IOUtilities;

import static libmib.format.smi.SMIToken.*;
import libmib.MibObjectType;
import libmib.MibSyntax;
import libmib.MibObjectType.Access;
import libmib.MibObjectType.Status;
import libmib.format.smi.InvalidSmiMibFormatException;
import libmib.format.smi.SMIParsers;
import libmib.format.smi.SMIStructureHandler;
import libmib.format.smi.SMIToken;
import libmib.format.smi.SMIStructureHandler.HierarchyData;

/**
 * This class is a very crusty reader and parser for ASN.1 specification SMI MIB module
 * definition files. It reads the files, attempts to interpret the syntax of the structures 
 * within, extracts relevant data, and inserts it into nodes for use in a tree structure.  
 * Then, it compiles the SMI MIB modules into a DefaultTreeModel. It in no way attempts to 
 * implement the entire SMI syntax, only a small subset. Nodes in the tree are indexed by 
 * a HashMap that maps MIB object names to MIBTreeNodes.
 */
public class MibTreeBuilderSmi extends AbstractMibTreeBuilder
{  
    
    /**
     * Parses an SMI MIB module text file and adds its elements to the MIB tree model.
     * 
     * @param mibFile the File to add to the MIB Tree
     * 
     * @throws InvalidSmiMibFormatException if the MIB file is not in a valid format
     */
    protected void addMibToTree(File mibFile) throws InvalidSmiMibFormatException
    { 
    	BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(mibFile));
            String line;

            // If the file does not contain the correct MIB 'header', throw an exception.
            // it's game over, man
            String mibName = SMIStructureHandler.readMibName(reader);
            if (mibName.equals(""))
                throw new InvalidSmiMibFormatException(mibFile);
            

            //System.out.println(mibFile.getName() + ": valid");
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
                    
                // Skip the 'IMPORTS' section.
                if (line.contains(IMPORTS.token()))               
                {
                    char c = 0;
                    while ( (c != -1) && (c != ';'))
                        c = (char)reader.read();
                    
                    continue;
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
                
                // Special case for basic MIB objects consisting of only a name, parent, and OID index.
                if ( line.contains(OBJECT_ID.token()) && !line.contains(SYNTAX.token()) && !line.contains(",") 
                        && !line.contains(SOURCE.token()) && !line.trim().startsWith(OBJECT_ID.token()))
                {
                    StringBuilder oidDef = new StringBuilder();
                    oidDef.append(line);

                    // Check whether the entire definition is on this line.
                    if (oidDef.indexOf("}") < 0) 
                    {
                        char c = ' ';  
                        do
                        {
                            c = (char)reader.read();
                            oidDef.append(c);
                        } while (c != '}');
                    }
                    String oidString = oidDef.toString().replaceAll("\\s+", " ").trim(); //simplify whitespace
                    
                    if (oidString.contains("::=") && oidString.contains("{"))
                    {
                        String nodeName = "";    
                        
                        // Get the mib object name.
                        int index = oidString.indexOf(OBJECT_ID.token());
                        nodeName = oidString.substring(0, index).trim();
                        //System.out.println(nodeName);

                        // Check the HashMap to see if this node already exists in the tree.
                        if (!nodeMap.containsKey(nodeName)) 
                        {
                            String nodeInfo = oidString.substring(index + OBJECT_ID.token().length());
                            HierarchyData nodeData = SMIStructureHandler.parseHierarchyData(nodeInfo);

                            // make sure parent and index are not empty since some data type definitions can look like OIDs
                            if ( !nodeName.equals("") && !nodeData.getParent().equals("") && nodeData.getIndex() != -1 )
                            {
                                MibObjectType mibObject = new MibObjectType(nodeName, nodeData.getIndex());
                                mibObject.setMibName(mibName);
                                
                                this.addMibObject(mibObject, nodeData.getParent());
                                
                            } 
                        }
                    }
                }
                
                else
                {
                    // Test for object type.
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
                                            
                    // Read other object types.
                    if ( objectType != null && !line.trim().equalsIgnoreCase(objectType.token()) 
                            && !line.contains(SOURCE.token()) && !line.contains(",") )
                    {
                        MibObjectType mibObject = new MibObjectType();
                        mibObject.setMibName(mibName);
                        
                        line = readMIBObject(reader, line, mibObject, objectType);
                    }
                }
                
            } // loop until EOF or the END marker is reached

        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
        	IOUtilities.closeQuietly(reader);
        }
    }
    
    
    /**
     * Reads and parses information about a MIB Object from a MIB file, puts it into a 
     * MIB Object and MibTreeNode, and adds the node to the tree if the parent exists 
     * and the node does not already exist in the tree.
     * 
     * @param reader the BufferedReader for the current MIB file
     * @param line the most recently read line in the file
     * @param mibObject the current MIB Object
     * @param objectType the type of the MIB Object
     * @return the line in the file that was last read before returning
     * 
     * @throws IOException if an error occurs reading a line from the BufferedReader
     */
    private String readMIBObject(final BufferedReader reader, String line, MibObjectType mibObject, SMIToken objectType) throws IOException
    { 
        // get the node name
        String nodeName = "";
        int index = line.indexOf(objectType.token());
        nodeName = line.substring(0, index).trim();
        
        // check the HashMap to see if this node already exists in the tree
        if (!nodeMap.containsKey(nodeName)) 
        {
            // Initialize properties
            Access nodeAccess = null;
            Status nodeStatus = null;
            MibSyntax nodeSyntax = null;
            StringBuilder nodeDesc = new StringBuilder();

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
                        nodeAccess = (Access)SMIParsers.getParser(ACCESS).parse(reader, line);
                    
                    // STATUS
                    else if (line.contains(STATUS.token()))
                        nodeStatus = (Status)SMIParsers.getParser(STATUS).parse(reader, line);
    
                    // DESCRIPTION
                    else if (line.contains(DESCRIPTION.token()))
                        nodeDesc.append((String)SMIParsers.getParser(DESCRIPTION).parse(reader, line));

                    line = reader.readLine();
                }
                else
                {
                    line = reader.readLine();
                }
            }
    
            if (line != null)
            {
	            // the line with ::= should be processed here
	            HierarchyData nodeInfo = SMIStructureHandler.parseHierarchyData(line);
	            //System.out.println(nodeInfo.getParent() + " " + nodeInfo.getIndex());
	
	            // set basic properties
	            mibObject.setName(nodeName);
	            mibObject.setId(nodeInfo.getIndex());
	            
	            mibObject.setDescription(nodeDesc.toString());
	            
	            if (nodeAccess != null)
	                mibObject.setAccess(nodeAccess);
	            
	            if (nodeStatus != null)
	                mibObject.setStatus(nodeStatus);
	            
	            if (nodeSyntax != null) 
	            {
	            	// Synchronize because there MAY be  simultaneous access.
	                if (nodeSyntax.hasValues())
	                    nodeSyntax.setValuePairs(Collections.synchronizedMap(nodeSyntax.getValuePairs()));
	                
	                mibObject.setSyntax(nodeSyntax);
	            }
	            
	            this.addMibObject(mibObject, nodeInfo.getParent());
            }
            
        }
        else // if it already exists, read past it's data
        { 
            while (line != null && !line.contains("::="))
                line = reader.readLine();
        }

        return line;
    }
    
    
    /**
     * @see libmib.mibtree.MibTreeBuilder#getMibDirectory() 
     */
	@Override
	public String getMibDirectory()
	{
		return "smi";
	}
}

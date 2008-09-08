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

package libmib.mibtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import libmib.InvalidSmiMibFormatException;
import libmib.SmiKeywords;
import libmib.SmiStructureHandler;
import libmib.oid.MibObjectType;
import libmib.oid.MibSyntax;
import libmib.oid.MibNameValuePair;
import libmib.oid.MibObjectType.Access;
import libmib.oid.MibObjectType.Status;

/**
 * This class is a very simple reader and parser for ASN.1 specification SMI MIB module
 * definition files. It reads the files, attempts to interpret the syntax of the structures 
 * within, extracts relevant data, and inserts it into nodes for use in a tree structure.  
 * Then, it compiles the SMI MIB modules into a DefaultTreeModel. It in no way attempts to 
 * implement the entire SMI syntax, only a small subset. Nodes in the tree are indexed by 
 * a HashMap that maps MIB object names to MIBTreeNodes.
 */
public class MibTreeBuilderSmi extends AbstractMibTreeBuilder
{
    private SmiStructureHandler handler;
    
    
    /**
     * Parses an SMI MIB module text file and adds its elements to the MIB tree model.
     * 
     * @param mibFile the File to add to the MIB Tree
     * 
     * @throws InvalidSmiMibFormatException if the MIB file is not in a valid format
     */
    protected void addMIBToTree(File mibFile) throws InvalidSmiMibFormatException
    { 
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(mibFile));
            handler = new SmiStructureHandler(in);
            String line;

            // If the file does not contain the correct MIB 'header', throw an exception.
            // it's game over, man
            String mibName = handler.readMibName();
            if (mibName.equals(""))
                throw new InvalidSmiMibFormatException(mibFile);
            

            //System.out.println(mibFile.getName() + ": valid");
            while ( ((line = in.readLine()) != null) && !line.trim().equals(SmiKeywords.MIB_END))
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
                    
                // Skip the 'IMPORTS' section.
                if (line.contains(SmiKeywords.IMPORTS_BEGIN))               
                {
                    char c = 0;
                    while ( (c != -1) && (c != ';'))
                        c = (char)in.read();
                    
                    continue;
                }
                
                
                // Skip 'MACRO' definitions.
                if (line.contains("MACRO ::="))
                {
                    while ( (line = in.readLine()) != null )
                    {
                        if (line.trim().equals(SmiKeywords.MIB_END))
                            break;
                    }
                    continue;
                }
                
                // Special case for basic MIB objects consisting of only a name, parent, and OID index.
                if ( line.contains(SmiKeywords.OBJECT_ID) && !line.contains(SmiKeywords.OID_SYNTAX) && !line.contains(",") 
                        && !line.contains(SmiKeywords.IMPORT_FROM) && !line.trim().startsWith(SmiKeywords.OBJECT_ID))
                {
                    StringBuilder oidDef = new StringBuilder();
                    oidDef.append(line);

                    // Check whether the entire definition is on this line.
                    if (oidDef.indexOf("}") < 0) 
                    {
                        char c = ' ';  
                        do
                        {
                            c = (char)in.read();
                            oidDef.append(c);
                        } while (c != '}');
                    }
                    String oidString = oidDef.toString().replaceAll("\\s+", " ").trim(); //simplify whitespace
                    
                    if (oidString.contains("::=") && oidString.contains("{"))
                    {
                        String nodeName = "";    
                        
                        // Get the mib object name.
                        int index = oidString.indexOf(SmiKeywords.OBJECT_ID);
                        nodeName = oidString.substring(0, index).trim();
                        //System.out.println(nodeName);

                        // Check the HashMap to see if this node already exists in the tree.
                        if (!nodeMap.containsKey(nodeName)) 
                        {
                            // initializations
                            String nodeInfo = "";
                            String nodeParentName = "";
                            String nodeIndex = "";;  
                            
                            nodeInfo = oidString.substring(index + SmiKeywords.OBJECT_ID.length());
                            nodeInfo = nodeInfo.substring(nodeInfo.indexOf("{") + 1, nodeInfo.indexOf("}")).trim();

                            String parents = "";
                            if (nodeInfo.contains("(") && nodeInfo.contains(")"))
                            {   // these have the form ':= {test(1) test2(6) test3(2) 1}'
                                
                                parents = nodeInfo.substring(nodeInfo.indexOf(" "), nodeInfo.lastIndexOf(")"));
                                parents = parents.substring(parents.lastIndexOf(" "), parents.lastIndexOf("("));
                                nodeParentName = parents.trim();
                            }
                            else
                            {   // these have the regular form ':= {test 1}'
                                index = nodeInfo.indexOf(" ");
                                nodeParentName = nodeInfo.substring(0, index);
                            }
                            
                            int index2 = nodeInfo.lastIndexOf(" ") + 1;
                            nodeIndex = nodeInfo.substring(index2, nodeInfo.length());
                            

                            // make sure parent and index are not empty since some data type definitions can look like OIDs
                            if ( !nodeName.equals("") && !nodeParentName.equals("") && !nodeIndex.equals("") )
                            {
                                MibObjectType curMIBObject = new MibObjectType(nodeName, Integer.parseInt(nodeIndex));
                                curMIBObject.setMibName(mibName);
                                
                                this.addMibObject(curMIBObject, nodeParentName);
                                
                            } 
                        }
                    }
                }
                
                else
                {
                    // Test for object type.
                    String objectType = "";
                    if (line.contains(SmiKeywords.OBJECT_TYPE))
                        objectType = SmiKeywords.OBJECT_TYPE;
                    else if (line.contains(SmiKeywords.OBJECT_GRP))
                        objectType = SmiKeywords.OBJECT_GRP;
                    else if (line.contains(SmiKeywords.NOTIF))
                        objectType = SmiKeywords.NOTIF;
                    else if (line.contains(SmiKeywords.MODULE_COMP))
                        objectType = SmiKeywords.MODULE_COMP;
                    else if (line.contains(SmiKeywords.MODULE_ID))
                        objectType = SmiKeywords.MODULE_ID;
                    else if (line.contains(SmiKeywords.NOTIF_GRP))
                        objectType = SmiKeywords.NOTIF_GRP;
                                            
                    // Read other object types.
                    if ( !objectType.equals("") && !line.trim().equalsIgnoreCase(objectType) 
                            && !line.contains(SmiKeywords.IMPORT_FROM) && !line.contains(",") )
                    {
                        MibObjectType mibObject = new MibObjectType();
                        mibObject.setMibName(mibName);
                        
                        line = readMIBObject(in, mibObject, line, objectType);
                    }
                }
                
            } // loop until EOF or the END marker is reached

            in.close();  // close the input file
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    
    /**
     * Reads and parses information about a MIB Object from a MIB file, puts it into a 
     * MIB Object and MibTreeNode, and adds the node to the tree if the parent exists 
     * and the node does not already exist in the tree.
     * 
     * @param in the BufferedReader for the current MIB file
     * @param mibObject the current MIB Object
     * @param line the most recently read line in the file
     * @param objectType the type of the MIB Object
     * 
     * @return the line in the file that was last read before returning
     * 
     * @throws IOException if an error occurs reading a line from the BufferedReader
     */
    private String readMIBObject(final BufferedReader in, MibObjectType mibObject, String line, String objectType) throws IOException
    { 
        // get the node name
        String nodeName = "";
        int index = line.indexOf(objectType);
        nodeName = line.substring(0, index).trim();
        
        // check the HashMap to see if this node already exists in the tree
        if (!nodeMap.containsKey(nodeName)) 
        {
            // initialize properties
            String nodeDataType = "";
            String nodeAccess = "";
            String nodeStatus = "";
            
            StringBuilder nodeDesc = new StringBuilder();
            List<MibNameValuePair> nodeValues = null;
    
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
                    if (line.contains(SmiKeywords.OID_SYNTAX) && !objectType.equals(SmiKeywords.MODULE_COMP))
                    {
                        if (line.trim().equals(SmiKeywords.OID_SYNTAX))
                            line = in.readLine();
    
                        // if the OID has a list of specific integer values
                        if (line.contains("{"))
                        {
                            index = line.indexOf("{");
                            nodeDataType = line.substring(SmiKeywords.OID_SYNTAX.length(), index).trim();
                            
                            nodeValues = handler.readValueList(line, SmiKeywords.OID_SYNTAX);
                        }
                        else
                            nodeDataType = line.substring(SmiKeywords.OID_SYNTAX.length()).trim();
                    }
                    
                    // ACCESS
                    else if (line.contains(SmiKeywords.OID_ACCESS) && !objectType.equals(SmiKeywords.MODULE_COMP))
                    {
                        index = line.indexOf(SmiKeywords.OID_ACCESS) + SmiKeywords.OID_ACCESS.length();
                        nodeAccess = line.substring(index).trim();
                    }
                    
                    // STATUS
                    else if (line.contains(SmiKeywords.OID_STATUS))
                    {
                        index = line.indexOf(SmiKeywords.OID_STATUS) + SmiKeywords.OID_STATUS.length();
                        nodeStatus = line.substring(index).trim();
                    }
    
                    // DESCRIPTION
                    else if (line.contains(SmiKeywords.OID_DESCRIPTION))
                    {
                        nodeDesc.append(handler.readQuotedSection(line, SmiKeywords.OID_DESCRIPTION));
                    }

                    line = in.readLine().trim();
                }
                else
                {
                    line = in.readLine().trim();
                }
            }
    
            // the line with ::= should be processed here
            String nodeInfo = line.substring(line.indexOf("{") + 1, line.indexOf("}")).trim();
            String nodeParentName = "";
            
            if (nodeInfo.contains("(") && nodeInfo.contains(")"))
            {   // these have the form ':= {test(1) test2(6) test3(2) 1}'
                
                String parents = "";
                parents = nodeInfo.substring(nodeInfo.indexOf(" "), nodeInfo.lastIndexOf(")"));
                parents = parents.substring(parents.lastIndexOf(" "), parents.lastIndexOf("("));
                nodeParentName = parents.trim();
            }
            else
            {   // these have the regular form ':= {test 1}'
                index = nodeInfo.indexOf(" ");
                nodeParentName = nodeInfo.substring(0, index);
            }
            
            int index2 = nodeInfo.lastIndexOf(" ") + 1;
            String nodeIndex = "";
            nodeIndex = nodeInfo.substring(index2, nodeInfo.length());
    
            //System.out.println(nodeParent + " " + nodeIndex);

            // set basic properties
            mibObject.setName(nodeName);
            mibObject.setId(Integer.parseInt(nodeIndex));
            
            mibObject.setDescription(nodeDesc.toString());
            
            if (!nodeAccess.equals(""))
                mibObject.setAccess(Access.valueOf(nodeAccess.toUpperCase().replaceAll("-", "_")));
            
            if (!nodeStatus.equals(""))
                mibObject.setStatus(Status.valueOf(nodeStatus.toUpperCase()));
            
            // construct the Syntax object
            // -the object's type is the only thing required for the existence of a syntax element
            if (!nodeDataType.equals("")) 
            {
                MibSyntax nodeSyntax = new MibSyntax(nodeDataType);
                if (nodeValues != null)
                    nodeSyntax.setValues(Collections.synchronizedList(nodeValues)); //synchronize because there MAY be 
                                                                                        //simultaneous access
                mibObject.setSyntax(nodeSyntax);
            }
            
            this.addMibObject(mibObject, nodeParentName);
            
        }
        else // if it already exists, read past it's data
        { 
            while (!line.contains("::=") && (line != null))
                line = in.readLine();
        }

        return line;
    }
    
    
    /**
     * @see libmib.mibtree.MibTreeBuilder#getMibFolder() 
     */
	@Override
	public String getMibFolder()
	{
		return "smi";
	}
}

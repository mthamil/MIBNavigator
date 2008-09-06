/**
 * libmib - Java SNMP Management Information Base Library
 *
 * Copyright (C) 2005, Matt Hamilton <matthew.hamilton@washburn.edu>
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import libmib.InvalidMibFormatException;
import libmib.oid.MibObjectType;

/**
 * AbstractMibTreeBuilder implements functionality that can be shared by different MIB parsers.  It 
 * contains most of the logic that does not pertain to the actual processing of a MIB file's structure 
 * and leaves the details of how to actually parse a MIB up to concrete subclasses.
 */
public abstract class AbstractMibTreeBuilder implements MibTreeBuilder
{
    protected TreeModel mibTreeModel = null;
    
    // Contains nodes that couldn't be added to the MIB tree at creation time and their parents
    // in pairs of (lost node, parent name).
    protected List<Object[]> lostChildren;
    
    // This improves performance in searching for existing nodes with what seems to be 
    // relatively little memory cost.  According to JProfiler, the call to the very 
    // inefficient getNodeByName actually took up the most amount of CPU time. Now,
    // using the HashMap to search for existing nodes is an O(1) operation.
    protected HashMap<String, MibTreeNode> nodeMap;
    
    
    public AbstractMibTreeBuilder()
    {
        // Initialize the HashMap with 2000 entries to avoid rehashing early.
        nodeMap = new HashMap<String, MibTreeNode>(2000); 
                
        // Tree Initialization
        MibObjectType root = new MibObjectType("root", 0);
        MibTreeNode rootNode = new MibTreeNode(root); //invisible root for structural purposes

        MibObjectType iso = new MibObjectType("iso", 1);
        MibTreeNode isoNode = new MibTreeNode(iso);
        nodeMap.put("iso", isoNode);

        MibObjectType ccitt = new MibObjectType("ccitt", 0);
        MibTreeNode ccittNode = new MibTreeNode(ccitt);
        nodeMap.put("ccitt", ccittNode);

        MibObjectType org = new MibObjectType("org", 3);
        MibTreeNode orgNode = new MibTreeNode(org);
        nodeMap.put("org", orgNode);

        MibObjectType dod = new MibObjectType("dod", 6);
        MibTreeNode dodNode = new MibTreeNode(dod);
        nodeMap.put("dod", dodNode);

        mibTreeModel = new DefaultTreeModel(rootNode); //use a default tree model

        rootNode.add(isoNode);
        rootNode.add(ccittNode);
        isoNode.add(orgNode);
        orgNode.add(dodNode);
    }


    /**
     * Adds all MIB files in the specified directory to the MIB tree if that directory 
     * exists and the files are valid.  Because potentially more than one MIB is being 
     * added, the order in which the files are added is taken into account.
     * If a file in the directory is invalid, it is skipped and the next file is processed.
     * 
     * @param mibDir the directory containing the MIB files
     * 
     * @throws IllegalArgumentException if mibDir is not a directory
     */
    public void addMIBDirectory(File mibDir)
    {
        // Make sure the file exists and is actually a directory.
        if (!mibDir.isDirectory())
            throw new IllegalArgumentException("\"" + mibDir.getName() + "\" is not a directory.");

        File[] mibDirList = mibDir.listFiles();

        // Organize MIB files for a more optimal compile order.
        // This is really half-assed; a proper handling of dependencies is very much in order,
        // but I am not sure how to do this the first time around.
        // TODO: create a way of determining mib dependencies and organizing the file list
        //       accordingly
        File[] rfcList = new File[mibDirList.length];
        File[] otherList = new File[mibDirList.length];

        int rfcCnt = 0;
        int otherCnt = 0;
        for (int i = 0; i < mibDirList.length; i++)
        {
            if ( mibDirList[i].getName().toLowerCase().startsWith("rfc") )
            {
                rfcList[rfcCnt] = mibDirList[i];
                rfcCnt++;
            }
            else
            {
                otherList[otherCnt] = mibDirList[i];
                otherCnt++;
            }
        }
        
        Arrays.sort(rfcList, 0, rfcCnt); // RFC dependencies USUALLY tend to be in numerical order, though this just sorts lexicographically

        for (int i = 0; i < rfcCnt; i++)
        {
            mibDirList[i] = rfcList[i];
        }
        
        int j = rfcCnt;
        for (int i = 0; i < otherCnt; i++)
        {
            mibDirList[j] = otherList[i];
            j++;
        }

        // this uses the lost children list on a global level between files
        if (lostChildren == null)
            lostChildren = new ArrayList<Object[]>();
        
        // loop through all files in the mib directory and add them to the tree
        for (File mibFile : mibDirList)
        {
            try
            {
                this.addMIBToTree(mibFile);  
            }
            catch (InvalidMibFormatException e)
            {
                System.out.println(e.getMessage());
            }
        }
        
        this.addLostChildren();  
    }


    /**
     * Adds a single MIB file to the MIB tree.
     * 
     * @param mibFile the File to add to the MIB Tree
     * 
     * @throws InvalidMibFormatException if the MIB file is invalid
     */
    public void addMIBFile(File mibFile) throws InvalidMibFormatException
    {
        // This method really is just a public wrapper for the internal implementation
        // of the MIB adding process.  It uses the lost children list on a single file
        // instead of across files.
        if (lostChildren == null)
            lostChildren = new ArrayList<Object[]>();
         
        this.addMIBToTree(mibFile);
        
        this.addLostChildren(); 
    }
    
    
    /**
     * Parses and validates a MIB file and adds its elements to the MIB tree model.
     * This method should be implemented by subclasses and it is where the actual
     * structure of the MIB file is handled.
     * 
     * @param mibFile the File to add to the MIB Tree
     * 
     * @throws InvalidMibFormatException if the MIB file is invalid
     */
    abstract protected void addMIBToTree(File mibFile) throws InvalidMibFormatException;


    /**
     * Retrieves the model representing a MIB tree structure.  If this is called before the 
     * MIB tree is compiled, a default model with only basic objects will be returned. This 
     * implementation returns a DefaultTreeModel.
     * 
     * @return a TreeModel representing the MIB object structure
     */
    public TreeModel getMibTreeModel()
    {
        return mibTreeModel;
    }
    
    
    /** 
     * @see libmib.mibtree.MibTreeBuilder#getMibFolder() 
     */
    abstract public String getMibFolder();
    
    
    /**
     * Tries to add any nodes that could not be added at the time they were created because 
     * their parents did not yet exist.
     */
    protected void addLostChildren()
    {
        // try to add the nodes whose parents may not have been added yet at the time they were created
        // note: may cause visual ordering anomalies, but it is acceptable functionally
        if (lostChildren != null && lostChildren.size() > 1)
        {
            //System.out.println("\nLOST CHILDREN:");
            for (Object[] lostChild : lostChildren)
            {
                MibTreeNode curNode = (MibTreeNode)lostChild[0];
                MibObjectType curMIBObject = (MibObjectType)curNode.getUserObject();
                String parentName = (String)lostChild[1];

                //System.out.print("\n" + curMIBObject.getName() + " --> " + nodeParent);

                // test to see if this node already exists in the tree, lost children may have been added later due to duplication
                if (!nodeMap.containsKey(curMIBObject.getName())) //check the HashMap
                {
                    // check the HashMap for specified parent node
                    if (nodeMap.containsKey(parentName))
                    {
                        MibTreeNode parent = nodeMap.get(parentName); 
                        parent.add(curNode);
                        nodeMap.put(curMIBObject.getName(), curNode);
                        //System.out.print(":    Added");
                    }
                }

            }

            lostChildren.clear();  // reset lostChildren
        }
    }
    
    
    
    /**
     * Checks whether a parent node with the given name exists in the tree
     * and adds the new MIB object to the parent if it does exist or adds it
     * to the list of lost children if it doesn't.
     * 
     * @param newObject the MIB object to add to the MIB tree
     * @param parentName the name of the MIB object's parent
     */
    protected void addMibObject(MibObjectType newObject, String parentName)
    {
        MibTreeNode newNode = new MibTreeNode(newObject);
        String name = newObject.getName();
        
        // check the HashMap for specified parent node
        if (nodeMap.containsKey(parentName))
        {
            MibTreeNode parent = nodeMap.get(parentName); 
            parent.add(newNode);
            nodeMap.put(name, newNode);
        }
        else
        {
            Object[] noParentNode = new Object[] {newNode, parentName};
            lostChildren.add(noParentNode);
            //System.out.print("Error, " + nodeParent + " not found.");
        }
    }
    
    
}

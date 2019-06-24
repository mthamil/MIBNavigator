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

package libmib.mibtree;

import java.util.Enumeration;
import java.util.List;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import utilities.iteration.adapters.IterableAdapters;

import libmib.MibObjectIdentifier;

/**
 * This extension of DefaultMutableTreeNode includes some MIB specific methods for searching, 
 * as well as a more specific constructor for using a MibObjectIdentifier as the node's user object.
 */
public class MibTreeNode extends DefaultMutableTreeNode 
{
    public enum NodeSearchOption { MatchNearestPath, MatchExactPath; }
    
    /**
     * Constructs a new default MibTreeNode.
     */
    public MibTreeNode() 
    {
        super();
    }
    
    
    /**
     * Constructs a new MibTreeNode using an object that inherits from MibObjectIdentifier 
     * as the user object.
     * 
     * @param userMIBObject the MibObjectIdentifier to set as the user object of the node
     */
    public MibTreeNode(MibObjectIdentifier userMIBObject) 
    {
        super(userMIBObject);
    }
    
    public void add(MibTreeNode node)
    {
    	MibObjectIdentifier newChild = (MibObjectIdentifier)node.getUserObject();
    	
    	String prefix = oidNumeralPath == null ? "" : oidNumeralPath + ".";
    	node.setOidNumeralPath(prefix + newChild.getId());
    	
    	this.add((MutableTreeNode)node);
    }
    
    
    /**
     * Searches for a node by it's name starting the search at this node.  A 
     * breadth first enumeration is used and is quite inefficient.
     * 
     * @param nodeName the name of the node to search for
     * @return the node if it is found or null if it is not
     */
	public MibTreeNode getNodeByName(String nodeName)
    {
        Enumeration<TreeNode> nodes = this.breadthFirstEnumeration();
        while (nodes.hasMoreElements())
        {
            MibTreeNode node = (MibTreeNode)nodes.nextElement();
            if (nodeName.equalsIgnoreCase(String.valueOf(node)))
        		return node;
        }
        
        // Return null if the node is not found.
        return null;
    }
    
    
    /**
     * Searches for a node by its OID numeral string path starting at this node.  The search goes 
     * through the children at each successive node until the correct OID has been constructed.
     * <br><br>
     * NOTE: the OID will be constructed STARTING at this node, and thus this should usually 
     * be used with the root node of a tree since local searches aren't useful with whole OIDs.  
     * However, it could be useful with partial OIDs.
     * 
     * @param oid the OID numeral path string of the node to search for
     * @param matchType indicates whether the nearest node should be returned even though 
     *        the exact node was not found.  For example, if 1.3.6.1.2.1.1.1.0 was searched for, 
     *        then 1.3.6.1.2.1.1.1 (system.sysDescr) will be returned.
     *        
     * @return the node if it is found or null if it is not
     * @throws NumberFormatException 
     */
    @SuppressWarnings("unchecked")
	public MibTreeNode getNodeByOid(String oid, NodeSearchOption matchType) throws NumberFormatException
    {    
        String[] oidArray = oid.trim().split("\\.");

        int elementIndex = 0; // the current index in oidArray
        int treeDepth = 0;  // the number of levels the search has advanced into the tree

        StringBuilder constructedOID = new StringBuilder();
        MibTreeNode node = null;
        MibTreeNode foundNode = null;
        MibObjectIdentifier mibObject = null;

        Enumeration children = this.children();

        try
        {
            boolean oidFound = false;
            while ((treeDepth < oidArray.length) && !oidFound)
            {
                boolean indexFound = false;
                while (children.hasMoreElements() && !indexFound)
                {
                    node = (MibTreeNode)children.nextElement();
                    mibObject = (MibObjectIdentifier)node.getUserObject();
    
                    // Compare the current object's id to the desired next number in the OID.
                    int id = mibObject.getId();
                    if (id == Integer.parseInt(oidArray[elementIndex]))
                    {
                        indexFound = true;
                        
                        if (elementIndex != 0)
                            constructedOID.append(".");
                        
                        constructedOID.append(id);
                        
                        children = node.children();
                        foundNode = node;
                        elementIndex++;
                    }
                }
                
                treeDepth++;

                if (constructedOID.toString().equals(oid))
                    oidFound = true;
            }
    
            if (oidFound || matchType == NodeSearchOption.MatchNearestPath)
                return foundNode;

            // Return null if the node is not found.
            return null;
        }
        catch (NumberFormatException e)
        {
            throw new NumberFormatException("Object Identifier: " + oid + " is invalid.");
        }      
    }
    
    
    /**
     * Searches for a node by its OID name string path starting at this node.  The search goes 
     * through the children at each successive node until the correct OID has been constructed.
     * <br><br>
     * NOTE: the OID will be constructed STARTING at this node, and thus this should usually 
     * be used with the root node of a tree since local searches aren't useful with whole OIDs.  
     * However, it could be useful with partial OIDs.
     * 
     * @param oid the OID name path string of the node to search for
     * @param matchType indicates whether the nearest node should be returned even though 
     *        the exact node was not found.
     *        
     * @return the node if it is found or null if it is not
     */
    @SuppressWarnings("unchecked")
	public MibTreeNode getNodeByOidName(String oid, NodeSearchOption matchType) throws NumberFormatException
    {    
        String[] oidArray = oid.trim().split("\\.");

        int elementIndex = 0;
        int treeDepth = 0;

        StringBuilder constructedOID = new StringBuilder();
        MibTreeNode node = null;
        MibTreeNode foundNode = null;
        MibObjectIdentifier mibObject = null;

        Enumeration children = this.children();

        boolean oidFound = false;
        while ( (treeDepth < oidArray.length) && !oidFound )
        {
            boolean indexFound = false;
            while (children.hasMoreElements() && !indexFound)
            {
                node = (MibTreeNode)children.nextElement();
                mibObject = (MibObjectIdentifier)node.getUserObject();

                // Compare the current object's name to the desired next name in the OID.
                String name = mibObject.getName();
                if (name.equals(oidArray[elementIndex]))
                {
                    indexFound = true;
                    
                    if (elementIndex != 0)
                        constructedOID.append(".");
                    
                    constructedOID.append(name);
                    
                    foundNode = node;
                    elementIndex++;
                }
            }

            if (constructedOID.toString().equals(oid))
                oidFound = true;
            
            // This ensures that if all children of a node have been checked and no index match was found,
            // then the search has gone as deep as it should go, and thus it should break out
            if (!indexFound)
                break; 
            
            children = node.children();

            treeDepth++;
        }

        if (oidFound || matchType == NodeSearchOption.MatchNearestPath)
            return foundNode;
        
        // Return null if the node is not found.
        return null;    
    }
    
    /**
     * Returns the full path name from the root of the tree to this node as a String. 
     * This basically simplifies what is returned from a node's getPath method.
     * 
     * @return a String containing the full path name for this node, for example: 
     *         iso.org.dod
     */
    public String getOidNamePath()
    {
        StringBuilder fullNamePath = new StringBuilder();
        TreeNode[] pathFromRoot = this.getPath();
        
        fullNamePath.append(pathFromRoot[1]);   // ignore first node (generic root)
        for (int i = 2; i < pathFromRoot.length; i++)
        	fullNamePath.append("." + pathFromRoot[i].toString());

        return fullNamePath.toString();
    }
    
    
    private String oidNumeralPath;
    
    /**
     * Returns the full numeral path from the root of the tree to this node as a String.
     * 
     * @return a String such as "1.3.6.1.1" representing a node's path from the root.
     */
    public String getOidNumeralPath() 
    { 
    	return oidNumeralPath; 
	}
    
    protected void setOidNumeralPath(String path) 
    { 
    	oidNumeralPath = path; 
	}
    
    /**
     * Returns both the OID name and numeral paths.  This returns what getOidNamePath 
     * and getOidNumeralPath do, but in a String array together.
     * 
     * @return a pair with both the full OID name and numeral paths of this node.
     *         The key is the numeral and the value is the name.
     */
    public Entry<String, String> getOidPaths()
    {
        String oidNamePath = getOidNamePath();
        
        Entry<String, String> paths = new AbstractMap.SimpleEntry<String, String>(getOidNumeralPath(), oidNamePath);
        return paths;
    }
    
}

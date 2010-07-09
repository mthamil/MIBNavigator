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

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.tree.TreeModel;

import utilities.events.EventListener;

import libmib.format.InvalidMibFormatException;


/**
 * An interface defining the basic methods required for a MIB compiler that uses a 
 * TreeModel structure to store the OID hierarchy.
 */
public interface MibTreeBuilder 
{
    /**
     * Adds all of the MIB files in the specified directory to the MIB tree model.
     * Because potentially more than one MIB is being added, dependencies between MIBs are 
     * taken into account.
     * 
     * @param mibDir the File object representing a directory containing MIB files to add 
     *        to the MIB tree.
     */
    public void addMibDirectory(File mibDir);
    
    /**
     * Adds an individual MIB file to the MIB tree.
     * 
     * @param mibFile the File object representing the MIB to add to the MIB tree.
     * 
     * @throws InvalidMibFormatException if the MIB file is not in a valid format
     */
    public void addMibFile(File mibFile) throws InvalidMibFormatException;
    
    /**
     * Retrieves the TreeModel used by the builder.
     */
    public TreeModel getTreeModel();
    
    /**
     * Gets the format-specific base folder name used by the tree builder.
     * @return the name of the tree builder's MIB directory
     */
    public String getMibDirectory();
    
    
    /**
     * Sets the file filter used by a builder when adding
     * files from a directory.
     * @param filter
     */
    public void setFileFilter(FilenameFilter filter);
    
    /**
     * Gets the file filter used by a builder when adding
     * files from a directory.
     * @return
     */
    public FilenameFilter getFileFilter();
    
    /**
     * Adds a subscriber to the event fired when a MIB is added to the tree.
     * @param listener
     */
    public void addMibAddedListener(EventListener<AbstractMibTreeBuilder, MibAddedEventInfo> listener);
    
    /**
     * Removes a subscriber from the event fired when a MIB is added to the tree.
     * @param listener
     */
    public void removeMibAddedListener(EventListener<AbstractMibTreeBuilder, MibAddedEventInfo> listener);

}

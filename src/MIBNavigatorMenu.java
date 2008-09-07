/**
 * MIB Navigator
 *
 * Copyright (C) 2005, 2006 Matt Hamilton <matthew.hamilton@washburn.edu>
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.tree.DefaultTreeModel;

import filefilters.FileFilterXml;

import libmib.InvalidMibFormatException;
import libmib.mibtree.MibTreeBuilder;



/**
 * This class manages the menu bar for the MIBNavigator application and reacts to user 
 * menu actions.
 */
public class MIBNavigatorMenu implements ActionListener
{
    private JMenuBar theMenubar;
    private JMenu optionMenu;
    private JMenuItem addMIBItem, importItem, closeItem;
    
    private FileFilterXml xmlFilter = null;
    private MIBNavigator navigator = null;
    
    
    /**
     * Creates a new MIBNavigatorMenu.
     */
    public MIBNavigatorMenu(MIBNavigator newNav)
    {
        if (newNav == null)
            throw new IllegalArgumentException("MIBNavigator cannot be null.");
            
        navigator = newNav;
        
        // Create menubar.
        theMenubar = new JMenuBar();
        optionMenu = new JMenu("Options");
        optionMenu.setMnemonic(KeyEvent.VK_O);

        addMIBItem = new JMenuItem("Add MIB");
        addMIBItem.setActionCommand("add mib");
        addMIBItem.addActionListener(this);
        optionMenu.add(addMIBItem);
        
        importItem = new JMenuItem("Import MIB");
        importItem.setActionCommand("permanently add mib");
        importItem.addActionListener(this);
        optionMenu.add(importItem);
        
        optionMenu.addSeparator();

        closeItem = new JMenuItem("Close");
        closeItem.setActionCommand("close");
        closeItem.addActionListener(this);
        optionMenu.add(closeItem);
        
        theMenubar.add(optionMenu);
        
        xmlFilter = new FileFilterXml();
    }
    
    
    /**
     * Gets the internal JMenuBar used by MIBNavigatorMenu.
     */
    public JMenuBar getMenuBar()
    {
        return theMenubar;
    }
    

    public void actionPerformed(ActionEvent event) 
    {
        String actionCommand = event.getActionCommand();
        
        if (actionCommand.equals("close"))
        {
            // Save application settings and exit.
            navigator.saveState();
            System.exit(0);
        }
        else if (actionCommand.equals("add mib"))
        {
            // Add a new MIB to the browser's tree if it is valid.

        	// Create a file chooser with the correct filter for MIB files.
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setFileFilter(xmlFilter);
            
            JRootPane menuParentFrame = theMenubar.getRootPane();  // use the root pane's parent frame to launch dialogs
            int returnValue = chooser.showOpenDialog(menuParentFrame);

            if (returnValue == JFileChooser.APPROVE_OPTION)
            {
                File mib = chooser.getSelectedFile();
                MibTreeBuilder treeBuilder = navigator.getBrowser().getMibBuilder();
                try
                {
                    treeBuilder.addMIBFile(mib);
                    
                    // If the MIB isn't valid, it will not be added and there will be no reason to reload.
                    ((DefaultTreeModel)treeBuilder.getMibTreeModel()).reload();
                }
                catch (InvalidMibFormatException e)
                {
                    JOptionPane.showMessageDialog(menuParentFrame, e.getMessage(), "MIB File Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (actionCommand.equals("permanently add mib"))
        {
            // Try to add a new MIB to the browser's tree, and if it is valid, copy it into the default MIBs directory.

            // Create a file chooser with the correct filter for MIB files.
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setFileFilter(xmlFilter);
            
            JRootPane menuParentFrame = theMenubar.getRootPane();  // Use the root pane's parent frame to launch dialogs
            int returnValue = chooser.showOpenDialog(menuParentFrame);

            if (returnValue == JFileChooser.APPROVE_OPTION)
            {
                File sourceMib = chooser.getSelectedFile();
                MibTreeBuilder treeBuilder = navigator.getBrowser().getMibBuilder();
                File mibDirectory = navigator.getBrowser().getMibDirectory();

                try
                {
                    treeBuilder.addMIBFile(sourceMib);
                    
                    // If the MIB isn't valid, it will not be added and there will be no reason to reload.
                    ((DefaultTreeModel)treeBuilder.getMibTreeModel()).reload();  
                    
                    File destinationMib = new File(mibDirectory.getPath() + File.separator + sourceMib.getName());
                    
                    boolean proceedWithCopy = true;
                    if (destinationMib.exists())
                    {
                        // ask the user if they want to overwrite the file if it already exists
                        String popupMsg = destinationMib.getName() + " already exists in the \"" 
                        	+ mibDirectory.getName()  + "\" directory." + "  Overwrite existing MIB file?";
                        
                        int confirmValue = JOptionPane.showConfirmDialog(menuParentFrame, popupMsg ,"MIB File Already Exists", 
                                JOptionPane.YES_NO_OPTION);
                        
                        if (confirmValue == JOptionPane.NO_OPTION)
                            proceedWithCopy = false;
                    }

                    // if the destination file didn't already exist or the user approved an overwrite
                    if (proceedWithCopy)
                       fileCopy(sourceMib, destinationMib);
                    
                }
                catch (IOException e)
                {
                    JOptionPane.showMessageDialog(menuParentFrame, e.getMessage(), "Error Copying File", JOptionPane.ERROR_MESSAGE);
                }
                catch (InvalidMibFormatException e)
                {
                    JOptionPane.showMessageDialog(menuParentFrame, e.getMessage(), "MIB File Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
    }
    
    /**
     * Simply copies a file from one location to another.
     * 
     * @param sourceFile the source file to copy
     * @param destFile the destination of the copy operation
     * @throws IOException
     */
    private void fileCopy(File sourceFile, File destFile) throws IOException
    {
        InputStream in = new FileInputStream(sourceFile);
        OutputStream out = new FileOutputStream(destFile);
    
        // Transfer the file as raw bytes from in to out.
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > 0) 
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }
    
    
}

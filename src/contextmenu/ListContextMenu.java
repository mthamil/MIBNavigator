/**
 * MIB Navigator
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

package contextmenu;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * This class represents a popup menu containing common context-relevant actions such
 * as copy and select all for JLists.
 */
public class ListContextMenu extends JPopupMenu
                         //implements ActionListener
{
    private JMenuItem copyItem, selectAllItem;
    private JList menuSource;
    
    public ListContextMenu()
    {
        ListMenuItemHandler menuItemHandler = new ListMenuItemHandler();
        
        copyItem = new JMenuItem("Copy");
        copyItem.setActionCommand("copy");
        copyItem.addActionListener(menuItemHandler);
        this.add(copyItem);

        selectAllItem = new JMenuItem("Select All");
        selectAllItem.setActionCommand("select all");
        selectAllItem.addActionListener(menuItemHandler);
        this.add(selectAllItem);
    }
    
    /**
     * When this method is invoked, the ListContextMenu is displayed at the 
     * coordinates of the source JList with context appropriate options.
     * These actions are disabled/enabled/hidden depending on the list's
     * status.
     */
    public void show(JList source, int x, int y)
    {
        source.requestFocusInWindow();
        menuSource = source;
        
        //disable select all if there are no items in the list
        if (menuSource.getModel().getSize() > 0)
            selectAllItem.setEnabled(true);
        else
            selectAllItem.setEnabled(false);
        
        
        //disable copy if no items are selected
        if (!menuSource.isSelectionEmpty())    
            copyItem.setEnabled(true);
        else
            copyItem.setEnabled(false);
        
        super.show(menuSource, x, y);
    }


    /**
     * This ActionListener is notified when the user has selected one of the ListContextMenu options.
     */
    private class ListMenuItemHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent event) 
        {
            String actionCommand = event.getActionCommand();
            
            if (actionCommand.equals("copy"))
            {
                //Get the selected items and put the results of their toString() methods
                //on the system clipboard.
                menuSource.requestFocusInWindow();
                Object[] selectedItems = menuSource.getSelectedValues();
                
                if (selectedItems.length > 0)
                {
                    StringBuilder selectString = new StringBuilder();
                    
                    for (Object selectedItem : selectedItems)
                        selectString.append(selectedItem.toString() + "\n");  
                    
                    StringSelection ss = new StringSelection(selectString.toString());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
                }
            }
            else if (actionCommand.equals("select all"))
            {
                //select all rows of the JList
                menuSource.requestFocusInWindow();
                menuSource.clearSelection();
                int listSize = menuSource.getModel().getSize();
                menuSource.setSelectionInterval(0, listSize - 1);
            }
        }
        
    }

}

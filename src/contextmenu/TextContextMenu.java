/**
 * MIB Navigator
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

package contextmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * This class represents a popup menu containing common context-relevant actions such
 * as copy, paste, and select all for JTextComponents.
 */
public class TextContextMenu extends JPopupMenu
{
    private JMenuItem copyItem, pasteItem, selectAllItem;
    private JTextComponent menuSource;
    
    public TextContextMenu()
    {
        TextMenuItemHandler menuItemHandler = new TextMenuItemHandler();
        
        copyItem = new JMenuItem("Copy");
        copyItem.setActionCommand("copy");
        copyItem.addActionListener(menuItemHandler);
        this.add(copyItem);

        pasteItem = new JMenuItem("Paste");
        pasteItem.setActionCommand("paste");
        pasteItem.addActionListener(menuItemHandler);
        this.add(pasteItem);

        selectAllItem = new JMenuItem("Select All");
        selectAllItem.setActionCommand("select all");
        selectAllItem.addActionListener(menuItemHandler);
        this.add(selectAllItem);
    }
    
    
    /**
     * When this method is invoked, the TextContextMenu is displayed at the 
     * coordinates of the source component with context appropriate options.
     * These actions are disabled/enabled/hidden depending on the text 
     * component's status.
     */
    public void show(JTextComponent source, int x, int y)
    {
        source.requestFocusInWindow();
        menuSource = source;

        // Only show the paste option for editable fields.
        if (menuSource.isEditable())
            pasteItem.setVisible(true);
        else
            pasteItem.setVisible(false);

        //Disable select all if no visible text in the text component.
        //getText is not sufficient for styled text components, so the Document must be 
        //accessed directly.
        Document doc = menuSource.getDocument();
        String plainText = "";
        try 
        {
            plainText = doc.getText(0, doc.getLength()).trim();
        } 
        catch (BadLocationException e) 
        {
            //What should I even do here?  Bad Location my ass.
            System.out.println(e.getMessage() + ":" + e.offsetRequested());
        }

        if (plainText.equals(""))
            selectAllItem.setEnabled(false);
        else
            selectAllItem.setEnabled(true);
        

        //disable copy if no text is selected
        if (menuSource.getSelectedText() == null)
            copyItem.setEnabled(false);
        else
            copyItem.setEnabled(true);

        super.show(menuSource, x, y);
    }


    /**
     * This ActionListener is notified when the user has selected one of the TextContextMenu options.
     */
    private class TextMenuItemHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent event) 
        {
            String actionCommand = event.getActionCommand();
            
            if (actionCommand.equals("copy"))
            {
                // Copy the text component's selected text to the clipboard.
                menuSource.requestFocusInWindow();
                menuSource.copy();
            }
            else if (actionCommand.equals("paste"))
            {           
                // Paste from the clipboard into the text component.
                menuSource.requestFocusInWindow();
                menuSource.paste();
            }
            else if (actionCommand.equals("select all"))
            {
                // Select the entire text component's contents.
                menuSource.requestFocusInWindow();
                menuSource.selectAll();
            }
        }
        
    }

}

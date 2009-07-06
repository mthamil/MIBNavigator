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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import contextmenu.ContextMenuResources.Keys;

/**
 * This class represents a popup menu containing common context-relevant actions such
 * as copy, paste, and select all for JTextComponents.
 */
public class TextContextMenu extends JPopupMenu
{
    private Action copyAction, pasteAction, selectAllAction;
    private JTextComponent menuSource;
    
    public TextContextMenu()
    {       
        copyAction = new AbstractAction(ContextMenuResources.getString(Keys.CopyLabel))
        {
			public void actionPerformed(ActionEvent e)
			{
				// Copy the text component's selected text to the clipboard.
                menuSource.requestFocusInWindow();
                menuSource.copy();
			}
        };
        this.add(copyAction);

        pasteAction = new AbstractAction(ContextMenuResources.getString(Keys.PasteLabel))
        {
			public void actionPerformed(ActionEvent e)
			{
				 // Paste from the clipboard into the text component.
                menuSource.requestFocusInWindow();
                menuSource.paste();
			}
        	
        };
        this.add(pasteAction);

        selectAllAction = new AbstractAction(ContextMenuResources.getString(Keys.SelectAllLabel))
        {
			public void actionPerformed(ActionEvent e)
			{
                // Select the entire text component's contents.
                menuSource.requestFocusInWindow();
                menuSource.selectAll();
			}
        };
    	this.add(selectAllAction);
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
            pasteAction.setEnabled(true);
        else
            pasteAction.setEnabled(false);

        // Disable select all if no visible text in the text component.
        // getText is not sufficient for styled text components, so the Document must be 
        // accessed directly.
        Document doc = menuSource.getDocument();
        String plainText = "";
        try 
        {
            plainText = doc.getText(0, doc.getLength()).trim();
        } 
        catch (BadLocationException e) 
        {
            // What should I even do here?  Bad Location my ass.
            System.out.println(e.getMessage() + ":" + e.offsetRequested());
        }

        if (plainText.equals(""))
            selectAllAction.setEnabled(false);
        else
            selectAllAction.setEnabled(true);
        

        //disable copy if no text is selected
        if (menuSource.getSelectedText() == null)
            copyAction.setEnabled(false);
        else
            copyAction.setEnabled(true);

        super.show(menuSource, x, y);
    }

}

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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.text.JTextComponent;

/**
 * When JTextComponents that have registered this listener trigger a 
 * popup event (such as a right click), a popup menu is displayed
 * at the coordinates of the invoking component.
 */
public class TextContextMenuListener extends MouseAdapter 
{
    TextContextMenu popup;

    public TextContextMenuListener(TextContextMenu popupMenu)
    {
        popup = popupMenu;
    }

    public void mousePressed(MouseEvent event)
    {
        showPopup(event);
    }

    public void mouseReleased(MouseEvent event)
    {
        showPopup(event);
    }

    private void showPopup(MouseEvent event)
    {
        if (event.isPopupTrigger())
        {
            JTextComponent origin = (JTextComponent)event.getComponent();
            popup.show(origin, event.getX(), event.getY());
        }
    }
            
}

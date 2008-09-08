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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextField;

import libmib.mibtree.MibTreeNode.NodeSearchOption;


/**
 * Action for performing a search of a MIB tree based on an
 * OID string in a text field.
 */
public class MibTreeSearchAction extends AbstractAction
{
	private MibBrowser browser;
	
	public MibTreeSearchAction(MibBrowser browser)
	{
		this.browser = browser;
	}
	
    public void actionPerformed(ActionEvent event)
    {
    	if (event.getSource() instanceof JTextField)
    	{
			JTextField textField = (JTextField)event.getSource();
		    try
		    {
		        browser.setVisibleNodeByOID(textField.getText().trim(), NodeSearchOption.MATCH_EXACT_PATH);
		    }
		    catch (NumberFormatException e)
		    {
		        // just do nothing, I may add an "OID not found" message later
		    }
    	}
    }
}

/**
 * MIB Navigator
 *
 * Copyright (C) 2009, Matt Hamilton <matthamilton@live.com>
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


import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import libmib.mibtree.MibTreeNode.NodeSearchOption;

public class ResultsListListener implements ListSelectionListener
{
	private MibBrowser browser;
	
	public ResultsListListener(MibBrowser browser)
	{
		this.browser = browser;
	}
	
    /**
     * ListSelectionListener implementation method: reacts to changes in the selection on a JList.
     * When the user selects a row or more, the OID of the selected row with the lowest index is 
     * retrieved, searched for in the MIB tree, and then displayed.
     */
    public void valueChanged(ListSelectionEvent selectEvent) 
    {
        // The IsAdjusting check is to make sure the code that occurs on a selection value
        // change does not run twice.
        // The second time valueChanged is called is when a new row has been selected, the first time
        // is when the previous selections are removed.  The second time valueChanged is called,
        // this value is false.
        if (!selectEvent.getValueIsAdjusting())
        {
            JList source = (JList)selectEvent.getSource();
            int selectedIndex = source.getSelectedIndex();
            
            if (selectedIndex > -1)
            {
                Object selectedObject = source.getModel().getElementAt(selectedIndex);
                
                try
                {
                    if (selectedObject instanceof GetRequestResult)
                    {
                        String selectedOID = ((GetRequestResult)selectedObject).getOIDNumber();
                        browser.setVisibleNodeByOID(selectedOID, NodeSearchOption.MatchNearestPath);
                    }
                }
                // Catch bad OIDs, though this is very unlikely if the OID is in the results list.
                catch (NumberFormatException e) 
                {
                    // do nothing
                    // They say it is horrible to have empty catch blocks but I do not care when this happens!
                }
            }
        }
        
    }
}
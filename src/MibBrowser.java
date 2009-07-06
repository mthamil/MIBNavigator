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

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import utilities.Utilities;

import libmib.MibObjectType;
import libmib.mibtree.MibTreeBuilder;
import libmib.mibtree.MibTreeNode;
import libmib.mibtree.MibTreeNode.NodeSearchOption;
import contextmenu.*;


/**
 *  MibBrowser creates and manages the graphical interface for the MIB Navigator 
 *  application. 
 */
public class MibBrowser
{
    private JPanel browserPanel;
    
    private JTree mibTree;
    private DefaultTreeModel mibModel;
    private MibTreeBuilder treeBuilder;
    private JScrollPane mibTreeScroll;
    
    private OidDataPanel oidViewer;

    private JLabel oidNameLabel, oidNumberLabel, addressLabel, communityLabel, portLabel, timeoutLabel, oidInputLabel;
    private JTextField oidNameField, oidNumberField, resolvedAddrField, communityField, portField, timeoutField, oidInputField; 
    private JComboBox addressBox;
    
    private JLabel resultsLabel;
    private JScrollPane resultsScroll;
    private JList resultsList;
    
    private JButton getButton;
    
    private Color backgroundColor;

    private StringBuilder currentOidName;
    private StringBuilder currentOidNumber;
    
    private File mibDirectory;

    /**
     * Creates a new MibBrowser that uses the given MibTreeBuilder to manage its MIB tree.
     * 
     * @param newBuilder the MibTreeBuilder used to manage the MIB tree
     * 
     * @throws IllegalArgumentException if newBuilder is null
     */
    public MibBrowser(MibTreeBuilder newBuilder, File newMibDirectory)
    {
        if (newBuilder == null)
            throw new IllegalArgumentException("MIB tree builder cannot be null.");
        
        treeBuilder = newBuilder;
        
        currentOidName = new StringBuilder();
        currentOidNumber = new StringBuilder();
        
        mibDirectory = newMibDirectory;

        initializeComponents();
        layoutComponents(); 
    }
    

    /**
     * Initializes the MibBrowser interface components.
     */
    private void initializeComponents()
    {
        browserPanel = new JPanel();

        oidViewer = new OidDataPanel(); 
        
        TextContextMenu contextMenu = new TextContextMenu();
		TextContextMenuListener contextMenuListener = new TextContextMenuListener(contextMenu);
        
		// OID info
        oidNameLabel = new JLabel(Resources.getString("oidNameLabel"));
        backgroundColor = oidNameLabel.getBackground(); //this is for look and feel purposes
        oidNumberLabel = new JLabel(Resources.getString("oidNumberLabel"));

        oidNameField = new JTextField(37);
        oidNameField.setEditable(false);
        oidNameField.setBackground(backgroundColor);
        oidNameField.addMouseListener(contextMenuListener);

        oidNumberField = new JTextField(37);
        oidNumberField.setEditable(false);
        oidNumberField.setBackground(backgroundColor);
        oidNumberField.addMouseListener(contextMenuListener);
        
        
		// Host info
		addressLabel = new JLabel(Resources.getString("ipAddressLabel"));
        addressBox = new JComboBox();
        addressBox.setMaximumRowCount(15);
        addressBox.setEditable(true);
        Dimension addressSize = new Dimension(113, 20);
        addressBox.setPreferredSize(addressSize);
        addressBox.setMaximumSize(addressSize);
        
        // Add a context menu to the combo box's editor component.
        ComboBoxEditor editor = addressBox.getEditor();
        Component comp = editor.getEditorComponent();
        comp.addMouseListener(contextMenuListener);

        resolvedAddrField = new JTextField(17);
        resolvedAddrField.setEditable(false);
        resolvedAddrField.setHorizontalAlignment(JTextField.CENTER);
        resolvedAddrField.setBackground(backgroundColor);
        resolvedAddrField.addMouseListener(contextMenuListener);

        communityLabel = new JLabel(Resources.getString("communityStringLabel"));
        communityField = new JTextField(12);
        communityField.setText("public");
        communityField.setEditable(true);
        communityField.addMouseListener(contextMenuListener);
        
        portLabel = new JLabel(Resources.getString("portLabel"));
        portField = new JTextField(4);
        portField.setText("161");
        portField.addMouseListener(contextMenuListener);
        
        timeoutLabel = new JLabel(Resources.getString("timeoutLabel"));
        timeoutField = new JTextField(4);
        timeoutField.setText("4000");
        timeoutField.addMouseListener(contextMenuListener);

        oidInputLabel = new JLabel(Resources.getString("oidInputLabel"));
        oidInputField = new JTextField(21);
        oidInputField.setText("");
        oidInputField.setEditable(true);
        oidInputField.addMouseListener(contextMenuListener);
        oidInputField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "treeSearch");
        oidInputField.getActionMap().put("treeSearch", new MibTreeSearchAction(this)); 

        ListContextMenu listContextMenu = new ListContextMenu();
        ListContextMenuListener listContextMenuListener = new ListContextMenuListener(listContextMenu);
        
		resultsLabel = new JLabel(Resources.getString("resultsLabel")); 
        resultsList = new JList(new DefaultListModel());
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        resultsList.addMouseListener(listContextMenuListener);
        resultsList.addListSelectionListener(new ResultsListListener(this));
        
        resultsScroll = new JScrollPane(resultsList);
		resultsScroll.setPreferredSize(new Dimension(50, 75));

        getButton = new JButton();
        getButton.setPreferredSize(new Dimension(80, 25));
        getButton.setAction(new GetRequestAction());
        getButton.setMnemonic(KeyEvent.VK_G);


        // Configure the MIB tree.
        try
        {
            treeBuilder.addMibDirectory(mibDirectory);
        }
        catch (IllegalArgumentException e) // if the default directory doesn't exist
        {
            System.out.print(e.getMessage());
        }
        
        // If the mibs directory wasn't found, this will return a tree model with only default nodes.
        mibModel = (DefaultTreeModel)treeBuilder.getTreeModel();

        mibTree = new JTree();
        mibTree.setModel(mibModel); 
        mibTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        mibTree.addTreeSelectionListener(new MibTreeListener());
        mibTree.addTreeSelectionListener(oidViewer);
        mibTree.setRootVisible(false);
                
        DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();
        cellRenderer.setHorizontalAlignment(DefaultTreeCellRenderer.CENTER);
        
        // Don't display any node icons.
        cellRenderer.setLeafIcon(null);
        cellRenderer.setOpenIcon(null);
        cellRenderer.setClosedIcon(null);
        
        //cellRenderer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        mibTree.setCellRenderer(cellRenderer);
        mibTree.setShowsRootHandles(true);
        mibTree.setSelectionRow(0);         // automatically select the first visible node

		// Add the tree to a scroll pane.
		mibTreeScroll = new JScrollPane(mibTree);
		mibTreeScroll.setPreferredSize(new Dimension(400, 180));
    }


    /**
     * Lays out and arranges the interface components.
     */
    private void layoutComponents()
    {
		// set up the layout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints cons = new GridBagConstraints();
		Insets ins = new Insets(2, 2, 2, 0);
		cons.insets = ins;
		//cons.gridwidth = 1;
		//cons.gridheight = 1;

		// TREE PANEL
		JPanel treePanel = new JPanel();
		treePanel.setLayout(layout);

		cons.gridx = 0;
		cons.gridy = 0;
		cons.weightx = .5;
		cons.weighty = .5;
		cons.fill = GridBagConstraints.BOTH;
		cons.anchor = GridBagConstraints.FIRST_LINE_START;
		layout.setConstraints(mibTreeScroll, cons);
		treePanel.add(mibTreeScroll);


		// TREE AND OID DETAILS CONTAINING PANEL
		JPanel topPanel = new JPanel();
		topPanel.setLayout(layout);

		cons.gridx = 0;
		cons.gridy = 0;
        cons.weightx = .65;
        cons.weighty = .65;
		cons.fill = GridBagConstraints.BOTH;
		layout.setConstraints(treePanel, cons);
		topPanel.add(treePanel);

		cons.gridx = 1;
		cons.gridy = 0;
        cons.weightx = .35;
        cons.weighty = .35;
		layout.setConstraints(oidViewer.getPanel(), cons);
		topPanel.add(oidViewer.getPanel());


		// OID NUMBER AND NAME PANEL
		JPanel oidPanel = new JPanel();
		oidPanel.setLayout(layout);

		ins.set(2, 0, 2, 0);
		cons.weightx = 0;
		cons.weighty = 0;
		cons.anchor = GridBagConstraints.LINE_START;

		cons.fill = GridBagConstraints.NONE;
		cons.gridx = 0;
		cons.gridy = 0;
		layout.setConstraints(oidNameLabel, cons);
		oidPanel.add(oidNameLabel);

		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.gridx = 1;
		cons.gridy = 0;
		layout.setConstraints(oidNameField, cons);
		oidPanel.add(oidNameField);

		cons.fill = GridBagConstraints.NONE;
		cons.gridx = 0;
		cons.gridy = 1;
		layout.setConstraints(oidNumberLabel, cons);
		oidPanel.add(oidNumberLabel);

		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.gridx = 1;
		cons.gridy = 1;
		layout.setConstraints(oidNumberField, cons);
		oidPanel.add(oidNumberField);
        
        
		// TIMEOUT/PORT PANEL
        JPanel hostSubPanel = new JPanel();
        hostSubPanel.setLayout(layout);
        
        ins.set(2, 2, 2, 2);
        cons.gridx = 0;
        cons.gridy = 0;
        layout.setConstraints(portLabel, cons);
        hostSubPanel.add(portLabel);
        
        cons.gridx = 1;
        cons.gridy = 0;
        layout.setConstraints(portField, cons);
        hostSubPanel.add(portField);
        
        cons.gridx = 2;
        cons.gridy = 0;
        layout.setConstraints(timeoutLabel, cons);
        hostSubPanel.add(timeoutLabel);
        
        cons.gridx = 3;
        cons.gridy = 0;
        layout.setConstraints(timeoutField, cons);
        hostSubPanel.add(timeoutField);


		// HOST DETAILS PANEL
		JPanel hostPanel = new JPanel();
		hostPanel.setLayout(layout);

		cons.weightx = 0;
		cons.weighty = 0;
		cons.anchor = GridBagConstraints.LINE_START;
		cons.fill = GridBagConstraints.NONE;

		cons.gridx = 0;
		cons.gridy = 0;
		layout.setConstraints(addressLabel, cons);
		hostPanel.add(addressLabel);

		cons.gridx = 1;
		cons.gridy = 0;
		layout.setConstraints(addressBox, cons);
		hostPanel.add(addressBox);

        cons.gridx = 2;
        cons.gridy = 0;
        layout.setConstraints(resolvedAddrField, cons);
        hostPanel.add(resolvedAddrField);

		cons.gridx = 0;
		cons.gridy = 1;
		layout.setConstraints(communityLabel, cons);
		hostPanel.add(communityLabel);

		cons.gridx = 1;
		cons.gridy = 1;
		layout.setConstraints(communityField, cons);
		hostPanel.add(communityField);
        
        cons.gridx = 2;
        cons.gridy = 1;
        layout.setConstraints(hostSubPanel, cons);
        hostPanel.add(hostSubPanel);
        

		// OID NUMBER/NAME AND HOST DETAILS CONTAINER PANEL
		JPanel topMidPanel = new JPanel();
		topMidPanel.setLayout(layout);

		cons.gridx = 0;
		cons.gridy = 0;
		layout.setConstraints(oidPanel, cons);
		topMidPanel.add(oidPanel);

		cons.gridx = 1;
		cons.gridy = 0;
		layout.setConstraints(hostPanel, cons);
		topMidPanel.add(hostPanel);

		// BUTTONS PANEL
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(layout);

		cons.gridx = 0;
		cons.gridy = 0;
		layout.setConstraints(getButton, cons);
		buttonPanel.add(getButton);

		// OID SEARCH/SELECTION PANEL
		JPanel oidSearchPanel = new JPanel();
		oidSearchPanel.setLayout(layout);

		cons.gridx = 0;
		cons.gridy = 0;
		layout.setConstraints(oidInputLabel, cons);
		oidSearchPanel.add(oidInputLabel);

		cons.gridx = 1;
		cons.gridy = 0;
		layout.setConstraints(oidInputField, cons);
		oidSearchPanel.add(oidInputField);

		// BUTTON AND SEARCH/SELECTION CONTAINING PANEL
		JPanel bottomMidPanel = new JPanel();
		bottomMidPanel.setLayout(layout);

		cons.gridx = 0;
		cons.gridy = 0;
		layout.setConstraints(buttonPanel, cons);
		bottomMidPanel.add(buttonPanel);

		cons.gridx = 1;
		cons.gridy = 0;
		layout.setConstraints(oidSearchPanel, cons);
		bottomMidPanel.add(oidSearchPanel);

		// RESULTS PANEL
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(layout);

		cons.anchor = GridBagConstraints.LINE_START;
		cons.gridx = 0;
		cons.gridy = 0;
		layout.setConstraints(resultsLabel, cons);
		bottomPanel.add(resultsLabel);

		cons.weightx = .75;
		cons.weighty = .75;
		cons.fill = GridBagConstraints.BOTH;
		cons.gridheight = GridBagConstraints.REMAINDER;
		cons.gridx = 0;
		cons.gridy = 1;
		layout.setConstraints(resultsScroll, cons);
		bottomPanel.add(resultsScroll);


		// Configure and add panels to the master browser panel.
        browserPanel.setLayout(layout);
        browserPanel.setBackground(backgroundColor);

		ins.set(2, 0, 2, 0);
		cons.gridheight = 1;
		cons.anchor = GridBagConstraints.LINE_START;
		cons.fill = GridBagConstraints.BOTH;
		cons.gridx = 0;
		cons.gridy = 0;
        cons.weightx = .30;
        cons.weighty = .30;
		layout.setConstraints(topPanel, cons);
        browserPanel.add(topPanel);

		cons.anchor = GridBagConstraints.LINE_START;
		cons.fill = GridBagConstraints.NONE;
		cons.gridx = 0;
		cons.gridy = 1;
        cons.weightx = .0;
        cons.weighty = .0;
		layout.setConstraints(topMidPanel, cons);
        browserPanel.add(topMidPanel);

		cons.anchor = GridBagConstraints.LINE_START;
		cons.gridx = 0;
		cons.gridy = 2;
        cons.weightx = .0;
        cons.weighty = .0;
		layout.setConstraints(bottomMidPanel, cons);
        browserPanel.add(bottomMidPanel);

		cons.anchor = GridBagConstraints.LINE_START;
		cons.fill = GridBagConstraints.BOTH;
		ins.set(3, 0, 3, 0);
		cons.insets = ins;
		cons.weightx = .5;
		cons.weighty = .5;
		cons.gridx = 0;
		cons.gridy = 3;
		layout.setConstraints(bottomPanel, cons);
        browserPanel.add(bottomPanel);
	}
    
    
    // *** Start of MibBrowser data access methods. ***
    
    /**
     * Gets the directory where MIB files are contained.
     */
    public File getMibDirectory()
    {
    	return mibDirectory;
    }
    
    /**
     * Sets the directory where MIB files are contained.
     */
    public void setMibDirectory(File newMibDirectory)
    {
    	mibDirectory = newMibDirectory;
    }
    
    
    /**
     * Gets the internal JPanel created and used by MibBrowser.
     * 
     * @return the internal JPanel of MibBrowser
     */
    public JPanel getBrowserPanel()
    {
        return browserPanel;
    }
    
    
    /**
     * Gets a reference to the MibTreeBuilder used by MibBrowser to manage MIBs in the tree.
     * 
     * @return the MibTreeBuilder used by MibBrowser
     */
    public MibTreeBuilder getMibBuilder()
    {
        return treeBuilder;
    }
    
    
    /**
     * Gets the browser's list of IP addresses.  It returns a copy of the address combo box's 
     * data model in List form.  External modifications to the List after retrieving it from 
     * MibBrowser will not change MibBrowser's address combo box.
     */
    public List<String> getAddresses()
    {
        int ipCount = addressBox.getItemCount();
        List<String> addresses = new ArrayList<String>(ipCount);
        
        for (int i = 0; i < ipCount; i++)
        	addresses.add((String)addressBox.getItemAt(i));

        return addresses;
    }
    
    /**
     * Sets the browser's list of IP addresses.  The address list is used to create the address
     * combo box's data model.  External modifications to newAddressList after passing it to 
     * MibBrowser will not change MibBrowser's address combo box.
     */
    public void setAddresses(List<String> newAddresses)
    {
        addressBox.setModel(new DefaultComboBoxModel(newAddresses.toArray()));
    }
    
    // *** End of MibBrowser data access methods. ***
    
    
    /**
     * Searches for a node by a specified OID string such as 1.3.6.1.1, etc., and then sets 
     * the selected node in the JTree to that node. Note that in the event that the OID is 
     * found in the tree, a TreeSelectionListener valueChanged event will be triggered.
     * 
     * @param oidString the OID path string to search for
     * @param matchType determines whether to match the closest path or the exact path
     */
	public void setVisibleNodeByOID(String oidString, NodeSearchOption matchType) throws NumberFormatException
    {
        MibTreeNode root = (MibTreeNode)mibModel.getRoot(); //get the root node to search from the root
        MibTreeNode testNode = root.getNodeByOid(oidString, matchType);
        //MibTreeNode testNode = root.getNodeByOidName(oidString, matchType);

        if (testNode != null)
        {
            TreePath nodePath = new TreePath(testNode.getPath());
            
            mibTree.setSelectionPath(nodePath);
            mibTree.scrollPathToVisible(nodePath);
        }
    }
    

    private class GetRequestAction extends AbstractAction
    {   	
    	private boolean requestStarted;
    	private GetRequestTask snmpGetWorker = null;
    	
    	public GetRequestAction()
    	{
    		putValue(NAME, Resources.getString("getButton"));
    	}
    	
		/**
		 * ActionListener implementation method: reacts to menu and button actions, which are usually 
	     * clicks or the pressing of the Enter key when a button has focus.
	     * 
	     * @param event the ActionEvent generated when a component's default action occurs
	     */
		public void actionPerformed(ActionEvent event)
		{  
		    // Spawn a new GetRequestWorker thread for retrieving data from a device running an SNMP agent.
            
            if (getValue(NAME).equals(Resources.getString("getButton")))  //getButton.getText().equals(GET_START_LABEL))
            {
                // This check is just to avoid even attempting to use an empty OID or IP address field.
                if (!oidInputField.getText().trim().equals("")
                    && addressBox.getSelectedItem() != null
                    && !(((String)addressBox.getSelectedItem()).trim().equals("")) )
                {
                    DefaultListModel resultsListModel = (DefaultListModel)resultsList.getModel();
                    
                    try
                    {
                        // Get all necessary values from the user interface before starting the Get process.
                        // This ensures that the user can't affect what values the thread uses once it has 
                        // been launched.
                        String communityString = communityField.getText().trim();
                        String addressString = ((String)addressBox.getSelectedItem()).trim();
                        int port = Integer.parseInt(portField.getText().trim());
                        int timeout = Integer.parseInt(timeoutField.getText().trim());
    
                        // Try to scroll to the correct OID.
                        String oidInputString = Utilities.trim(oidInputField.getText().trim(), '.');
                        String oidTreeNumberString = oidNumberField.getText();
                        
                        if (!oidInputString.equals(oidTreeNumberString))
                            setVisibleNodeByOID(oidInputString, NodeSearchOption.MatchExactPath);
    
                        resultsListModel.removeAllElements();
                        putValue(NAME, Resources.getString("stopButton"));
                        
                        // Initialize and start the GetRequest process in a different thread using a SwingWorker.
//                        snmpGetWorker = new GetRequestWorker(communityString, oidInputString, addressString,
//                                (MibTreeNode)mibModel.getRoot());
//                        snmpGetWorker.addGetRequestListener(new MibGetRequestListener());
//                        snmpGetWorker.setPort(port);
//                        snmpGetWorker.setTimeout(timeout);
                        
                        snmpGetWorker = new GetRequestTask(communityString, oidInputString, addressString,
                              (MibTreeNode)mibModel.getRoot());
						snmpGetWorker.addGetRequestListener(new MibGetRequestListener());
						snmpGetWorker.setPort(port);
						snmpGetWorker.setTimeout(timeout);
						snmpGetWorker.addPropertyChangeListener(new PropertyChangeListener()
						{
							public void propertyChange(PropertyChangeEvent propertyEvent)
							{
								if ("state".equals(propertyEvent.getPropertyName()) && 
									javax.swing.SwingWorker.StateValue.DONE == propertyEvent.getNewValue()) 
								{
									getButton.getAction().putValue(GetRequestAction.NAME, Resources.getString("getButton"));
								}
							}
						});
						
						snmpGetWorker.setResultProcessor(new GetRequestResultProcessor()
						{
							@Override
							public void processResult(GetRequestResult result)
							{
								Action getAction = getButton.getAction();
						    	if (!getAction.isEnabled())
						    		getAction.setEnabled(true);
						    	
						        ((DefaultListModel)resultsList.getModel()).addElement(result);
							}
						});
                        
                        // Disable the Get button because clicking Stop will not do anything
                        // while waiting for the timeout.
                        this.setEnabled(false);
                        
                        //snmpGetWorker.start();
                        snmpGetWorker.execute();
                    }
                    catch (NumberFormatException e)
                    {
                        resultsListModel.removeAllElements();
                        resultsListModel.addElement(Resources.getString("badOidInputMessage") + e.getMessage() + "\n");
                    } 
                }
            }
            else
            {
                if (snmpGetWorker != null)
                    snmpGetWorker.cancel(true);  // stop the Get process
            }
		}
    }

    
	/**
	 * Class that listens for and responds to MIB tree node
	 * selection changes.
	 */
	private class MibTreeListener implements TreeSelectionListener
	{
		/**
	     * TreeSelectionListener implementation method: reacts to tree node selections 
	     * (ie. user clicks a node)
	     * 
	     * @param event the TreeSelectionEvent generated by a node selection change
		 */
		public void valueChanged(TreeSelectionEvent event)
		{
			// Reset the string buffers; these buffers are continuously reused to avoid 
	        // always creating new ones.
			currentOidName.delete(0, currentOidName.length());
			currentOidNumber.delete(0, currentOidNumber.length());

			Object[] path = event.getPath().getPath();
			MibTreeNode node;
			MibObjectType mibObject;
			
			// Construct the MIB object name and OID path strings from the object path 
	        // array; start at 1 to exclude the root.
			for (int i = 1; i < path.length; i++)
			{
				node = (MibTreeNode)path[i];
				mibObject = (MibObjectType)node.getUserObject();
	
				// Don't put a '.' at the beginning.
				if (i > 1)
				{
					currentOidName.append(".");
					currentOidNumber.append(".");
				}
	
				currentOidName.append(mibObject.getName());
				currentOidNumber.append(String.valueOf(mibObject.getId()));
			}
	
			// Set the OID name and number strings.
			oidNameField.setText(currentOidName.toString());
			oidNumberField.setText(currentOidNumber.toString());
	
	        if (!oidInputField.getText().trim().equals(oidNumberField.getText().trim()))
			    oidInputField.setText(currentOidNumber.toString()); // synch the input field with the tree display
		}
	}
	

    /**
     * Listens for and handles events from a GetRequestWorker.
     */
    private class MibGetRequestListener implements GetRequestListener
    {
	    /**
	     * Displays the resolved name of an IP address next to the input combo box, 
	     * and adds the raw address to this combo box if it does not already exist.
	     */
	    public void hostAddressResolved(String validAddress, String resolvedAddress) 
	    {
	        // Display the resolved address.
	        resolvedAddrField.setText(resolvedAddress);
	        resolvedAddrField.setCaretPosition(0);
	        
	        int listLength = addressBox.getItemCount();
	        
	        // Do a search to determine if this address already exists in the combo box.
	        boolean alreadyExists = false;
	        int i = 0;
	        while (i < listLength && !alreadyExists)
	        {
	            String currentAddr = (String)addressBox.getItemAt(i);
	            if (validAddress.equals(currentAddr))
	                alreadyExists = true;
	            
	            i++;
	        }
	        
	        // If the address doesn't already exist, add it to the beginning of the list.
	        if (!alreadyExists)
	            addressBox.insertItemAt(validAddress, 0);
	    }
	    
	    
	    /**
	     * Adds a new GetRequestResult to the resultsList.
	     * A GetRequestResult encapsulates data about a single OID and its
	     * returned value. Both the partial OID name and full OID number are stored in it.
	     * @see GetRequestResult
	     */
	    public void requestResultReceived(GetRequestResult dataResultItem) 
	    {
	    	Action getAction = getButton.getAction();
	    	if (!getAction.isEnabled())
	    		getAction.setEnabled(true);
	    	
	        ((DefaultListModel)resultsList.getModel()).addElement(dataResultItem);
	    }
	    
	    
	   /**
	    * Adds the termination status of the GetRequestWorker to the results list.  If it terminated
	    * successfully, an empty String will be returned.  It also resets the get data button.
	    */
	    public void requestTerminated(String messageString)
	    {
	    	Action getAction = getButton.getAction();
	    	if (!getAction.isEnabled())
	    		getAction.setEnabled(true);
	    	
	    	// Message string will only contain error messages, has nothing when successful.
	        if (!messageString.equals(""))
	            ((DefaultListModel)resultsList.getModel()).addElement(messageString);
	    }
    }
    
}

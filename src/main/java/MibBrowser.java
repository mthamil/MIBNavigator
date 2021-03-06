/**
 * MIB Navigator
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

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.*;

import utilities.NullArgumentException;
import utilities.Strings;
import utilities.events.EventListener;

import libmib.MibObjectIdentifier;
import libmib.MibObjectType;
import libmib.mibtree.AbstractMibTreeBuilder;
import libmib.mibtree.MibAddedEventInfo;
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

    private JLabel oidNameLabel, oidNumeralLabel, addressLabel, communityLabel, portLabel, timeoutLabel, oidInputLabel;
    private JTextField oidNameField, oidNumeralField, resolvedAddressField, communityField, oidInputField;
    private JFormattedTextField portField, timeoutField;
    private JComboBox addressBox;
    
    private JLabel resultsLabel;
    private JScrollPane resultsScroll;
    private JList resultsList;
    
    private JButton getButton;
    
    private Color backgroundColor;

    private StringBuilder currentOidName;
    private StringBuilder currentOidNumeral;
    
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
            throw new NullArgumentException("newBuilder");
        
        treeBuilder = newBuilder;
        
        currentOidName = new StringBuilder();
        currentOidNumeral = new StringBuilder();
        
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
        oidNameLabel = new JLabel(StringResources.getString("oidNameLabel"));
        backgroundColor = oidNameLabel.getBackground(); //this is for look and feel purposes
        oidNumeralLabel = new JLabel(StringResources.getString("oidNumeralLabel"));

        oidNameField = new JTextField(37);
        oidNameField.setEditable(false);
        oidNameField.setBackground(backgroundColor);
        oidNameField.addMouseListener(contextMenuListener);

        oidNumeralField = new JTextField(37);
        oidNumeralField.setEditable(false);
        oidNumeralField.setBackground(backgroundColor);
        oidNumeralField.addMouseListener(contextMenuListener);
        
        
		// Host info
		addressLabel = new JLabel(StringResources.getString("ipAddressLabel"));
        addressBox = new JComboBox();
        addressBox.setMaximumRowCount(15);
        addressBox.setEditable(true);
        Dimension addressSize = LookAndFeelResources.getDimension("addressBox");
        addressBox.setPreferredSize(addressSize);
        addressBox.setMaximumSize(addressSize);
        
        // Add a context menu to the combo box's editor component.
        ComboBoxEditor editor = addressBox.getEditor();
        Component comp = editor.getEditorComponent();
        comp.addMouseListener(contextMenuListener);

        resolvedAddressField = new JTextField(17);
        resolvedAddressField.setEditable(false);
        resolvedAddressField.setHorizontalAlignment(JTextField.CENTER);
        resolvedAddressField.setBackground(backgroundColor);
        resolvedAddressField.addMouseListener(contextMenuListener);

        communityLabel = new JLabel(StringResources.getString("communityStringLabel"));
        communityField = new JTextField(12);
        communityField.setText("public");
        communityField.setEditable(true);
        communityField.addMouseListener(contextMenuListener);
        
        // Configure formatter for integer fields.
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false);
        integerFormat.setRoundingMode(RoundingMode.DOWN);
        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setMinimum(1);
        
        portLabel = new JLabel(StringResources.getString("portLabel"));
        portField = new JFormattedTextField(numberFormatter);
        portField.setColumns(4);
        portField.addMouseListener(contextMenuListener);
        
        timeoutLabel = new JLabel(StringResources.getString("timeoutLabel"));
        timeoutField = new JFormattedTextField(numberFormatter);
        timeoutField.setColumns(4);
        timeoutField.addMouseListener(contextMenuListener);

        oidInputLabel = new JLabel(StringResources.getString("oidInputLabel"));
        oidInputField = new JTextField(21);
        oidInputField.setText("");
        oidInputField.setEditable(true);
        oidInputField.addMouseListener(contextMenuListener);
        oidInputField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "treeSearch");
        oidInputField.getActionMap().put("treeSearch", new MibTreeSearchAction(this)); 

        ListContextMenu listContextMenu = new ListContextMenu();
        ListContextMenuListener listContextMenuListener = new ListContextMenuListener(listContextMenu);
        
		resultsLabel = new JLabel(StringResources.getString("resultsLabel")); 
        resultsList = new JList(new DefaultListModel());
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        resultsList.addMouseListener(listContextMenuListener);
        resultsList.addListSelectionListener(new ResultsListListener(this));
        
        resultsScroll = new JScrollPane(resultsList);
		resultsScroll.setPreferredSize(LookAndFeelResources.getDimension("resultsScroll"));

        getButton = new JButton();
        getButton.setPreferredSize(LookAndFeelResources.getDimension("getButton"));
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
        
        treeBuilder.addMibAddedListener(new EventListener<AbstractMibTreeBuilder, MibAddedEventInfo>()
		{
			public void handleEvent(AbstractMibTreeBuilder source, MibAddedEventInfo eventInfo)
			{
                ((DefaultTreeModel)treeBuilder.getTreeModel()).reload();
				setVisibleNodeByOID(eventInfo.getMibRoot().getOidNumeralPath(), NodeSearchOption.MatchExactPath);
			}
		});
        
        // If the mibs directory wasn't found, this will return a tree model with only default nodes.
        mibModel = (DefaultTreeModel)treeBuilder.getTreeModel();

        mibTree = new JTree();
        mibTree.setModel(mibModel); 
        mibTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        mibTree.addTreeSelectionListener(new MibTreeListener());
        mibTree.addTreeSelectionListener(new TreeSelectionListener()
        {
			public void valueChanged(TreeSelectionEvent e)
			{
				TreePath treePath = e.getPath();
				MibTreeNode node = (MibTreeNode)treePath.getLastPathComponent();
				MibObjectIdentifier mibObject = (MibObjectIdentifier)node.getUserObject();
				
				// Display the selected OID's details.
				if (mibObject instanceof MibObjectType)
					oidViewer.setMIBObject((MibObjectType)mibObject);
				else
					oidViewer.setMIBObject(mibObject);
			}
        });
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
		GridBagConstraints constraints = new GridBagConstraints();
		Insets inset = new Insets(2, 2, 2, 0);
		constraints.insets = inset;
		//cons.gridwidth = 1;
		//cons.gridheight = 1;

		// TREE PANEL
		JPanel treePanel = new JPanel();
		treePanel.setLayout(layout);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = .5;
		constraints.weighty = .5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		layout.setConstraints(mibTreeScroll, constraints);
		treePanel.add(mibTreeScroll);


		// TREE AND OID DETAILS PANEL
		JPanel topPanel = new JPanel();
		topPanel.setLayout(layout);

		constraints.gridx = 0;
		constraints.gridy = 0;
        constraints.weightx = .65;
        constraints.weighty = .65;
		constraints.fill = GridBagConstraints.BOTH;
		layout.setConstraints(treePanel, constraints);
		topPanel.add(treePanel);

		constraints.gridx = 1;
		constraints.gridy = 0;
        constraints.weightx = .35;
        constraints.weighty = .35;
		layout.setConstraints(oidViewer.getPanel(), constraints);
		topPanel.add(oidViewer.getPanel());


		// OID NUMBER AND NAME PANEL
		JPanel oidPanel = new JPanel();
		oidPanel.setLayout(layout);

		inset.set(2, 0, 2, 0);
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.anchor = GridBagConstraints.LINE_START;

		constraints.fill = GridBagConstraints.NONE;
		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(oidNameLabel, constraints);
		oidPanel.add(oidNameLabel);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 1;
		constraints.gridy = 0;
		layout.setConstraints(oidNameField, constraints);
		oidPanel.add(oidNameField);

		constraints.fill = GridBagConstraints.NONE;
		constraints.gridx = 0;
		constraints.gridy = 1;
		layout.setConstraints(oidNumeralLabel, constraints);
		oidPanel.add(oidNumeralLabel);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 1;
		constraints.gridy = 1;
		layout.setConstraints(oidNumeralField, constraints);
		oidPanel.add(oidNumeralField);
        
        
		// TIMEOUT/PORT PANEL
        JPanel hostSubPanel = new JPanel();
        hostSubPanel.setLayout(layout);
        
        inset.set(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        layout.setConstraints(portLabel, constraints);
        hostSubPanel.add(portLabel);
        
        constraints.gridx = 1;
        constraints.gridy = 0;
        layout.setConstraints(portField, constraints);
        hostSubPanel.add(portField);
        
        constraints.gridx = 2;
        constraints.gridy = 0;
        layout.setConstraints(timeoutLabel, constraints);
        hostSubPanel.add(timeoutLabel);
        
        constraints.gridx = 3;
        constraints.gridy = 0;
        layout.setConstraints(timeoutField, constraints);
        hostSubPanel.add(timeoutField);


		// HOST DETAILS PANEL
		JPanel hostPanel = new JPanel();
		hostPanel.setLayout(layout);

		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.fill = GridBagConstraints.NONE;

		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(addressLabel, constraints);
		hostPanel.add(addressLabel);

		constraints.gridx = 1;
		constraints.gridy = 0;
		layout.setConstraints(addressBox, constraints);
		hostPanel.add(addressBox);

        constraints.gridx = 2;
        constraints.gridy = 0;
        layout.setConstraints(resolvedAddressField, constraints);
        hostPanel.add(resolvedAddressField);

		constraints.gridx = 0;
		constraints.gridy = 1;
		layout.setConstraints(communityLabel, constraints);
		hostPanel.add(communityLabel);

		constraints.gridx = 1;
		constraints.gridy = 1;
		layout.setConstraints(communityField, constraints);
		hostPanel.add(communityField);
        
        constraints.gridx = 2;
        constraints.gridy = 1;
        layout.setConstraints(hostSubPanel, constraints);
        hostPanel.add(hostSubPanel);
        

		// OID NUMBER/NAME AND HOST DETAILS CONTAINER PANEL
		JPanel topMidPanel = new JPanel();
		topMidPanel.setLayout(layout);

		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(oidPanel, constraints);
		topMidPanel.add(oidPanel);

		constraints.gridx = 1;
		constraints.gridy = 0;
		layout.setConstraints(hostPanel, constraints);
		topMidPanel.add(hostPanel);

		// BUTTONS PANEL
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(layout);

		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(getButton, constraints);
		buttonPanel.add(getButton);

		// OID SEARCH/SELECTION PANEL
		JPanel oidSearchPanel = new JPanel();
		oidSearchPanel.setLayout(layout);

		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(oidInputLabel, constraints);
		oidSearchPanel.add(oidInputLabel);

		constraints.gridx = 1;
		constraints.gridy = 0;
		layout.setConstraints(oidInputField, constraints);
		oidSearchPanel.add(oidInputField);

		// BUTTON AND SEARCH/SELECTION CONTAINER PANEL
		JPanel bottomMidPanel = new JPanel();
		bottomMidPanel.setLayout(layout);

		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(buttonPanel, constraints);
		bottomMidPanel.add(buttonPanel);

		constraints.gridx = 1;
		constraints.gridy = 0;
		layout.setConstraints(oidSearchPanel, constraints);
		bottomMidPanel.add(oidSearchPanel);

		// RESULTS PANEL
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(layout);

		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(resultsLabel, constraints);
		bottomPanel.add(resultsLabel);

		constraints.weightx = .75;
		constraints.weighty = .75;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		constraints.gridx = 0;
		constraints.gridy = 1;
		layout.setConstraints(resultsScroll, constraints);
		bottomPanel.add(resultsScroll);


		// Configure and add panels to the master browser panel.
        browserPanel.setLayout(layout);
        browserPanel.setBackground(backgroundColor);

		inset.set(2, 0, 2, 0);
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
        constraints.weightx = .30;
        constraints.weighty = .30;
		layout.setConstraints(topPanel, constraints);
        browserPanel.add(topPanel);

		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridx = 0;
		constraints.gridy = 1;
        constraints.weightx = .0;
        constraints.weighty = .0;
		layout.setConstraints(topMidPanel, constraints);
        browserPanel.add(topMidPanel);

		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridx = 0;
		constraints.gridy = 2;
        constraints.weightx = .0;
        constraints.weighty = .0;
		layout.setConstraints(bottomMidPanel, constraints);
        browserPanel.add(bottomMidPanel);

		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.fill = GridBagConstraints.BOTH;
		inset.set(3, 0, 3, 0);
		constraints.insets = inset;
		constraints.weightx = .5;
		constraints.weighty = .5;
		constraints.gridx = 0;
		constraints.gridy = 3;
		layout.setConstraints(bottomPanel, constraints);
        browserPanel.add(bottomPanel);
	}
    
    
    // *** Start of MibBrowser configurable property methods. ***
    
    /**
     * Gets the default directory where MIB files are contained.
     */
    public File getMibDirectory()
    {
    	return mibDirectory;
    }
    
    /**
     * Sets the default directory where MIB files are contained.
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
    
    /**
     * Gets the browser's port number.
     * @return
     */
    public int getPort()
    {
    	return (Integer)portField.getValue();
    }
    
    /**
     * Sets the browser's port number.
     * @param port
     */
    public void setPort(int port)
    {
    	portField.setText(String.valueOf(port));
    	
    	try
    	{
    		portField.commitEdit();
    	}
    	catch (ParseException e) { }	// This should pass since the method takes an int
    }
    
    /**
     * Gets the browser's timeout.
     * @return
     */
    public int getTimeout()
    {
    	return (Integer)timeoutField.getValue();
    }
    
    /**
     * Sets the browser's timeout.
     * @param timeout
     */
    public void setTimeout(int timeout)
    {
    	timeoutField.setText(String.valueOf(timeout));
    	
    	try
    	{
    		timeoutField.commitEdit();
    	}
    	catch (ParseException e) { }	// This should pass since the method takes an int
    }
    
    // *** End of MibBrowser configurable property methods. ***
    
    
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
    	private GetRequestTask snmpGetTask = null;
    	
    	public GetRequestAction()
    	{
    		putValue(NAME, StringResources.getString("getButton"));
    	}
    	
		/**
		 * ActionListener implementation method: reacts to menu and button actions, which are usually 
	     * clicks or the pressing of the Enter key when a button has focus.
	     * 
	     * @param event the ActionEvent generated when a component's default action occurs
	     */
		public void actionPerformed(ActionEvent event)
		{  
		    // Spawn a new GetRequestTask thread for retrieving data from a device running an SNMP agent.
            
            if (getValue(NAME).equals(StringResources.getString("getButton")))
            {
                // This check is just to avoid even attempting to use an empty OID or IP address field.
                if (oidInputField.getText().trim().equals("") ||
                    addressBox.getSelectedItem() == null ||
                    (((String)addressBox.getSelectedItem()).trim().equals("")) )
                {
                	return;
                }

                DefaultListModel resultsListModel = (DefaultListModel)resultsList.getModel();
                try
                {
                    // Get all necessary values from the user interface before starting the Get process.
                    // This ensures that the user can't affect what values the thread uses once it has 
                    // been launched.
                    SnmpHost host = createSnmpHost();

                    // Try to scroll to the correct OID.
                    String oidInputString = Strings.trim(oidInputField.getText().trim(), '.');
                    String oidTreeNumeralPathString = oidNumeralField.getText();
                    
                    if (!oidInputString.equals(oidTreeNumeralPathString))
                        setVisibleNodeByOID(oidInputString, NodeSearchOption.MatchExactPath);

                    resultsListModel.removeAllElements();
                    putValue(NAME, StringResources.getString("stopButton"));
                    
                    // Initialize and start the GetRequest process in a different thread using a SwingWorker.
                	snmpGetTask = createNewTask(host, oidInputString);
                    
                    // Disable the Get button because clicking Stop will not do anything
                    // while waiting for the timeout.
                    this.setEnabled(false);
                    
                    //snmpGetTask.start();
                    snmpGetTask.execute();
                }
                catch (NumberFormatException e)
                {
                    resultsListModel.removeAllElements();
                    resultsListModel.addElement(StringResources.getString("badOidInputMessage") + e.getMessage() + "\n");
                }
            }
            else
            {
                if (snmpGetTask != null)
                    snmpGetTask.cancel(true);  // stop the Get process
            }
		}
		
		private GetRequestTask createNewTask(SnmpHost host, String oidInputString)
		{
			// snmpGetTask = new GetRequestWorker(host, oidInputString, (MibTreeNode)mibModel.getRoot());
			// snmpGetTask.addGetRequestListener(new MibGetRequestListener());
			      
			GetRequestTask newGetTask = new GetRequestTask(host, oidInputString, (MibTreeNode)mibModel.getRoot());
			newGetTask.addGetRequestListener(new MibGetRequestListener());
			newGetTask.addPropertyChangeListener(new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent propertyEvent)
				{
					if ("state".equals(propertyEvent.getPropertyName()) && javax.swing.SwingWorker.StateValue.DONE == propertyEvent.getNewValue())
					{
						getButton.getAction().putValue(GetRequestAction.NAME, StringResources.getString("getButton"));
					}
				}
			});

			newGetTask.setResultProcessor(new GetRequestResultProcessor()
			{
				public void processResult(GetRequestResult result)
				{
					Action getAction = getButton.getAction();
					if (!getAction.isEnabled())
						getAction.setEnabled(true);

					((DefaultListModel)resultsList.getModel()).addElement(result);
				}
			});
			
			return newGetTask;
		}

		private SnmpHost createSnmpHost()
		{
            String communityString = communityField.getText().trim();
            String addressString = ((String)addressBox.getSelectedItem()).trim();
            int port = Integer.parseInt(portField.getText().trim());
            int timeout = Integer.parseInt(timeoutField.getText().trim());
            SnmpHost host = new SnmpHost(communityString, addressString, port, timeout);
            return host;
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
			currentOidNumeral.delete(0, currentOidNumeral.length());

			Object[] path = event.getPath().getPath();
			MibTreeNode node;
			MibObjectIdentifier mibObject;
			
			// Construct the MIB object name and OID path strings from the object path 
	        // array; start at 1 to exclude the root.
			for (int i = 1; i < path.length; i++)
			{
				node = (MibTreeNode)path[i];
				mibObject = (MibObjectIdentifier)node.getUserObject();
	
				// Don't put a '.' at the beginning.
				if (i > 1)
				{
					currentOidName.append(".");
					currentOidNumeral.append(".");
				}
	
				currentOidName.append(mibObject.getName());
				currentOidNumeral.append(String.valueOf(mibObject.getId()));
			}
	
			// Set the OID name and numeral path strings.
			oidNameField.setText(currentOidName.toString());
			oidNumeralField.setText(currentOidNumeral.toString());
	
	        if (!oidInputField.getText().trim().equals(oidNumeralField.getText().trim()))
			    oidInputField.setText(currentOidNumeral.toString()); // sync the input field with the tree display
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
	        resolvedAddressField.setText(resolvedAddress);
	        resolvedAddressField.setCaretPosition(0);
	        
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
	     * returned value. Both the partial OID name and full OID numeral path are stored in it.
	     * @see GetRequestResult
	     */
	    public void requestResultReceived(GetRequestResult result) 
	    {
	    	throw new UnsupportedOperationException();
//	    	Action getAction = getButton.getAction();
//	    	if (!getAction.isEnabled())
//	    		getAction.setEnabled(true);
//	    	
//	        ((DefaultListModel)resultsList.getModel()).addElement(result);
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

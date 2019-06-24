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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import libmib.MibObjectIdentifier;
import libmib.MibObjectType;
import libmib.MibSyntax;
import libmib.MibObjectType.Access;
import libmib.MibObjectType.Status;
import contextmenu.TextContextMenu;
import contextmenu.TextContextMenuListener;

/**
 * A GUI component for displaying information about a MIB object.
 */
public class OidDataPanel
{
    private JPanel oidInfoPanel;	// The main panel.
    private JLabel oidDescLabel;
    private JEditorPane oidDescription;
    private JScrollPane oidDescriptionScroll;
    
    private JLabel oidValuePairsLabel;
    private JComboBox oidValuePairsBox;

    private JLabel oidDataTypeLabel, oidAccessLabel, oidStatusLabel, oidMIBNameLabel;
    private JTextField oidDataTypeField, oidAccessField, oidStatusField, oidMIBNameField;
    
    private Color uneditableBackColor;
    
    private static final String DESC_FORMAT = "<font face='%s' size='%d'>%s</font>";
    
    public OidDataPanel() 
    {
        configureComponents();
        layoutComponents();
    }
    
    private void configureComponents()
    {
        oidInfoPanel = new JPanel();
        
        // This is to ensure that uneditable text fields match the background.
        uneditableBackColor = oidInfoPanel.getBackground();
        
        TextContextMenu contextMenu = new TextContextMenu();
		TextContextMenuListener contextMenuListener = new TextContextMenuListener(contextMenu);
        
        // OID details
        oidDataTypeLabel = new JLabel(StringResources.getString("oidTypeLabel"));
        oidDataTypeField = new JTextField(18);
        oidDataTypeField.setEditable(false);
        oidDataTypeField.setBackground(uneditableBackColor);
        oidDataTypeField.addMouseListener(contextMenuListener);

        Dimension valuesSize = LookAndFeelResources.getDimension("oidValuePairsBox");
        oidValuePairsLabel = new JLabel(StringResources.getString("oidValuePairsLabel"));
        oidValuePairsBox = new JComboBox();
        oidValuePairsBox.setPreferredSize(valuesSize);
        oidValuePairsBox.setMaximumSize(valuesSize);
        oidValuePairsBox.setEditable(false);
        oidValuePairsBox.setEnabled(false);
        
        oidAccessLabel = new JLabel(StringResources.getString("oidAccessLabel"));
        oidAccessField = new JTextField(12);
        oidAccessField.setEditable(false);
        oidAccessField.setBackground(uneditableBackColor);
        oidAccessField.addMouseListener(contextMenuListener);
        
        oidStatusLabel = new JLabel(StringResources.getString("oidStatusLabel"));
        oidStatusField = new JTextField(12);
        oidStatusField.setEditable(false);
        oidStatusField.setBackground(uneditableBackColor);
        oidStatusField.addMouseListener(contextMenuListener);

        oidMIBNameLabel = new JLabel(StringResources.getString("oidSourceLabel"));
        oidMIBNameField = new JTextField(18);
        oidMIBNameField.setEditable(false);
        oidMIBNameField.setBackground(uneditableBackColor);
        oidMIBNameField.addMouseListener(contextMenuListener);

        oidDescription = new JEditorPane();
        oidDescription.setEditable(false);
        oidDescription.setBackground(uneditableBackColor);
        oidDescription.setBackground(oidStatusLabel.getBackground());
        oidDescription.setContentType("text/html");
        oidDescription.addMouseListener(contextMenuListener);

        oidDescriptionScroll = new JScrollPane(oidDescription);
        oidDescriptionScroll.setPreferredSize(LookAndFeelResources.getDimension("oidDescription"));
        oidDescLabel = new JLabel(StringResources.getString("oidDescriptionLabel"));
    }
    
    
    private void layoutComponents()
    {       
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        Insets ins = new Insets(2, 2, 2, 3);
        cons.insets = ins;
        
        // OID DETAILS PANEL
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(layout);

        cons.anchor = GridBagConstraints.FIRST_LINE_START;
        cons.fill = GridBagConstraints.NONE;

        cons.gridx = 0;
        cons.gridy = 0;
        layout.setConstraints(oidDataTypeLabel, cons);
        detailPanel.add(oidDataTypeLabel);

        cons.gridx = 1;
        cons.gridy = 0;
        layout.setConstraints(oidDataTypeField, cons);
        detailPanel.add(oidDataTypeField);

        cons.gridx = 2;
        cons.gridy = 0;
        layout.setConstraints(oidAccessLabel, cons);
        detailPanel.add(oidAccessLabel);

        cons.gridx = 3;
        cons.gridy = 0;
        layout.setConstraints(oidAccessField, cons);
        detailPanel.add(oidAccessField);

        cons.gridx = 0;
        cons.gridy = 1;
        layout.setConstraints(oidValuePairsLabel, cons);
        detailPanel.add(oidValuePairsLabel);

        cons.gridx = 1;
        cons.gridy = 1;
        layout.setConstraints(oidValuePairsBox, cons);
        detailPanel.add(oidValuePairsBox);

        cons.gridx = 2;
        cons.gridy = 1;
        layout.setConstraints(oidStatusLabel, cons);
        detailPanel.add(oidStatusLabel);

        cons.gridx = 3;
        cons.gridy = 1;
        layout.setConstraints(oidStatusField, cons);
        detailPanel.add(oidStatusField);

        cons.gridx = 0;
        cons.gridy = 2;
        layout.setConstraints(oidMIBNameLabel, cons);
        detailPanel.add(oidMIBNameLabel);

        cons.gridx = 1;
        cons.gridy = 2;
        layout.setConstraints(oidMIBNameField, cons);
        detailPanel.add(oidMIBNameField);

        // OID DESCRIPTION PANEL
        JPanel descPanel = new JPanel();
        descPanel.setLayout(layout);

        ins.set(1, 2, 0, 2);

        cons.gridx = 0;
        cons.gridy = 0;
        cons.weightx = 0;
        cons.weighty = 0;
        cons.anchor = GridBagConstraints.LINE_START;
        layout.setConstraints(oidDescLabel, cons);
        descPanel.add(oidDescLabel);

        cons.gridx = 0;
        cons.gridy = 1;
        cons.weightx = 1;
        cons.weighty = 1;
        cons.fill = GridBagConstraints.BOTH;
        layout.setConstraints(oidDescriptionScroll, cons);
        descPanel.add(oidDescriptionScroll);

        // OID DETAIL AND DESCRIPTION CONTAINER PANEL (the main panel)
        oidInfoPanel.setLayout(layout);

        ins.set(2, 0, 2, 0);
        cons.anchor = GridBagConstraints.LINE_START;

        cons.weightx = 0;
        cons.weighty = 0;
        cons.fill = GridBagConstraints.NONE;
        cons.gridx = 0;
        cons.gridy = 0;
        layout.setConstraints(detailPanel, cons);
        oidInfoPanel.add(detailPanel);

        cons.weightx = 1;
        cons.weighty = 1;
        cons.fill = GridBagConstraints.BOTH;
        cons.gridx = 0;
        cons.gridy = 1;
        layout.setConstraints(descPanel, cons);
        oidInfoPanel.add(descPanel);
    }
    
    /**
     * Sets the MIb Object displayed by the OID viewer.
     * @param mibObject
     */
    public void setMIBObject(MibObjectIdentifier mibObject)
    {
    	oidMIBNameField.setText(mibObject.getMibName());
        oidMIBNameField.setCaretPosition(0);
    }
    
    /**
     * Sets the MIb Object displayed by the OID viewer.
     * @param mibObject
     */
	public void setMIBObject(MibObjectType mibObject)
    {
        // Display the OID's details in the panel.
        MibSyntax oidSyntax = mibObject.getSyntax();
        if (oidSyntax != null)
            oidDataTypeField.setText(oidSyntax.getDataType());
        else
            oidDataTypeField.setText("");
        
        oidDataTypeField.setCaretPosition(0);
        
        Status oidStatus = mibObject.getStatus();
        if (oidStatus != null)
            oidStatusField.setText(oidStatus.toString());
        else
            oidStatusField.setText("");
        
        Access oidAccess = mibObject.getAccess();
        if (oidAccess != null)
            oidAccessField.setText(oidAccess.toString());
        else
            oidAccessField.setText("");

        // The oidDesc JEditorPane automatically converts escaped special characters
        // to the correct display characters, ie. "&amp" -> "&" and "&gt;" -> ">".
        Font descriptionFont = LookAndFeelResources.getFont("oidDescription");
        String descriptionContent = String.format(DESC_FORMAT, descriptionFont.getName(), descriptionFont.getSize(), mibObject.getDescription());
        oidDescription.setText(descriptionContent);
        oidDescription.setCaretPosition(0);
        
        oidMIBNameField.setText(mibObject.getMibName());
        oidMIBNameField.setCaretPosition(0);

        // Process the object's enumerated data type.
        DefaultComboBoxModel comboModel = (DefaultComboBoxModel)oidValuePairsBox.getModel();
        if (mibObject.hasNameValuePairs())
        {
            oidValuePairsBox.setEnabled(true);
            comboModel.removeAllElements();
            
            Map<Integer, String> pairs = mibObject.getSyntax().getValuePairs();
            for (Entry<Integer, String> entry : pairs.entrySet())
            	comboModel.addElement(String.format("%s (%d)", entry.getValue(), entry.getKey()));
        }
        else
        {
            oidValuePairsBox.setEnabled(false);
            if (comboModel.getSize() > 0)
                comboModel.removeAllElements();
        }
    }
    
    
    public JPanel getPanel()
    {
        return oidInfoPanel;
    }
    
    /*public static void main(String[] args)
    {
        TextContextMenu popMenu = new TextContextMenu();
        TextContextMenuListener popupListen = new TextContextMenuListener(popMenu);
        JFrame testFrame = new JFrame();
        OidInfoViewer infoView = new OidInfoViewer(popupListen);
        testFrame.add(infoView.getPanel());
        testFrame.pack();
        testFrame.setVisible(true);
        
        MibObjectType testObj = new MibObjectType("test", 1);
        testObj.setAccess(MibObjectType.Access.READ_ONLY);
        testObj.setDescription("haha");
        testObj.setSyntax(new MibSyntax("this is the type"));
        testObj.setMibName("test.mib");
        
        infoView.setMIBObject(testObj);
        
    }*/

}

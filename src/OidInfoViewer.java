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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import contextmenu.TextContextMenuListener;

import libmib.oid.MibObjectType;
import libmib.oid.MibSyntax;
import libmib.oid.MibNameValuePair;
import libmib.oid.MibObjectType.Access;
import libmib.oid.MibObjectType.Status;

public class OidInfoViewer
{
    private JPanel oidInfoPanel;
    private JLabel oidDescLabel;
    private JEditorPane oidDesc;
    private JScrollPane oidDescScroll;
    
    private JLabel oidValueBoxLabel;
    private JComboBox oidValueBox;

    private JLabel oidDataTypeLabel, oidAccessLabel, oidStatusLabel, oidMIBNameLabel;
    private JTextField oidDataTypeField, oidAccessField, oidStatusField, oidMIBNameField;
    
    private TextContextMenuListener popListen;  //listens for popup events on internal swing components
    
    private Color uneditableBackColor;
    
    private static final String DESC_HTML_PREFIX = "<font face=\"SansSerif\" size=\"2\">";
    private static final String DESC_HTML_SUFFIX = "</font>";

    
    public OidInfoViewer(TextContextMenuListener popup) 
    {
        popListen = popup;
        configureComponents();
        layoutComponents();
    }
    
    private void configureComponents()
    {
        oidInfoPanel = new JPanel(); //instantiate the main panel
        
        uneditableBackColor = oidInfoPanel.getBackground();  //This is to ensure that uneditable text fields match the background.
        
        //oid details
        oidDataTypeLabel = new JLabel("Type:");
        oidDataTypeField = new JTextField(18);
        oidDataTypeField.setEditable(false);
        oidDataTypeField.setBackground(uneditableBackColor);
        oidDataTypeField.addMouseListener(popListen);

        Dimension valuesSize = new Dimension(167, 20);
        oidValueBoxLabel = new JLabel("Values:");
        oidValueBox = new JComboBox();
        oidValueBox.setPreferredSize(valuesSize);
        oidValueBox.setMaximumSize(valuesSize);
        oidValueBox.setEditable(false);
        oidValueBox.setEnabled(false);
        
        oidAccessLabel = new JLabel("Access:");
        oidAccessField = new JTextField(12);
        oidAccessField.setEditable(false);
        oidAccessField.setBackground(uneditableBackColor);
        oidAccessField.addMouseListener(popListen);
        
        oidStatusLabel = new JLabel("Status:");
        oidStatusField = new JTextField(12);
        oidStatusField.setEditable(false);
        oidStatusField.setBackground(uneditableBackColor);
        oidStatusField.addMouseListener(popListen);

        oidMIBNameLabel = new JLabel("Defined in:");
        oidMIBNameField = new JTextField(18);
        oidMIBNameField.setEditable(false);
        oidMIBNameField.setBackground(uneditableBackColor);
        oidMIBNameField.addMouseListener(popListen);

        oidDesc = new JEditorPane();
        oidDesc.setEditable(false);
        oidDesc.setBackground(uneditableBackColor);
        oidDesc.setBackground(oidStatusLabel.getBackground());
        oidDesc.setContentType("text/html");
        oidDesc.addMouseListener(popListen);

        oidDescScroll = new JScrollPane(oidDesc);
        oidDescScroll.setPreferredSize(new Dimension(75, 100));
        oidDescLabel = new JLabel("Description:");
    }
    
    
    private void layoutComponents()
    {
 /*       //OID DETAILS
        GridLayout detailsLayout = new GridLayout(3, 4, 3, 3);
        
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(detailsLayout);
        
        detailPanel.add(oidDataTypeLabel);
        detailPanel.add(oidDataTypeField);

        detailPanel.add(oidAccessLabel);
        detailPanel.add(oidAccessField);

        detailPanel.add(oidValueBoxLabel);
        detailPanel.add(oidValueBox);

        detailPanel.add(oidStatusLabel);
        detailPanel.add(oidStatusField);

        detailPanel.add(oidMIBNameLabel);
        detailPanel.add(oidMIBNameField);
        
        
        //MAIN PANEL
        BorderLayout mainLayout = new BorderLayout(2, 2);
        
        oidInfoPanel = new JPanel();
        oidInfoPanel.setLayout(mainLayout);
        
        oidInfoPanel.add(detailPanel, BorderLayout.NORTH);
        oidInfoPanel.add(oidDescScroll, BorderLayout.SOUTH);*/
        
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        Insets ins = new Insets(2, 2, 2, 3);
        cons.insets = ins;
        
        //OID DETAILS PANEL
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
        layout.setConstraints(oidValueBoxLabel, cons);
        detailPanel.add(oidValueBoxLabel);

        cons.gridx = 1;
        cons.gridy = 1;
        layout.setConstraints(oidValueBox, cons);
        detailPanel.add(oidValueBox);

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

        //OID DESCRIPTION PANEL
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
        layout.setConstraints(oidDescScroll, cons);
        descPanel.add(oidDescScroll);

        //OID DETAIL AND DESCRIPTION CONTAINER PANEL (the main panel)
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
    
    
    public void setMIBObject(MibObjectType currOID)
    {
        //display the OID's details in the panel
        MibSyntax oidSyntax = currOID.getSyntax();
        if (oidSyntax != null)
            oidDataTypeField.setText(oidSyntax.getDataType());
        else
            oidDataTypeField.setText("");
        
        oidDataTypeField.setCaretPosition(0);
        
        Status oidStatus = currOID.getStatus();
        if (oidStatus != null)
            oidStatusField.setText(oidStatus.toString());
        else
            oidStatusField.setText("");
        
        Access oidAccess = currOID.getAccess();
        if (oidAccess != null)
            oidAccessField.setText(oidAccess.toString());
        else
            oidAccessField.setText("");

        //The oidDesc JEditorPane automatically converts escaped special characters
        //to the correct display characters, ie. "&amp" -> "&" and "&gt;" -> ">".
        oidDesc.setText(DESC_HTML_PREFIX + currOID.getDescription() + DESC_HTML_SUFFIX);
        oidDesc.setCaretPosition(0);
        oidMIBNameField.setText(currOID.getMibName());
        oidMIBNameField.setCaretPosition(0);

        //if a particular MibObjectType has an enumerated data type
        DefaultComboBoxModel curModel = (DefaultComboBoxModel)oidValueBox.getModel();
        if (currOID.hasValueList())
        {
            oidValueBox.setEnabled(true);
            curModel.removeAllElements();
            
            List<MibNameValuePair> valList = currOID.getSyntax().getValues();
            for (MibNameValuePair item : valList)
                curModel.addElement(item.getName() + " (" + item.getValue() + ")");
        }
        else
        {
            oidValueBox.setEnabled(false);
            if (curModel.getSize() > 0)
                curModel.removeAllElements();
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

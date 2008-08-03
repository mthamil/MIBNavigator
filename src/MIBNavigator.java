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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.FontUIResource;
import javax.xml.parsers.ParserConfigurationException;

import libmib.mibtree.MibTreeBuilder;
import libmib.mibtree.MibTreeBuilderXml;

import org.xml.sax.SAXException;

/**
 * MIBNavigator is the main class for the application. It performs initial configuration and starts the user interface.
 * It can be easily modified to use regular SMI syntax MIBs by using MibTreeBuilderSmi in place of MibTreeBuilderXml 
 * because they implement a common interface.
 * <br><br>
 * The application reads MIB files and presents them visually for navigation through MibBrowser. The OID hierarchy is 
 * displayed in a tree and information about each OID node is also shown. In addition, it uses SNMP GetNextRequests to 
 * "walk" the MIB tree of a specified host and display the retrieved values.  I have designed the code to be mostly SNMP 
 * implementation independent.  If a different SNMP implementation were to be used, it would need to support the use of 
 * individual GetNextRequests and the GetRequestWorker would have to be modified accordingly.
 * <br><br>
 * This project was born out of my frustration with certain limitations of the SNMP tool GetIf's interface.  Namely, it 
 * could not be resized.  All this work because of that.  Who could have thought?  It has served as a major learning 
 * experience.
 * <br><br>
 * Notes on Java compatibility: This application will not work on versions of Java lower than 1.5 (or 5.0, whatever).
 * It uses the following 1.5 functionality: the String contains() method, StringBuilder, the JAXP 1.3 schema validation 
 * technique that has been integrated, the storing and loading of XML Properties files, and extensive use of generic type 
 * parameters.
 */
public class MIBNavigator 
{
    private MIBNavigatorSettings settings;  // saves and loads application state
    private MibBrowser browser;             // main graphical component
   
    private int maxAddresses;               // stores the maximum number of addresses to save (currently not modifiable 
                                            // within the application, only through editing a file by hand)

    /**
     * Creates and configures MIBNavigator's interface. The MibTreeBuilder is 
     * simply passed through to the MibBrowser.
     */
    public MIBNavigator(MibTreeBuilder newBuilder)
    {
        settings = new MIBNavigatorSettings();
        settings.loadSettings();
        maxAddresses = settings.getMaxAddresses();
        
        this.shrinkFonts();

        browser = new MibBrowser(newBuilder);
        browser.setAddresses(settings.getAddressList());
        
        JPanel navPanel = browser.getBrowserPanel();
        
        // Configure the application's JFrame.
        JFrame navFrame = new JFrame();
        navFrame = new JFrame();
        navFrame.setTitle("MIB Navigator");
        navFrame.getRootPane().setBorder(new BevelBorder(BevelBorder.RAISED));
        
        MIBNavigatorMenu navMenu = new MIBNavigatorMenu(this);
        navFrame.setJMenuBar(navMenu.getMenuBar());
        
        navFrame.add(navPanel);  // Add the browser panel to the JFrame's content pane.
        navFrame.pack();
        navFrame.setLocationRelativeTo(null);
        navFrame.setVisible(true);
        
        // This is necesary because certain interface components do not scale when the frame is resized below a set
        // initial width and/or height.  This simply uses the initial, packed size as the minimum.
        Dimension frameSize = navFrame.getSize();
        navFrame.addComponentListener(new MinimumSizeEnforcer((long)frameSize.getWidth(), (long)frameSize.getHeight()));
             
        // Catch window closing events with an anonymous event handler so that the browser's state can be saved.
        navFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        navFrame.addWindowListener(new 
                WindowAdapter()
                {
                    public void windowClosing(WindowEvent we)
                    {
                        saveState();
                        System.exit(0);
                    }
                });
    }
    
    
    /**
     * Gets the MibBrowser used by MIBNavigator.
     */
    public MibBrowser getBrowser()
    {
        return browser;
    }
    
    
    /**
     * Saves persistent attributes to the MIBNavigatorSettings object.
     */
    public void saveState()
    {
        settings.setMaxAddresses(maxAddresses);
        settings.setAddressList(browser.getAddresses());
        settings.saveSettings();
    }
    
    
    
    /**
     * Utility method that sets all fonts in the UI table to smaller than normal size.
     */
    private void shrinkFonts()
    {
        FontUIResource appFont = new FontUIResource("SansSerif", Font.PLAIN, 10);
        UIDefaults defaults = UIManager.getDefaults();//UIManager.getLookAndFeelDefaults();
        Enumeration<Object> keys = defaults.keys();

        while (keys.hasMoreElements())
        {
        	String nextKey = keys.nextElement().toString();
            if (nextKey.toLowerCase().contains("font"))
            	defaults.put(nextKey, appFont);
        }
    }
    

    
    /**
     * Static initializer for the MIBNavigator application.
     */
    public static void main(String args[])
    {
        try
        {  
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            // NOTE: In the event of an exception, the app will fall back to Java's Metal style.
        }

        // The initialization of the MibTreeBuilder occurs first because it is the most crucial 
        // component of the application.  It also helps to decouple the rest of the application 
        // from a specific type of MibTreeBuilder (such as SMI or XML) and provide better exception 
        // handling for the XML version.
        try
        { 
            // ***Configure MIB Compiler***
            // If the schema file used for parsing and validating the MIB files can't be found
            // then there is no point in continuing since XML files can't be added later.
            File schemaFile = new File("." + File.separator + "mib.xsd");
            if (schemaFile.exists())
            {    
                final MibTreeBuilder treeBuilder = new MibTreeBuilderXml(schemaFile);
                //final MibTreeBuilder treeBuilder = new MibTreeBuilderSmi();
                
                // Create and configure the interface components in the EventDispatch 
                // thread according to best practices for using Swing.
                Runnable createInterface = new 
                    Runnable()
                    {
                        public void run()
                        {   
                            MIBNavigator navApp = new MIBNavigator(treeBuilder);
                        }
                    };
                SwingUtilities.invokeLater(createInterface);
            }
            else
            {
                String message = "The schema file, " + schemaFile.getName() + ", was not found.";
                JOptionPane.showMessageDialog(null, message, "MIB Schema Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        // The following exceptions are non-recoverable and application terminating.
        catch (SAXException e)
        {
            JOptionPane.showMessageDialog(null, "An error occurred while parsing the schema file.", 
                    "MIB Tree Compiler Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (ParserConfigurationException e)
        {
            JOptionPane.showMessageDialog(null, "An error occurred configuring the XML parser.", 
                    "MIB Tree Compiler Error", JOptionPane.ERROR_MESSAGE);
        }

    }
}

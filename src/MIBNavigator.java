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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import filefilters.SmiFileFilter;
import filefilters.XmlFileFilter;

import libmib.mibtree.CannotCreateBuilderException;
import libmib.mibtree.MibTreeBuilder;
import libmib.mibtree.MibTreeBuilderFactory;

import settings.UserSettings;

/**
 * MIBNavigator is the main class for the application. It performs initial configuration and starts the user interface.
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
    private UserSettings appSettings;  	// Saves and loads application state
    private MibBrowser browser;         // Main graphical component
   
    private int maxAddresses;           // Stores the maximum number of addresses to save (currently not modifiable 
                                        // within the application, only through editing a file by hand)

    /**
     * Creates and configures the application.
     * 
     *  @param newBuilder - MibTreeBuilder that is 
     *  	   simply passed through to the MibBrowser
     *  @param settings - Application user settings
     */
    public MIBNavigator(MibTreeBuilder newBuilder, UserSettings settings)
    {
    	this.appSettings = settings;
        maxAddresses = settings.getMaxAddresses();
        
        this.shrinkFonts();

        browser = new MibBrowser(newBuilder);
        browser.setAddresses(settings.getAddresses());
        
        this.configureFrame(browser.getBrowserPanel());
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
        appSettings.setMaxAddresses(maxAddresses);
        appSettings.setAddresses(browser.getAddresses());
        appSettings.saveSettings();
    }
    
    
    /**
     * Sets all fonts in the UI table to smaller than normal size.
     */
    private void shrinkFonts()
    {
        FontUIResource appFont = new FontUIResource("SansSerif", Font.PLAIN, 10);
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration<Object> keys = defaults.keys();

        while (keys.hasMoreElements())
        {
        	String nextKey = keys.nextElement().toString();
            if (nextKey.toLowerCase().contains("font"))
            	defaults.put(nextKey, appFont);
        }
    }
    

    /**
     * Creates and configures the application's JFrame.
     * @param content - the application content panel
     */
    private void configureFrame(JPanel content)
    {
    	JFrame navFrame = new JFrame();
        navFrame = new JFrame();
        navFrame.setTitle("MIB Navigator");
        
        MIBNavigatorMenu navMenu = new MIBNavigatorMenu(this);
        
        switch (appSettings.getMibFormat())
        {
        	case SMI:
        		navMenu.setFileFilter(new SmiFileFilter());
        		break;
        		
        	case XML:
        		navMenu.setFileFilter(new XmlFileFilter());
        		break;
        }
        
        navFrame.setJMenuBar(navMenu.getMenuBar());
        
        navFrame.add(content);  // Add the browser panel to the JFrame's content pane.
        navFrame.pack();
        navFrame.setLocationRelativeTo(null);
        navFrame.setVisible(true);
        
        // This is necessary because certain interface components do not scale when the frame is resized below a set
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

        // Load preferences.
        final UserSettings settings = new UserSettings();
        settings.loadSettings();
        
        // The initialization of the MibTreeBuilder occurs first because it is the most crucial 
        // component of the application.  It also helps to decouple the rest of the application 
        // from a specific type of MibTreeBuilder (such as SMI or XML) and provide better exception 
        // handling for the XML version.
        try
        { 
            // ***Configure MIB Compiler***
        	MibTreeBuilderFactory mibTreeFactory = new MibTreeBuilderFactory();
        	final MibTreeBuilder treeBuilder = mibTreeFactory.createTreeBuilder(settings.getMibFormat());
            
            // Create and configure the interface components in the EventDispatch 
            // thread according to best practices for using Swing.
            Runnable createInterface = new 
                Runnable()
                {
                    public void run()
                    {   
                        MIBNavigator navApp = new MIBNavigator(treeBuilder, settings);
                    }
                };
                
            SwingUtilities.invokeLater(createInterface);
        }
        catch(CannotCreateBuilderException e)
        {
        	JOptionPane.showMessageDialog(null, e.getMessage(), "Application Error", JOptionPane.ERROR_MESSAGE);
        }

    }
}

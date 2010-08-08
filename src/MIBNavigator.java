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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;

import libmib.format.InvalidMibFormatException;
import libmib.mibtree.TreeBuilderCreationException;
import libmib.mibtree.MibTreeBuilder;
import libmib.mibtree.MibTreeBuilderFactory;
import settings.FileSettingsLocation;
import settings.UserSettings;
import utilities.IOUtilities;

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
    private UserSettings appSettings;   // Saves and loads application state
    private MibBrowser browser;         // Main graphical component
   
    private int maxAddresses;           // Stores the maximum number of addresses to save (currently not modifiable 
                                         // within the application, only through editing a file by hand)

    /**
     * Creates and configures the application.
     * 
     *  @param newBuilder MibTreeBuilder that is simply passed through to the MibBrowser
     *  @param settings Application user settings
     */
    public MIBNavigator(MibTreeBuilder newBuilder, UserSettings settings)
    {
    	this.appSettings = settings;
        maxAddresses = settings.MaximumAddresses.getValue();
        
        this.setDefaultFont(LookAndFeelResources.getFont("application"));

        browser = new MibBrowser(newBuilder, settings.MibDirectory.getValue());
        browser.setAddresses(settings.IPAddresses.getValue());
        browser.setPort(settings.Port.getValue());
        browser.setTimeout(settings.Timeout.getValue());
        
        this.configureFrame(browser.getBrowserPanel());
    }
    
    
    /**
     * Saves persistent attributes to the MIBNavigatorSettings object.
     */
    public void saveState()
    {
        appSettings.MaximumAddresses.setValue(maxAddresses);
        appSettings.IPAddresses.setValue(browser.getAddresses());
        appSettings.Port.setValue(browser.getPort());
        appSettings.Timeout.setValue(browser.getTimeout());
        appSettings.saveSettings();
    }
    
    
    /**
     * Sets all fonts in the UI to a given font.
     * @param defaultFont
     */
    private void setDefaultFont(Font defaultFont)
    {
        FontUIResource appFont = new FontUIResource(defaultFont);
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
    private void configureFrame(final JPanel content)
    {
    	final JFrame navFrame = new JFrame();
        navFrame.setTitle(StringResources.getString("appName"));
        
        addMenuBar(navFrame);
        
        navFrame.add(content);  // Add the browser panel to the JFrame's content pane.
        navFrame.pack();
        navFrame.setLocationRelativeTo(null);
        navFrame.setVisible(true);
        
        // This is necessary because certain interface components do not scale when the frame is resized below a set
        // initial width and/or height.  This simply uses the initial, packed size as the minimum.
        Dimension frameSize = navFrame.getSize();
        navFrame.addComponentListener(new MinimumSizeEnforcer((int)frameSize.getWidth(), (int)frameSize.getHeight()));
             
        // Catch window closing events with an anonymous event handler so that the browser's state can be saved.
        navFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        navFrame.addWindowListener(new WindowAdapter()
        {
        	@Override
            public void windowClosing(WindowEvent we)
            {
                saveState();
                navFrame.dispose();
            }
        });
    }
    
    private void addMenuBar(final JFrame navFrame)
    {
    	// Create menubar.
    	final JMenuBar menuBar = new JMenuBar();
    	final JMenu optionMenu = new JMenu(StringResources.getString("optionsMenuLabel"));
        optionMenu.setMnemonic(KeyEvent.VK_O);
        
        // Hopefully temporary hack for GTK borders.
        if (UIManager.getLookAndFeel().getID().equals("GTK"))
        {
        	optionMenu.getPopupMenu().setBorder(BorderFactory.createRaisedBevelBorder());
        }

        // Set the Add menu item.
        optionMenu.add(new AddMenuAction(navFrame));
        
        // Set the Import menu item.
		optionMenu.add(new ImportMenuAction(navFrame));
        
        optionMenu.addSeparator();

        // Set the close menu item.
        optionMenu.add(new CloseMenuAction(navFrame));
        
        menuBar.add(optionMenu);
        navFrame.setJMenuBar(menuBar);
    }
    
    private final class AddMenuAction extends AbstractAction
    {
    	private JFrame rootFrame;
    	
    	public AddMenuAction(final JFrame frame)
    	{
    		super(StringResources.getString("addMibItemLabel"));
    		this.putValue(Action.SHORT_DESCRIPTION, StringResources.getString("addMibItemTip"));
    		rootFrame = frame;
    	}

		public void actionPerformed(ActionEvent e)
		{
			final FileFilter dialogFilter = appSettings.MibFileFormat.getValue().getDialogFileFilter();
			
			// Add a new MIB to the browser's tree if it is valid.

        	// Create a file chooser with the correct filter for MIB files.
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setFileFilter(dialogFilter);
            
            JRootPane menuParentFrame = rootFrame.getRootPane();
            int result = chooser.showOpenDialog(menuParentFrame);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                File mib = chooser.getSelectedFile();
                MibTreeBuilder treeBuilder = browser.getMibBuilder();
                try
                {
                    treeBuilder.addMibFile(mib);
                }
                catch (InvalidMibFormatException exception)
                {
                    JOptionPane.showMessageDialog(menuParentFrame, exception.getMessage(), StringResources.getString("mibGeneralErrorTitle"), JOptionPane.ERROR_MESSAGE);
                }
            }
		}
    }
    
    private final class ImportMenuAction extends AbstractAction
    {
    	private JFrame rootFrame;
    	
    	public ImportMenuAction(final JFrame frame)
    	{
    		super(StringResources.getString("importMibItemLabel"));
    		this.putValue(Action.SHORT_DESCRIPTION, StringResources.getString("importMibItemTip"));
    		rootFrame = frame;
    	}

		public void actionPerformed(ActionEvent e)
		{
			final FileFilter dialogFilter = appSettings.MibFileFormat.getValue().getDialogFileFilter();
			
			// Try to add a new MIB to the browser's tree, and if it is valid, copy it into the default MIBs directory.

            // Create a file chooser with the correct filter for MIB files.
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setFileFilter(dialogFilter);
            
            JRootPane menuParentFrame = rootFrame.getRootPane();  // Use the root pane's parent frame to launch dialogs
            int result = chooser.showOpenDialog(menuParentFrame);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                File sourceMib = chooser.getSelectedFile();
                MibTreeBuilder treeBuilder = browser.getMibBuilder();
                File mibDirectory = browser.getMibDirectory();

                try
                {
                    treeBuilder.addMibFile(sourceMib);
                    
                    File destinationMib = new File(mibDirectory.getPath() + File.separator + sourceMib.getName());
                    
                    boolean proceedWithCopy = true;
                    if (destinationMib.exists())
                    {
                        // Ask the user if they want to overwrite the file if it already exists.
                        String popupMsg = 
                        	String.format(StringResources.getString("mibAlreadyExistsMessage"), destinationMib.getName(), mibDirectory.getName());
                        
                        int confirmValue = JOptionPane.showConfirmDialog(menuParentFrame, popupMsg, 
                        		StringResources.getString("mibAlreadyExistsTitle"), JOptionPane.YES_NO_OPTION);
                        
                        if (confirmValue == JOptionPane.NO_OPTION)
                            proceedWithCopy = false;
                    }

                    // If the destination file didn't already exist or the user approved an overwrite.
                    if (proceedWithCopy)
                    {
                       boolean copySucceeded = IOUtilities.copyFile(sourceMib, destinationMib);
                       
                       if (!copySucceeded)
                       {
                    	   JOptionPane.showMessageDialog(menuParentFrame, StringResources.getString("mibCopyErrorMessage"),
                			   StringResources.getString("mibCopyErrorTitle"), JOptionPane.ERROR_MESSAGE);
                       }
                    }
                    
                }
                catch (InvalidMibFormatException exception)
                {
                    JOptionPane.showMessageDialog(menuParentFrame, exception.getMessage(), 
                    		StringResources.getString("mibGeneralErrorTitle"), JOptionPane.ERROR_MESSAGE);
                }
            }
		}
    }
    
    private final class CloseMenuAction extends AbstractAction
    {
    	private JFrame rootFrame;
    	
    	public CloseMenuAction(final JFrame frame)
    	{
    		super(StringResources.getString("closeItemLabel"));
    		this.putValue(Action.SHORT_DESCRIPTION, StringResources.getString("closeItemTip"));    	
    		rootFrame = frame;
    	}
    	
		public void actionPerformed(ActionEvent e)
		{
			// Save application settings and exit.
            saveState();
        	rootFrame.dispose();
		}
    }

    
    
    /**
     * Static initializer for the MIBNavigator application.
     */
    public static void main(String args[])
    {
        try
        {  
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            // NOTE: In the event of an exception, the app will fall back to Java's Metal style.
        }

        // Load preferences.
        final UserSettings settings = new UserSettings(new FileSettingsLocation());
        settings.loadSettings();
        
        // The initialization of the MibTreeBuilder occurs first because it is the most crucial 
        // component of the application.  It also helps to decouple the rest of the application 
        // from a specific type of MibTreeBuilder (such as SMI or XML) and provide better exception 
        // handling for the XML version.
        try
        { 
            // ***Configure MIB Compiler***
        	MibTreeBuilderFactory mibTreeFactory = new MibTreeBuilderFactory();
        	final MibTreeBuilder treeBuilder = mibTreeFactory.createTreeBuilder(settings.MibFileFormat.getValue());
            
            // Create and configure the interface components in the EventDispatch 
            // thread according to best practices for using Swing.
            Runnable createInterface = new Runnable()
                {
                    public void run()
                    {   
                        new MIBNavigator(treeBuilder, settings);
                    }
                };
                
            SwingUtilities.invokeLater(createInterface);
        }
        catch(TreeBuilderCreationException e)
        {
        	JOptionPane.showMessageDialog(null, e.getMessage(), StringResources.getString("appGeneralErrorTitle"), JOptionPane.ERROR_MESSAGE);
        }

    }
}

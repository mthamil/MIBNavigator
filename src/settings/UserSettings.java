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

package settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import static utilities.IOUtilities.closeQuietly;
import utilities.NullArgumentException;
import utilities.Strings;
import utilities.events.EventListener;
import utilities.parsing.DirectoryParser;
import utilities.parsing.EnumParser;
import utilities.parsing.PositiveIntegerParser;
import utilities.parsing.Parsers;
import utilities.parsing.TokenizerParser;
import utilities.property.Property;
import utilities.property.PropertyChangeEventInfo;

import libmib.format.MibFormat;

/**
 * Class for managing application user preferences. It provides an application
 * specific layer to an underlying Properties object so that saved state values
 * can be more easily processed.
 */
public class UserSettings
{
	public final ApplicationSetting<File> MibDirectory = 
		new ApplicationSetting<File>("MibDirectory", new DirectoryParser(new File(DEFAULT_MIB_DIRECTORY)))
	{
		@Override
		public void setValue(File newValue)
		{
			if (newValue == null)
				return;
			
			super.setValue(newValue);
		}
	};
	
	public final ApplicationSetting<Integer> MaximumAddresses = 
		new PositiveIntegerSetting("MaximumAddresses", new PositiveIntegerParser(DEFAULT_MAX_ADDRESSES));
	
	private TokenizerParser tokenParser = new TokenizerParser(',');
	public final ApplicationSetting<List<String>> IPAddresses = 
		new ApplicationSetting<List<String>>("IPAddresses", tokenParser)
	{
		@Override
		public void setValue(List<String> newValue)
		{
			if (newValue == null)
				return;	

			super.setValue(newValue);
		}
	};
	
	public final ApplicationSetting<Integer> Port = 
		new PositiveIntegerSetting("Port", new PositiveIntegerParser(DEFAULT_PORT));
	
	public final ApplicationSetting<Integer> Timeout = 
		new PositiveIntegerSetting("Timeout", new PositiveIntegerParser(DEFAULT_TIMEOUT));
	
	public final ApplicationSetting<MibFormat> MibFileFormat = 
		new ApplicationSetting<MibFormat>("MibFileFormat", new EnumParser<MibFormat>(MibFormat.SMI)) {};
	

	private SettingsLocation location;
	private Properties settings;
	private boolean settingsChanged;

	private static final int DEFAULT_MAX_ADDRESSES = 15;
	private static final String DEFAULT_MIB_DIRECTORY = "." + File.separator + "mibs" + File.separator + MibFormat.SMI.toString().toLowerCase();
	private static final int DEFAULT_PORT = 161;
	private static final int DEFAULT_TIMEOUT = 4000;

	/**
	 * Initializes user settings.
	 */
	public UserSettings(SettingsLocation location)
	{
		if (location == null)
			throw new NullArgumentException("location");
		
		this.settings = new Properties();
		this.settingsChanged = false;
		this.location = location;
		
		subscribeToEvents();
	}
	
	private void subscribeToEvents()
	{
		MibDirectory.PropertyChangedEvent.addListener(new EventListener<Property<File>, 
				PropertyChangeEventInfo<File>>()
		{
			public void handleEvent(Property<File> source, PropertyChangeEventInfo<File> eventInfo)
			{
				settingsChanged = true;
			}
		});
		
		MaximumAddresses.PropertyChangedEvent.addListener(new EventListener<Property<Integer>, 
				PropertyChangeEventInfo<Integer>>()
		{
			public void handleEvent(Property<Integer> source, PropertyChangeEventInfo<Integer> eventInfo)
			{
				settingsChanged = true;
			}
		});
		
		IPAddresses.PropertyChangedEvent.addListener(new EventListener<Property<List<String>>, 
				PropertyChangeEventInfo<List<String>>>()
		{
			public void handleEvent(Property<List<String>> source, PropertyChangeEventInfo<List<String>> eventInfo)
			{
				settingsChanged = true;
			}
		});

		Port.PropertyChangedEvent.addListener(new EventListener<Property<Integer>, 
				PropertyChangeEventInfo<Integer>>()
		{
			public void handleEvent(Property<Integer> source, PropertyChangeEventInfo<Integer> eventInfo)
			{
				settingsChanged = true;
				//Port.PropertyChangedEvent.removeListener(this);
			}
		});
		
		Timeout.PropertyChangedEvent.addListener(new EventListener<Property<Integer>, 
				PropertyChangeEventInfo<Integer>>()
		{
			public void handleEvent(Property<Integer> source, PropertyChangeEventInfo<Integer> eventInfo)
			{
				settingsChanged = true;
			}
		});
		
		MibFileFormat.PropertyChangedEvent.addListener(new EventListener<Property<MibFormat>, 
				PropertyChangeEventInfo<MibFormat>>()
		{
			public void handleEvent(Property<MibFormat> source, PropertyChangeEventInfo<MibFormat> eventInfo)
			{
				settingsChanged = true;
			}
		});
	}
	
	
	/**
	 * Loads saved settings from a properties file.
	 */
	public void loadSettings()
    {
		String mibPath = "";
        String hostAddresses = "";
        String addressNum = "";
        String formatString = "";
        String portString = "";
        String timeoutString = "";
        
        InputStream settingsIn = null;
        try
        {
        	if (location.isAccessible())
        	{
        		settingsIn = location.getInput();
	            settings.loadFromXML(settingsIn);
	            
	            mibPath		  = settings.getProperty(MibDirectory.toString(), DEFAULT_MIB_DIRECTORY);
	            addressNum    = settings.getProperty(MaximumAddresses.toString(), Integer.toString(DEFAULT_MAX_ADDRESSES));
	            hostAddresses = settings.getProperty(IPAddresses.toString(), "");
	            formatString  = settings.getProperty(MibFileFormat.toString(), MibFormat.SMI.toString());
	            portString 	  = settings.getProperty(Port.toString(), Integer.toString(DEFAULT_PORT));
	            timeoutString = settings.getProperty(Timeout.toString(), Integer.toString(DEFAULT_TIMEOUT));
        	}
        }
        catch (IOException e)
        {
            System.out.println("Error loading properties file.");
        }
        finally
		{
			closeQuietly(settingsIn);
		}

        // Parse the MIB directory property.
        MibDirectory.setValue(Parsers.parseDirectory(mibPath, DEFAULT_MIB_DIRECTORY));        

        // Parse the the max address property.
        MaximumAddresses.parse(addressNum);
        
        // Parse the address list property.
        tokenParser.setTokenLimit(MaximumAddresses.getValue());
        IPAddresses.parse(hostAddresses);
        
        // Parse the MIB file format property.
        MibFileFormat.parse(formatString);
        
        // Parse the port number property.
        Port.parse(portString);
        
        // Parse the timeout property.tracking stack overflow reputation
        Timeout.parse(timeoutString);
        
        // This must be reset because the initial loading of the values will set it to true.
        settingsChanged = false;
    }
	
	
	/**
	 * Saves settings to a properties file.
	 */
	public void saveSettings()
	{
		// Write the file is either the settings changed or no settings file exists.
		if (settingsChanged || !location.isAccessible())
		{
			if (location.connect())
			{
				if (MibDirectory.getValue() != null && MibDirectory.getValue().isDirectory())
					settings.setProperty(MibDirectory.toString(), MibDirectory.getValue().getPath());
				
				if (!IPAddresses.getValue().isEmpty())
				{
					int itemCount =  Math.min(IPAddresses.getValue().size(), MaximumAddresses.getValue());
					String joinedAddresses = Strings.join(",", IPAddresses.getValue().subList(0, itemCount));
					settings.setProperty(IPAddresses.toString(), joinedAddresses);
				}
	
				settings.setProperty(MaximumAddresses.toString(), String.valueOf(MaximumAddresses.getValue()));
				settings.setProperty(MibFileFormat.toString(), MibFileFormat.getValue().toString());
				settings.setProperty(Port.toString(), String.valueOf(Port.getValue()));
				settings.setProperty(Timeout.toString(), String.valueOf(Timeout.getValue()));
	
				// Save program state settings to file.
				OutputStream settingsOut = null;
				try
				{
					settingsOut = location.getOutput();
					settings.storeToXML(settingsOut, null);
				}
				catch (IOException e)
				{
					System.out.println("Error saving properties file.");
				}
				finally
				{
					closeQuietly(settingsOut);
				}
			}
		}
	}

}

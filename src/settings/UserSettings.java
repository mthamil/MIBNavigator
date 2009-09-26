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

package settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import utilities.IOUtilities;
import utilities.StringUtilities;

import libmib.format.MibFormat;

/**
 * Class for managing application user preferences. It provides an application
 * specific layer to an underlying Properties object so that saved state values
 * can be more easily processed.
 */
public class UserSettings
{
	private enum SettingsProperties
	{
		MibDirectory, IPAddresses, MaximumAddresses, MibFileFormat, Port, Timeout
	}

	private SettingsLocation location;
	private Properties settings;
	private boolean settingsChanged;

	private static final int DEFAULT_MAX_ADDRESSES = 15;
	private static final String DEFAULT_MIB_DIRECTORY = "." + File.separator + "mibs" + File.separator + MibFormat.SMI.toString().toLowerCase();
	private static final int DEFAULT_PORT = 161;
	private static final int DEFAULT_TIMEOUT = 4000;
	
	private File mibDirectory;
	private int maxAddresses;
	private List<String> addresses;
	private MibFormat mibFormat;
	private int port;
	private int timeout;

	/**
	 * Initializes user settings.
	 */
	public UserSettings(SettingsLocation location)
	{
		if (location == null)
			throw new IllegalArgumentException("Settings storage location cannot be null.");
		
		this.settings = new Properties();
		this.settingsChanged = false;
		this.location = location;
	}

	/**
	 * Gets the maximum allowed number of stored IP addresses.
	 */
	public int getMaxAddresses()
	{
		return maxAddresses;
	}

	/**
	 * Sets the maximum allowed number of stored IP addresses. Negative values
	 * will be treated as choosing to save no addresses.
	 */
	public void setMaxAddresses(int newMax)
	{
		if (newMax < 0)
			newMax = 0;

		if (newMax != maxAddresses)
		{
			maxAddresses = newMax;
			settingsChanged = true;
		}
	}

	/**
	 * Gets the list of saved IP addresses.
	 */
	public List<String> getAddresses()
	{
		return addresses;
	}

	/**
	 * Sets the list of saved IP addresses.
	 */
	public void setAddresses(List<String> newAddresses)
	{
		if (newAddresses == null)
			return;						// Ignore null

		if (newAddresses != addresses)
		{
			// Check to to see if the Lists are equivalent.
			if (!newAddresses.equals(addresses))
			{
				addresses = newAddresses;
				settingsChanged = true;
			}
		}
	}

	/**
	 * Gets the MIB format used.
	 */
	public MibFormat getMibFormat()
	{
		return mibFormat;
	}
	
	/**
	 * Sets the MIB format used.
	 * @param format
	 */
	public void setMibFormat(MibFormat format)
	{
		if (format != mibFormat)
		{
			mibFormat = format;
			settingsChanged = true;
		}
	}
	
	/**
	 * Gets the directory where application startup MIBs are located.
	 * @return
	 */
	public File getMibDirectory()
	{
		return mibDirectory;
	}
	
	/**
	 * Sets the directory where application startup MIBs are located.
	 * @param mibDirectory
	 */
	public void setMibDirectory(File newMibDirectory)
	{
		if (newMibDirectory == null)
			return;
		
		if (!newMibDirectory.equals(mibDirectory))
		{
			mibDirectory = newMibDirectory;
			settingsChanged = true;
		}
	}
	
	/**
	 * Gets the port number.
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * Sets the port number.
	 */
	public void setPort(int newPort)
	{
		if (newPort < 0)
			newPort = 0;

		if (newPort != port)
		{
			port = newPort;
			settingsChanged = true;
		}
	}
	
	/**
	 * Gets the timeout.
	 */
	public int getTimeout()
	{
		return timeout;
	}

	/**
	 * Sets the timeout.
	 */
	public void setTimeout(int newTimeout)
	{
		if (newTimeout < 0)
			newTimeout = 0;

		if (newTimeout != timeout)
		{
			timeout = newTimeout;
			settingsChanged = true;
		}
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
	            
	            mibPath		  = settings.getProperty(SettingsProperties.MibDirectory.toString(), DEFAULT_MIB_DIRECTORY);
	            addressNum    = settings.getProperty(SettingsProperties.MaximumAddresses.toString(), Integer.toString(DEFAULT_MAX_ADDRESSES));
	            hostAddresses = settings.getProperty(SettingsProperties.IPAddresses.toString(), "");
	            formatString  = settings.getProperty(SettingsProperties.MibFileFormat.toString(), MibFormat.SMI.toString());
	            portString 	  = settings.getProperty(SettingsProperties.Port.toString(), Integer.toString(DEFAULT_PORT));
	            timeoutString = settings.getProperty(SettingsProperties.Timeout.toString(), Integer.toString(DEFAULT_TIMEOUT));
        	}
        }
        catch (IOException e)
        {
            System.out.println("Error loading properties file.");
        }
        finally
		{
			IOUtilities.closeQuietly(settingsIn);
		}

        // Parse the MIB directory property.
        mibDirectory = PropertyParser.parseDirectoryProperty(mibPath, DEFAULT_MIB_DIRECTORY);        

        // Parse the the max address property.
        maxAddresses = PropertyParser.parseIntegerProperty(addressNum, DEFAULT_MAX_ADDRESSES);
        
        // Parse the address list property.
        addresses = PropertyParser.parseDelimitedProperty(hostAddresses, ',', maxAddresses);
        
        // Parse the MIB file format property.
        mibFormat = PropertyParser.parseEnumProperty(formatString, MibFormat.SMI);
        
        // Parse the port number property.
        port = PropertyParser.parseIntegerProperty(portString, DEFAULT_PORT);
        
        // Parse the timeout property.
        timeout = PropertyParser.parseIntegerProperty(timeoutString, DEFAULT_TIMEOUT);
    }
	
	
	/**
	 * Saves settings to a properties file.
	 */
	public void saveSettings()
	{
		// Write the file is either the settings changed or no settings file exists.
		if (settingsChanged || !location.isAccessible())
		{
			if (mibDirectory != null && mibDirectory.isDirectory())
				settings.setProperty(SettingsProperties.MibDirectory.toString(), mibDirectory.getPath());
			
			if (!addresses.isEmpty())
			{
				int itemCount =  Math.min(addresses.size(), maxAddresses);
				String joinedAddresses = StringUtilities.join(",", addresses.subList(0, itemCount));
				settings.setProperty(SettingsProperties.IPAddresses.toString(), joinedAddresses);
			}

			settings.setProperty(SettingsProperties.MaximumAddresses.toString(), String.valueOf(maxAddresses));
			settings.setProperty(SettingsProperties.MibFileFormat.toString(), mibFormat.toString());
			settings.setProperty(SettingsProperties.Port.toString(), String.valueOf(port));
			settings.setProperty(SettingsProperties.Timeout.toString(), String.valueOf(timeout));

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
				IOUtilities.closeQuietly(settingsOut);
			}
		}
	}

}

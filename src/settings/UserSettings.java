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

package settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import utilities.Utilities;

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
		MibDirectory, IPAddresses, MaximumAddresses, MibFileFormat
	}

	private static final String SETTINGS_FOLDERNAME = ".mibnavigator";
	private static final int DEFAULT_MAX_ADDRESSES = 15;
	private static final String DEFAULT_MIB_DIRECTORY = "." + File.separator + "mibs";
	private File settingsFile;

	private Properties settings;
	private boolean settingsChanged;
	
	private File mibDirectory;
	private int maxAddresses;
	private List<String> addresses;
	private MibFormat mibFormat;

	/**
	 * Initializes user settings.
	 */
	public UserSettings()
	{
		settings = new Properties();
		settingsChanged = false;
		String settingsDirPath = System.getProperty("user.home") + File.separator + SETTINGS_FOLDERNAME
			+ File.separator;
		settingsFile = new File(settingsDirPath + "properties.xml");
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
	 * Loads saved settings from a properties file.
	 */
	public void loadSettings()
    {
		String mibPath = "";
        String hostAddresses = "";
        String addressNum = "";
        String formatString = "";
        FileInputStream settingsIn = null;
        try
        {
        	if (settingsFile.exists())
        	{
	            settingsIn = new FileInputStream(settingsFile);
	            settings.loadFromXML(settingsIn);
	            
	            mibPath		  = settings.getProperty(SettingsProperties.MibDirectory.toString(), DEFAULT_MIB_DIRECTORY);
	            addressNum    = settings.getProperty(SettingsProperties.MaximumAddresses.toString(), Integer.toString(DEFAULT_MAX_ADDRESSES));
	            hostAddresses = settings.getProperty(SettingsProperties.IPAddresses.toString(), "");
	            formatString  = settings.getProperty(SettingsProperties.MibFileFormat.toString(), MibFormat.SMI.toString());
        	}
        }
        catch (IOException e)
        {
            System.out.println("Error loading properties file.");
        }
        finally
		{
			Utilities.closeQuietly(settingsIn);
		}
        
        // Parse the mib directory property.
        mibDirectory = parseMibDirectory(mibPath);        
        
        // Parse the the max address property.
        maxAddresses = parseMaxAddresses(addressNum);
        
        // Parse the address list property.
        addresses = parseAddresses(hostAddresses);
        
        // Parse the MIB file format property.
        mibFormat = parseMibFormat(formatString);
    }
	
	
	/**
	 * Saves settings to a properties file.
	 */
	public void saveSettings()
	{
		if (settingsChanged)
		{
			if (mibDirectory != null && mibDirectory.isDirectory())
			{
				settings.setProperty(SettingsProperties.MibDirectory.toString(), mibDirectory.getPath());
			}
			
			if (!addresses.isEmpty())
			{
				// Put IP addresses into a single, comma delimited string. The
				// addresses that are saved are limited to the first N 
				// addresses of the list, with N determined by maxAddresses.
				StringBuilder ipAddresses = new StringBuilder();

				int maxItems = Math.min(addresses.size(), maxAddresses);
				for (int i = 0; i < maxItems; i++)
					ipAddresses.append("," + addresses.get(i));

				// Trim off the leading comma.
				String addresses = ipAddresses.toString();
				if (addresses.startsWith(","))
					addresses = addresses.substring(addresses.indexOf(",") + 1);

				settings.setProperty(SettingsProperties.IPAddresses.toString(), addresses);
			}

			settings.setProperty(SettingsProperties.MaximumAddresses.toString(), 
					String.valueOf(maxAddresses));
			
			settings.setProperty(SettingsProperties.MibFileFormat.toString(), mibFormat.toString());

			// Save program state settings to file.
			FileOutputStream settingsOut = null;
			try
			{
				settingsOut = new FileOutputStream(settingsFile);
				settings.storeToXML(settingsOut, null);
			}
			catch (IOException e)
			{
				System.out.println("Error saving properties file.");
			}
			finally
			{
				Utilities.closeQuietly(settingsOut);
			}
		}
	}
	
	private File parseMibDirectory(String mibPath)
	{
		// If this is a local relative path, try to convert
		// the file path separators to the current OS.
		if (mibPath.startsWith("."))
		{
			mibPath = mibPath.replace("/", File.separator);
			mibPath = mibPath.replace("\\", File.separator);
		}
		
		File mibDir = new File(mibPath);
		if (!mibDir.isDirectory())
		{
			// If the path isn't a directory or doesn't exist, use the default.
			mibDir = new File(DEFAULT_MIB_DIRECTORY);
		}

		return mibDir;
	}
	

	/**
	 * Parses the max address property value from a string.
	 */
	private int parseMaxAddresses(String maxString)
	{
		int max;

		if (!maxString.equals(""))
		{
			try
			{
				max = Integer.parseInt(maxString);

				if (max < 0) // Why anyone would do this, I don't know.
					max = 0;
			}
			catch (NumberFormatException e)
			{
				max = DEFAULT_MAX_ADDRESSES;
			}
		}
		else
			max = DEFAULT_MAX_ADDRESSES;

		return max;
	}

	
	/**
	 * Parses the address list property.  If the property 
	 * retrieved was an empty string, an empty list will 
	 * be created.
	 * @param addresses
	 * @return
	 */
	private List<String> parseAddresses(String addressString)
	{
		List<String> newAddresses = new ArrayList<String>(maxAddresses);
		
		StringTokenizer tokenizer = new StringTokenizer(addressString, ",");
        int i = 0;
        while (tokenizer.hasMoreTokens() && i < maxAddresses)
        {
        	newAddresses.add(tokenizer.nextToken());
            i++;
        }
		
		return newAddresses;
	}
	
	/**
	 * Attempts to convert a string to a MibFormat enum value.
	 * If this fails, the default will be used.
	 * @param mibFormatString
	 * @return
	 */
	private MibFormat parseMibFormat(String mibFormatString)
	{
		MibFormat format = null;
		try
		{
			format = MibFormat.valueOf(mibFormatString);
		}
		catch (IllegalArgumentException exception)
		{
			format = MibFormat.SMI;
		}
		
		return format;
	}
	
}

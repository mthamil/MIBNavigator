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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A settings storage location backed by a file.
 */
public class FileSettingsLocation implements SettingsLocation
{
	private static final String SETTINGS_FOLDERNAME = ".mibnavigator";
	private static final File SETTINGS_PATH = 
		new File(System.getProperty("user.home") + File.separator + SETTINGS_FOLDERNAME);
	
	private File settingsFile;
	
	/**
	 * Creates a new settings file location with a default file path.
	 */
	public FileSettingsLocation()
	{
		this(SETTINGS_PATH  + File.separator + "properties.xml");
	}
	
	/**
	 * Creates a new settings file location with the given file path.
	 * @param settingsFilePath
	 */
	public FileSettingsLocation(String settingsFilePath)
	{
		settingsFile = new File(settingsFilePath);
	}

	/* (non-Javadoc)
	 * @see settings.SettingsLocation#isAccessible()
	 */
	public boolean isAccessible()
	{
		return settingsFile.exists();
	}
	
	/* (non-Javadoc)
	 * @see settings.SettingsLocation#connect()
	 */
	public boolean connect()
	{
		// Create a new settings file if necessary.
		boolean successful = true;
		try
		{
			// Create the settings directory if necessary.
			if (!SETTINGS_PATH.exists())
				SETTINGS_PATH.mkdir();

			settingsFile.createNewFile();
		}
		catch (IOException e)
		{
			successful = false;
		}
		catch (SecurityException e)
		{
			successful = false;
		}
		
		return successful;
	}
	
	/* (non-Javadoc)
	 * @see settings.SettingsLocation#getInputStream()
	 * @throws IOException 
	 */
	public InputStream getInput() throws IOException
	{
		return new FileInputStream(settingsFile);
	}

	/* (non-Javadoc)
	 * @see settings.SettingsLocation#getOutputStream()
	 * @throws IOException 
	 */
	public OutputStream getOutput() throws IOException
	{
		return new FileOutputStream(settingsFile);
	}
}

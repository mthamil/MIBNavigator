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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Class containing static helper methods for parsing app 
 * settings properties.
 */
public class PropertyParser
{
	private PropertyParser() { }
	
	/**
	 * Parses a file directory from a string.
	 * @param path the string path to parse
	 * @param defaultPath the path to use if the path doesn't exist or isn't a directory
	 * @return
	 */
	public static File parseDirectoryProperty(String path, String defaultPath)
	{
		// If this is a local relative path, try to convert
		// the file path separators to the current OS.
		if (path.startsWith("."))
		{
			path = path.replace("/", File.separator);
			path = path.replace("\\", File.separator);
		}
		
		File directory = new File(path);
		if (!directory.isDirectory())
		{
			// If the path isn't a directory or doesn't exist, use the default.
			directory = new File(defaultPath);
		}

		return directory;
	}
	
	/**
	 * Parses a string delimited by a given character into a list.  The maxEntries
	 * parameter can be used to limit how many delimited entries to return.
	 * @param delimitedString the delimited string to parse
	 * @param delimiter the delimiting character
	 * @param maxEntries the maximum number of entries to parse
	 * @return
	 */
	public static List<String> parseDelimitedProperty(String delimitedString, char delimiter, int maxEntries)
	{
		List<String> entries = new ArrayList<String>(maxEntries);
		
		StringTokenizer tokenizer = new StringTokenizer(delimitedString, String.valueOf(delimiter));
        int i = 0;
        while (tokenizer.hasMoreTokens() && i < maxEntries)
        {
        	entries.add(tokenizer.nextToken());
            i++;
        }
		
		return entries;
	}

	
	/**
	 * Parses an Enum value from a string.
	 * @param <T> the Enum type
	 * @param enumString the string to parse
	 * @param defaultValue the Enum value to use if parsing fails
	 * @return
	 */
	public static <T extends Enum<T>> T parseEnumProperty(String enumString, T defaultValue)
	{
		T enumValue = null;
		try
		{
			enumValue = T.valueOf(defaultValue.getDeclaringClass(), enumString);
		}
		catch (IllegalArgumentException exception)
		{
			enumValue = defaultValue;
		}
		
		return enumValue;
	}
	
	
	/**
	 * Parses a positive integer value property from a string.
	 * @param valueString the string to parse
	 * @param defaultValue the default value to use if parsing fails
	 * @return
	 */
	public static int parseIntegerProperty(String valueString, int defaultValue)
	{
		if (valueString == null || valueString.equals(""))
			return defaultValue;
		
		int value;
		try
		{
			value = Integer.parseInt(valueString);

			if (value < 0) // Why anyone would do this, I don't know.
				value = 0;
		}
		catch (NumberFormatException e)
		{
			value = defaultValue;
		}
		
		return value;
	}
}

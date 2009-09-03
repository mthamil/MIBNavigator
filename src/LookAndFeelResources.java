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

import java.awt.Dimension;
import java.awt.Font;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.UIManager;

/**
 * Wrapper class around platform look and feel specific property resource bundles.
 */
public class LookAndFeelResources
{
	private static final String BUNDLE_NAME = UIManager.getLookAndFeel().getID() + "Resources";
	private static ResourceBundle RESOURCE_BUNDLE;
	
	private static final String DEFAULT_BUNDLE_NAME = "MetalResources";
	private static final ResourceBundle DEFAULT_BUNDLE = ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME);
	
	static
	{
		// Find and load platform specific resource bundle.
		try
		{
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
		}
		catch (MissingResourceException missingBundleEx)
		{
			// If no bundle exists for this platform, use the default.
			RESOURCE_BUNDLE = DEFAULT_BUNDLE;
		}
	}

	private LookAndFeelResources() { }
	
	/**
	 * Gets a string resource value with the given key.
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key)
	{
		if (RESOURCE_BUNDLE.containsKey(key))
			return RESOURCE_BUNDLE.getString(key);
		
		// If the key couldn't be found in the platform-specific bundle,
		// try the default bundle.
		try
		{
			return DEFAULT_BUNDLE.getString(key);
		}
		catch (MissingResourceException ex)
		{
			return '!' + key + '!';
		}
	}
	
	/**
	 * Gets an integer resource value with the given key.
	 * 
	 * @param key
	 * @return
	 */
	public static int getInteger(String key)
	{
		try
		{
			if (RESOURCE_BUNDLE.containsKey(key))
				return Integer.parseInt(RESOURCE_BUNDLE.getString(key));
			
			// If the key couldn't be found in the platform-specific bundle,
			// try the default bundle.
			try
			{
				return Integer.parseInt(DEFAULT_BUNDLE.getString(key));
			}
			catch (MissingResourceException ex)
			{
				return 0;
			}
		}
		catch (NumberFormatException numberException)
		{
			return 0;
		}
	}
	
	private static final String WIDTH_SUFFIX = ".width";
	private static final String HEIGHT_SUFFIX = ".height";
	
	/**
	 * Gets a Dimension resource value with the given key prefix.
	 * The resource keys should follow the following format:
	 * keyPrefix.width and keyPrefix.height
	 * 
	 * @param keyPrefix
	 * @return
	 */
	public static Dimension getDimension(String keyPrefix)
	{
		String widthKey = keyPrefix + WIDTH_SUFFIX;
		int width = LookAndFeelResources.getInteger(widthKey);
		
		String heightKey = keyPrefix + HEIGHT_SUFFIX;
		int height = LookAndFeelResources.getInteger(heightKey);
		
		return new Dimension(width, height);
	}
	
	
	private static final String FONT_SUFFIX = ".font";
	
	/**
	 * Gets a Font resource with the given prefix.
	 * The resource keys should follow the following format:
	 * keyPrefix.font
	 * 
	 * @param keyPrefix
	 * @return
	 */
	public static Font getFont(String keyPrefix)
	{
		String fontKey = keyPrefix + FONT_SUFFIX;
		String fontString = LookAndFeelResources.getString(fontKey);
		
		Font font = Font.decode(fontString);
		return font;
	}

	
	/**
	 * Gets an enum resource with the given prefix.  The default value is
	 * what should be used if the key cannot be found or parsed.
	 * @param <T> the type of enum
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static <T extends Enum<T>> T getEnum(String key, T defaultValue)
	{
		String value = LookAndFeelResources.getString(key);
		T enumValue = null;
		try
		{
			enumValue = T.valueOf(defaultValue.getDeclaringClass(), value);
		}
		catch (IllegalArgumentException exception)
		{
			enumValue = defaultValue;
		}
		
		return enumValue;
	}
}

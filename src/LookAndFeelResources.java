import java.awt.Dimension;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.UIManager;

/**
 * Generated wrapper class around platform look and feel specific property resource bundles.
 */
public class LookAndFeelResources
{
	private static final String BUNDLE_NAME = UIManager.getLookAndFeel().getID() + "Resources";
	private static final String DEFAULT_BUNDLE_NAME = "MetalResources";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	private static final ResourceBundle DEFAULT_BUNDLE = ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME);

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
}

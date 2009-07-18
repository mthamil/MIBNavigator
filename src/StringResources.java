import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Generated wrapper class around externalized string resource bundle.
 */
public class StringResources
{
	private static final String BUNDLE_NAME = "StringResources";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private StringResources() { }

	public static String getString(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
}

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Generated wrapper class around resource bundle.
 *
 */
public class Resources
{
	private static final String BUNDLE_NAME = "Resources";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Resources() { }

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

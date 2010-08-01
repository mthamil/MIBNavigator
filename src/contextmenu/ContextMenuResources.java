package contextmenu;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Generated wrapper class around resource bundle.
 */
public final class ContextMenuResources
{
	private static final String BUNDLE_NAME = "contextmenu.ContextMenuResources";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	public enum Keys
	{
		CopyLabel { public String getKeyString() { return "copyLabel"; } },
		PasteLabel { public String getKeyString() { return "pasteLabel"; } },
		SelectAllLabel { public String getKeyString() { return "selectAllLabel"; } };
		
		public abstract String getKeyString();
	}

	private ContextMenuResources() { }

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
	
	public static String getString(Keys key)
	{
		return ContextMenuResources.getString(key.getKeyString());
	}
}

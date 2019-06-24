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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An interface for the actual storage location of settings data. This
 * abstracts away the actual physical location in that settings could
 * be stored in a local file or even in a network location.
 */
public interface SettingsLocation
{
	/**
	 * Whether a settings storage location exists and is accessible.
	 * @return
	 */
	public boolean isAccessible();
	
	/**
	 * Attempts to establish a connection to a settings location.
	 * @return whether the connection was successful or not.
	 */
	public boolean connect();

	/**
	 * Returns an input stream for the storage location.
	 * @throws IOException 
	 */
	public InputStream getInput() throws IOException;
	
	/**
	 * Returns an output stream for the storage location.
	 * @return
	 * @throws IOException 
	 */
	public OutputStream getOutput() throws IOException;
}

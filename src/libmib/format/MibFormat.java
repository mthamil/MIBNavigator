/**
 * libmib - Java SNMP Management Information Base Library
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

package libmib.format;

import java.io.FilenameFilter;

import javax.swing.filechooser.FileFilter;

import filefilters.*;

/**
 * Defines accepted MIB file formats.
 */
public enum MibFormat 
{ 
	SMI
	{
		public FileFilter getDialogFileFilter() { return new SmiFileFilter(); }
		public FilenameFilter getFilenameFilter() { return new SmiFilenameFilter(); }
	},
	
	XML
	{
		public FileFilter getDialogFileFilter() { return new XmlFileFilter(); }
		public FilenameFilter getFilenameFilter() { return new XmlFilenameFilter(); }
	};

	
	/**
	 * Gets a file filter for a given MIB format that is usable in a file picker dialog.
	 * @return a new file filter
	 */
	public abstract FileFilter getDialogFileFilter();
	
	/**
	 * Gets a filename filter for a given MIB format that is usable for IO operations.
	 * @return a new filename filter
	 */
	public abstract FilenameFilter getFilenameFilter();
}

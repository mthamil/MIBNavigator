/**
 * libmib - Java SNMP Management Information Base Library
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

package libmib;

import java.util.ArrayList;
import java.util.List;

/**
 *  This class represents a MIB definition import statement.
 *  It consists of a list of one or more general symbols that
 *  are imported from a source MIB.
 */
 public class MibImport extends Object
 {
 	private List<String> imports = null;
 	private String source;
    
 	/**
     * Constructs a new default MibImport with an ArrayList of imported names.
     */
    public MibImport()
    {
        imports = new ArrayList<String>();
    }
    
	/**
	 * Constructs a new MibImport with an ArrayList of imported item names
     * and a source MIB name.
     * 
	 * @param sourceName the name of the MIB containing this item
	 */
	public MibImport(final String sourceName)
	{
		imports = new ArrayList<String>();
        source = sourceName;
	}
	
    /**
     * Adds an import item to the import list.
     * 
     * @param item the item to add to the list
     */
    public void addImport(String item)
    {
        imports.add(item);
    }

	/**
	 * Gets the list of names of items being imported from the source.
     * 
	 * @return the list of names of items being imported
	 */
	public List<String> getImports()
	{
		return imports;
	}
    
    /**
     * Sets the list of names of items being imported from the source.
     * 
     * @param newImports the list of names of items being imported
     */
    public void setImports(List<String> newImports)
    {
        if (newImports == null)
            throw new IllegalArgumentException("Import list cannot be set to null.");
        
        imports = newImports;
    }

    
	/**
	 * Gets the import's source MIB.
     * 
	 * @return the name of the imported item's source MIB
	 */
	public String getSource()
	{
		return source;
	}
    
	/**
     * Sets the import's source MIB.
     * 
     * @param newSource the name of the imported item's source MIB
     */
    public void setSource(String newSource)
    {
        source = newSource;
    }
}
/**
 * MIB Navigator
 *
 * Copyright (C) 2005, Matt Hamilton <matthew.hamilton@washburn.edu>
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

package libmib.mibtree;

import java.io.*;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import libmib.MibFormat;

/**
 * Class that creates an appropriate tree builder for
 * a MIB format type.
 */
public class MibTreeBuilderFactory
{
	private File schemaFile;
	
	/**
	 * Default constructor.
	 */
	public MibTreeBuilderFactory() 
	{ 
		schemaFile = new File("." + File.separator + "mib.xsd");
	}
	
	
	/**
	 * Returns the MibTreeBuilder corresponding to
	 * the given MIB format type.
	 * @param mibFormat
	 * @return
	 * @throws CannotCreateBuilderException 
	 */
	public MibTreeBuilder createTreeBuilder(MibFormat mibFormat) throws CannotCreateBuilderException
	{
		switch(mibFormat)
		{
			case ASN:
				return new MibTreeBuilderSmi();
				
			case XML:
	            try
	            {
	            	// If the schema file used for parsing and validating MIB files
	            	// can't be found then there is no point in continuing since 
	            	// XML files can't be added later.
		            if (!schemaFile.exists())
		            {    
		            	String message = "The schema file, " + schemaFile.getName() + ", was not found.";
		            	FileNotFoundException cause = new FileNotFoundException(message);
		            	throw new CannotCreateBuilderException(cause);
		            }
		            
	            	return new MibTreeBuilderXml(schemaFile);

	            }
	            catch (SAXException e)
	            {
	            	throw new CannotCreateBuilderException("An error occurred while parsing the schema file.", e);
	            }
	            catch (ParserConfigurationException e)
	            {
	            	throw new CannotCreateBuilderException("An error occurred configuring the XML parser.", e);
	            }
		}
		
		return null;
	}
}

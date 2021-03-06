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

package libmib.format;

import java.io.File;

public class InvalidMibFormatException extends Exception
{
    private File invalidMIBFile;
    
    /**
     *  Creates a new InvalidMibFormatException containing the
     *  default message: "This file is not a valid MIB file."
     */
    public InvalidMibFormatException() 
    {
        super("This file is not a valid MIB file.");
        invalidMIBFile = null;
    }
    
    /**
     * Creates a new InvalidMibFormatException with the specified error
     * message and the File that caused the exception.
     * 
     * @param errorMessage the String error message
     * @param newInvalidFile the MIB file that caused the exception
     */
    public InvalidMibFormatException(String errorMessage, File newInvalidFile)
    {
        super(errorMessage);
        invalidMIBFile = newInvalidFile;
    }
    
    /**
     * Creates a new InvalidMibFormatException with the invalid File that caused the exception
     * and an error message containing the name of this File.
     * 
     * @param newInvalidFile the MIB file that caused the exception
     */
    public InvalidMibFormatException(File newInvalidFile) 
    {
        super("The file \"" + newInvalidFile.getName() + "\" is not a valid MIB file.");
        
        invalidMIBFile = newInvalidFile;
    }
    
    /**
     * Returns the File that caused this exception to be thrown.
     * 
     * @return the invalid MIB file that caused the exception.
     */
    public File getInvalidMIBFile()
    {
        return invalidMIBFile;
    }

}

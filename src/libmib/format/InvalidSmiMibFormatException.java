/**
 * libmib - Java SNMP Management Information Base Library
 *
 * Copyright (C) 2008, Matt Hamilton <mhamilton2383@comcast.net>
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

public class InvalidSmiMibFormatException extends InvalidMibFormatException
{

    /**
     *  Creates a new InvalidSmiMibFormatException containing the
     *  default message: "This file is not a valid MIB file."
     */
    public InvalidSmiMibFormatException() 
    {
        super();
    }
    
    /**
     * Creates a new InvalidSmiMibFormatException containing the File that
     * caused the exception to be thrown and an extra String allowing the 
     * specification of more details.
     * 
     * @param extraDetails a String containing any extra details about the exception
     * @param newInvalidFile
     */
    public InvalidSmiMibFormatException(String extraDetails, File newInvalidFile) 
    {
        super("The file \"" + newInvalidFile.getName() + "\" is not a valid SMI MIB file." + extraDetails, newInvalidFile);
    }
    
    /**
     * Creates a new InvalidSmiMibFormatException with the invalid File that caused the exception
     * and an error message containing the name of this File.
     * 
     * @param newInvalidFile the MIB file that caused the exception
     */
    public InvalidSmiMibFormatException(File newInvalidFile) 
    {
        super(newInvalidFile); 
    }

}

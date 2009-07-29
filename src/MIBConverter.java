/**
 * libmib - Java SNMP Management Information Base Library
 * MIB To XML Converter
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

import java.io.File;

import javax.swing.JFileChooser;

import libmib.format.InvalidSmiMibFormatException;
import libmib.format.MibToXmlConverter;

/**
 * Basic wrapper app around MibToXmlConverter.
 */
public class MIBConverter
{
	public static void main(String args[])
	{   
        // some basic code to execute the converter
        File inFile = null;
        File outFile = null;
        
        if (args.length > 0)
        {
            inFile = new File(args[0].trim());  		// first option as input file
        	
        	if (args.length > 1)
        		outFile = new File(args[1].trim());		// second option as output file
        }
        else  
        {
            // If there were no command line options, pop up a file dialog.
            JFileChooser fc = new JFileChooser(new File("."));
            int retVal = fc.showOpenDialog(null);
    
            if (retVal == JFileChooser.APPROVE_OPTION)
            {
                inFile = fc.getSelectedFile();
            
	            retVal = fc.showSaveDialog(null);
	            if (retVal == JFileChooser.APPROVE_OPTION)
	                outFile = fc.getSelectedFile();
            }
        }
        
        if (inFile != null && inFile.exists())
        {
            try
            {
                if (outFile == null)
                {
                    // Construct the output xml file's name to be the same as the 
                    // input file but with a different extension.
                	
                    String mibFilename = inFile.getName();
                    if (mibFilename.contains(".")) 
                        mibFilename = mibFilename.substring(0, mibFilename.lastIndexOf("."));
                    
                    outFile = new File(mibFilename + ".xml");
                }
                else if (outFile.isDirectory())
                {
                	String mibFilename = inFile.getName();
                    if (mibFilename.contains(".")) 
                        mibFilename = mibFilename.substring(0, mibFilename.lastIndexOf("."));
                    
                    outFile = new File(outFile.getAbsoluteFile() + File.separator + mibFilename + ".xml");
                }

                
                MibToXmlConverter converter = new MibToXmlConverter();
                converter.readMIB(inFile);
                converter.writeXML(outFile);
            }
            catch (InvalidSmiMibFormatException e)
            {
                System.out.println(e.getMessage());
            }
        }
        else
        {
        	String fileMessage = (inFile == null) ? "File" : inFile.getName();
            System.out.println("Error Opening File: " + fileMessage + " not found.");
        }
    }
}

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

package libmib.format.smi.parsers;

import static libmib.format.smi.SMIToken.SYNTAX;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import libmib.MibSyntax;
import libmib.format.smi.SMIStructureHandler;

public class SyntaxParser extends AbstractParser<MibSyntax>
{
	@Override
	public MibSyntax parse(BufferedReader reader, String currentLine) throws IOException
	{
		if (currentLine.equals(SYNTAX.token()))
			currentLine = reader.readLine();

        // If the OID has a list of specific integer values
		String dataType = "";
		Map<Integer, String> valuePairs = null;
        if (currentLine != null)
        {
        	currentLine = currentLine.trim();
            if (currentLine.contains("{"))
            {
                int index = currentLine.indexOf("{");
                dataType = currentLine.substring(SYNTAX.token().length(), index).trim();
                
                valuePairs = SMIStructureHandler.readPairs(reader, currentLine, SYNTAX);
            }
            else
            	dataType = currentLine.substring(SYNTAX.token().length()).trim();
        }
        
        // Construct a Syntax object; an object's type is the only thing
        // required for the existence of a syntax element.
        if (!dataType.equals("")) 
        {
            MibSyntax syntax = new MibSyntax(dataType);
            if (valuePairs != null)
            	syntax.setValuePairs(valuePairs);
            
            return syntax;
        }
        
        return null;
	}
}

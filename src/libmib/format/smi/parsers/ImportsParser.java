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

import static libmib.format.smi.SMIToken.COMMENT;
import static libmib.format.smi.SMIToken.SOURCE;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import libmib.MibImport;
import libmib.format.smi.SMIStructureHandler;

public class ImportsParser implements SMIParser<List<MibImport>>
{
	public List<MibImport> parse(BufferedReader reader, String currentLine) throws IOException
	{
		// Read the entire IMPORTS section into a StringBuilder.
        char c = 0;
        StringBuilder importSection = new StringBuilder("");
        while (c != -1 && c != ';')
        {
            c = (char)reader.read();
            importSection.append(c);
        }
        
        String[] importLines = importSection.toString().trim().split(SMIStructureHandler.NEWLINE_REGEX + "|,");
        
        List<MibImport> imports = new ArrayList<MibImport>();
        for (int i = 0; i < importLines.length; i++) // iterate through tokens
        {
        	MibImport importItem = new MibImport();
            boolean endOfImport = false;
            while (!endOfImport && i < importLines.length)
            {
                String element = importLines[i].trim();
                
                // Strip comments.
                if (element.contains(COMMENT.token()))
                {
                    int commentIndex = element.indexOf(COMMENT.token());
                    element = element.substring(0, commentIndex).trim();
                }
                
                // Skip empty lines.
                if (element.equals(""))
                {
                    i++;
                    continue;
                }
                
                if (element.contains(SOURCE.token())) // identify source MIBS
                {
                    String source = element.trim();
                    
                    // take care of imports that get put in the same string as the FROM statement
                    if (!source.startsWith(SOURCE.token()))
                    {
                        String item = source.substring(0, source.indexOf(SOURCE.token())).trim();
                        importItem.addImport(item);
                    }
                    
                    // don't include the ';' at the end
                    if (source.endsWith(";"))
                        source = source.substring(0, source.length() - 1);
                    
                    source = source.substring(source.indexOf(SOURCE.token()) 
                             + SOURCE.token().length()).trim();
                    importItem.setSource(source);
                    
                    endOfImport = true;
                    imports.add(importItem);
                }
                else
                {
                    importItem.addImport(element.trim());
                }
                
                if (!endOfImport)
                    i++;
            }
        }
        
        return imports;
	}
}

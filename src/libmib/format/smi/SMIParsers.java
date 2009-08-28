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

package libmib.format.smi;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static libmib.format.smi.SMIToken.*;
import libmib.MibImport;
import libmib.MibModuleIdRevision;
import libmib.MibSyntax;
import libmib.MibObjectType.Access;
import libmib.MibObjectType.Status;

/**
 * Contains a mapping of SMI tokens to their corresponding parsers.
 */
public class SMIParsers
{
	private static final Map<SMIToken, AbstractStructureParser<?>> parsers = 
		new HashMap<SMIToken, AbstractStructureParser<?>>();
	
	/**
	 * Initializes the parser mapping.
	 */
	static
	{
		// Add Access parser.
		parsers.put(ACCESS, new AbstractStructureParser<Access>()
		{
			@Override
			public Access parse(BufferedReader reader, String currentLine) throws IOException
			{
				int index = currentLine.indexOf(ACCESS.token()) + ACCESS.token().length();
                String access = currentLine.substring(index).trim();
                
	            if (!access.equals(""))
	                return Access.valueOf(access.toUpperCase().replaceAll("-", "_"));
	            
	            return null;
			}
		});
		
		// Add Status parser.
		parsers.put(STATUS, new AbstractStructureParser<Status>()
		{
			@Override
			public Status parse(BufferedReader reader, String currentLine) throws IOException
			{
                int index = currentLine.indexOf(STATUS.token()) + STATUS.token().length();
                String status = currentLine.substring(index).trim();
                
	            if (!status.equals(""))
	                return Status.valueOf(status.toUpperCase());
	            
	            return null;
			}
		});
		
		// Add Syntax parser.
		parsers.put(SYNTAX, new AbstractStructureParser<MibSyntax>()
		{
			@Override
			public MibSyntax parse(BufferedReader reader, String currentLine) throws IOException
			{
				if (currentLine.equals(SYNTAX))
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
		});
		
		// Add Description parser.
		parsers.put(DESCRIPTION, new AbstractStructureParser<String>()
		{
			@Override
			public String parse(BufferedReader reader, String currentLine) throws IOException
			{
				return SMIStructureHandler.readQuotedSection(reader, currentLine, DESCRIPTION);
			}
		});
		
		// Add Revision parser.
		parsers.put(MODULE_REVISION, new AbstractStructureParser<MibModuleIdRevision>()
		{
			@Override
			public MibModuleIdRevision parse(BufferedReader reader, String currentLine) throws IOException
			{
                // testing rev
                String revisionId = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")).trim();
                
                // As far as I've seen, a REVISION is always followed by a DESCRIPTION block, 
                // but this may not always be true.  If it's not, there could be consequences 
                // and repercussions.
                currentLine = reader.readLine();
                if (currentLine != null)
                {
                	currentLine = currentLine.trim();
                    String revDesc = "";
                    if (currentLine.contains(DESCRIPTION.token()))
                        revDesc = SMIStructureHandler.readQuotedSection(reader, currentLine, DESCRIPTION);
                    
                    return new MibModuleIdRevision(revisionId, revDesc);
                }
                
                return null;
			}
		});
		
		
		// Add IMPORTS parser.
		parsers.put(IMPORTS, new AbstractStructureParser<List<MibImport>>()
		{
			@Override
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
		});
		
	}

	/**
	 * Retrieves the parser for a given token type.
	 * @param token
	 * @return
	 */
	public static AbstractStructureParser<?> getParser(SMIToken token)
	{
		return parsers.get(token);
	}
}

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

package libmib;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import libmib.oid.MibNameValuePair;

/**
 * Class that handles various block structure parsing when reading SMI format MIB files.
 * This includes extracting the text from single and multi-line quotes (surrounded by " and "), 
 * simple lists (surrounded by { and } with entries delimited by commas), and value list 
 * enumerations, which are simple lists with name-value pairs as entries.
 * <br><br>
 * It also handles specific common tasks such as finding the start of the MIB definition.
 */
public class SmiStructureHandler 
{
    public static final String NEWLINE_REGEX = "\n|\r\n|\r"; //search for all types of line separators
    
    private BufferedReader in;
    
    
    /**
     * Initializes the structure handler with a BufferedReader that has been given a MIB 
     * file to be parsed.
     */
    public SmiStructureHandler(final BufferedReader newReader)
    {
        in = newReader;
    }
    
    
    /**
     * Reads the stream in an attempt to find the start of a MIB module definition and return 
     * its name.  The stream is scanned until a line containing "DEFINITIONS ::= BEGIN" is 
     * found. If this BEGIN marker can't be found within 100 lines, the scan stops.
     * 
     * @return an empty string if the MIB definition could not be found, and the name 
     * of the MIB if it was found
     * 
     * @throws IOException if an error occurs when reading a line
     */
    public String readMibName() throws IOException
    {      
        int lineCnt = 0;
        String line;
        String mibName = "";
        
        while ( ((line = in.readLine()) != null) && (lineCnt < 100) )
        {
            if (line.contains(SmiTokens.MIB_BEGIN))
            {
                mibName = line.substring(0, line.indexOf(SmiTokens.MIB_BEGIN) - 1).trim();
                break;  // don't read any more lines
            }

            lineCnt = lineCnt + 1;
        }
        
        return mibName;
    }
    
    
    /**
     * Reads all text between quote marks.
     */
    public String readQuotedSection(String line, String keyword) throws IOException
    {
        StringBuilder quote = new StringBuilder("");
        char c = 0;
        boolean isSingleLine = false;
        
        //If the quoted portion starts on the same line as the given keyword,
        //the line's contents following the quote mark must be collected.
        line = line.trim();
        if (!line.equals(keyword) && line.contains("\""))
        {
            quote.append(line);
            int firstQuoteMarkPos = quote.indexOf("\"");
            quote.delete(0, firstQuoteMarkPos + 1);
            
            //strip comments
            int commentPos = quote.indexOf("--");
            if (commentPos > -1)
                quote.delete(commentPos, quote.length());
            
            //check for a quote mark at the end of the line
            int secondQuoteMarkPos = quote.indexOf("\"");
            if (secondQuoteMarkPos > -1)
            {
                isSingleLine = true;
                quote.delete(secondQuoteMarkPos, quote.length());
            }      
            
            //this gets trimmed off and must be re-added; it's a sloppy fix
            quote.append(System.getProperty("line.separator"));
        }
        else
        {
            //read the stream until the first quote mark is reached
            c = 0;        
            while ((c != -1) && (c != '\"'))
                c = (char)in.read();
        }
        
        //don't read any further if the quote was a single line on the same line as the keyword
        if (!isSingleLine)
        {
            //read the entire quoted section into the StringBuilder
            c = 0;        
            while ((c != -1) && (c != '\"'))
            {
                c = (char)in.read();
                
                if (c != '\"')
                    quote.append(c);
            }
            
            String[] quoteLines = quote.toString().trim().split(NEWLINE_REGEX);
            quote.delete(0, quote.length());//clear builder for reuse
            
            for (int i = 0; i < quoteLines.length; i++)
            {
                String curLine = quoteLines[i].trim();
                
                if (!curLine.trim().startsWith("--"))    //ignore commented lines
                {
                    if (i == (quoteLines.length - 1))
                        quote.append(curLine.trim());   //last line needs no break at the end
                    else
                        quote.append(curLine.trim() + "<br>");
                }   
            }
            
        } //end of block that applies to quotes that are not entirely on a single line with the given keyword
        
        return quote.toString().trim();
    }
    
    
    /**
     * Reads a comma delimited list enclosed in curly brackets: { and }.
     * The process is very similar to reading text enclosed in quote marks, but there
     * are also enough differences to warrant different methods.
     */
    public List<String> readList(String line, String keyword) throws IOException
    {
        StringBuilder listString = new StringBuilder("");
        char c = 0;
        boolean isSingleLine = false;
        
        //If the list starts on the same line as the given keyword,
        //the line's contents following the left curly bracket must be collected.
        line = line.trim();
        if (!line.equals(keyword) && line.contains("{"))
        {
            listString.append(line);
            
            int leftBracketPos = listString.indexOf("{");
            listString.delete(0, leftBracketPos + 1);
            
            //strip comments
            int commentPos = listString.indexOf("--");
            if (commentPos > -1)
                listString.delete(commentPos, listString.length());
            
            //check for a right bracket at the end of the line
            int rightBracketPos = listString.indexOf("}");
            if (rightBracketPos > 0)
            {
                isSingleLine = true;
                listString.delete(rightBracketPos, listString.length());
            }      
            
            //this gets trimmed off and must be re-added; it's a sloppy fix
            listString.append(System.getProperty("line.separator"));
        }
        else
        {
            //read the stream until the left bracket is reached
            c = 0;        
            while ((c != -1) && (c != '{'))
                c = (char)in.read();
        }
        
        //don't read any further if the list was a single line on the same line as the keyword
        if (!isSingleLine)
        {
            //read the entire list (until the '}') into the StringBuilder
            c = 0;        
            while ((c != -1) && (c != '}'))
            {
                c = (char)in.read();

                if (c != '}')
                    listString.append(c);
            }
        }
        
        String[] listLines = listString.toString().split(NEWLINE_REGEX + "|,");
        
        //loop through each item and add it to the list
        ArrayList<String> returnList = new ArrayList<String>();
        
        for (int i = 0; i < listLines.length; i++)
        {
            String curLine = listLines[i].trim();

            if (!curLine.trim().equals("") && !curLine.trim().startsWith("--"))
            {
                int commentPos = curLine.indexOf("--");
                if (commentPos > -1)
                    curLine = curLine.substring(0, commentPos).trim();

                returnList.add(curLine);
            }

        }
        
        return returnList;
    }
    
    
    /**
     * Read comma delimited lists of name-value pairs, essentially enumerations.
     * This method uses readList and further parses the returned List of Strings
     * to extract the names and values.
     * 
     * @return a MibValueListItem ArrayList
     */
    public List<MibNameValuePair> readPairs(String line, String keyword) throws IOException
    {
        List<String> valueLines = readList(line, keyword);
        
        List<MibNameValuePair> valueList = new ArrayList<MibNameValuePair>();
        
        for (int i = 0; i < valueLines.size(); i++)
        {
            String curLine = valueLines.get(i).trim();
            
            if (curLine.contains("(") && curLine.contains(")"))
            {
                int commentPos = curLine.indexOf("--");
                if (commentPos > -1)
                    curLine = curLine.substring(0, commentPos).trim();
                
                //extract the integer value and its label/alias/name
                String name = curLine.substring(0, curLine.indexOf("(")).trim();
                String value = curLine.substring(curLine.indexOf("(") + 1, curLine.lastIndexOf(")")).trim();
                
                //add the value list entry
                MibNameValuePair curValueItem = new MibNameValuePair(name, Integer.parseInt(value));
                valueList.add(curValueItem);
            }
        }
        
        return valueList;
    }

}

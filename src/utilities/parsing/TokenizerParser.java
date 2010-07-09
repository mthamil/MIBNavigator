/**
 * Utilities
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

package utilities.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Parses a string delimited by a given character into a list.  The tokenLimit
 * parameter can be used to limit how many delimited entries to return.
 */
public class TokenizerParser implements Parser<List<String>>
{
	private static int UNLIMITED_TOKENS = -1;
	
	/** 
	 * The maximum number of tokens that should be parsed.
	 * A value of -1 means there is no limit.
	 */
	private int tokenLimit = UNLIMITED_TOKENS;
	
	private char delimiter;
	
	/**
	 * Creates a tokenzier parser with a limit to the number of tokens that should be parsed.  
	 * Values less than 0 indicate that tokens should be parsed.
	 * @param tokenLimit The maximum number of tokens that should be parsed.
	 * @param delimiter The delimiter used to split the tokens.
	 */
	public TokenizerParser(int tokenLimit, char delimiter)
	{
		setTokenLimit(tokenLimit);
		this.delimiter = delimiter;
	}
	
	/**
	 * Creates a tokenzier parser.
	 * @param delimiter The delimiter used to split the tokens.
	 */
	public TokenizerParser(char delimiter)
	{
		this(UNLIMITED_TOKENS, delimiter);
	}

	/**
	 * Sets the limit to the number of tokens that should be parsed.
	 * Values less than 0 indicate that tokens should be parsed.
	 * @param tokenLimit The maximum number of tokens that should be parsed.
	 */
	public void setTokenLimit(int tokenLimit)
	{
		this.tokenLimit = Math.max(UNLIMITED_TOKENS, tokenLimit);
	}
	
	/**
	 * Returns the limit to the number of tokens that should be parsed.
	 */
	public int getTokenLimit()
	{
		return tokenLimit;
	}

	public List<String> parse(String stringValue)
	{
		boolean noLimit = tokenLimit == UNLIMITED_TOKENS;
		List<String> entries = noLimit
							   ? new ArrayList<String>()
							   : new ArrayList<String>(tokenLimit);
									   
		StringTokenizer tokenizer = new StringTokenizer(stringValue, String.valueOf(delimiter));
        int i = 0;
        while (tokenizer.hasMoreTokens() && (noLimit ? true : i < tokenLimit))
        {
        	entries.add(tokenizer.nextToken());
            i++;
        }
		
		return entries;
	}
}

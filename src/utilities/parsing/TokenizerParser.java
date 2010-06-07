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

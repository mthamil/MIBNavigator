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

package libmib.format.smi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Class that extends buffered reader and automatically strips all comments
 * from each line read.
 */
public class CommentRemovingReader extends BufferedReader
{
	private String commentToken;
	
	public CommentRemovingReader(Reader in, String commentToken)
	{
		super(in);
		this.commentToken = commentToken;
	}
	
	public CommentRemovingReader(Reader in, int sz, String commentToken)
	{
		super(in, sz);
		this.commentToken = commentToken;
	}
	
	public String readLine() throws IOException
	{
		String line = super.readLine();
		if (line != null)
		{
			if (line.contains(commentToken))
            {
                int commentIndex = line.indexOf(commentToken);
                line = line.substring(0, commentIndex).trim();
            }
		}
		
		return line;
	}

}

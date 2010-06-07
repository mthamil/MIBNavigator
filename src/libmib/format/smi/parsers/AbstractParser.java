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

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Parses a buffered reader and returns an object.
 *
 * @param <T> the type of object to create from parsing
 */
public abstract class AbstractParser<T>
{
	/**
	 * Parses a buffered reader and creates an object with the parsed result.
	 * @param reader
	 * @param currentLine
	 * @return
	 * @throws IOException 
	 */
	public abstract T parse(BufferedReader reader, String currentLine) throws IOException;
}

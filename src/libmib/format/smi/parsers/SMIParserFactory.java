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

package libmib.format.smi.parsers;

import java.util.HashMap;
import java.util.Map;

import static libmib.format.smi.SMIToken.*;
import libmib.format.smi.SMIToken;

/**
 * Contains a mapping of SMI tokens to their corresponding parsers.
 */
public class SMIParserFactory
{
	private static final Map<SMIToken, AbstractParser<?>> parsers = 
		new HashMap<SMIToken, AbstractParser<?>>();
	
	/**
	 * Initializes the parser mapping.
	 */
	static
	{
		parsers.put(ACCESS, new AccessParser());
		parsers.put(STATUS, new StatusParser());
		parsers.put(SYNTAX, new SyntaxParser());
		parsers.put(DESCRIPTION, new DescriptionParser());
		parsers.put(MODULE_REVISION, new ModuleRevisionParser());
		parsers.put(IMPORTS, new ImportsParser());
		parsers.put(OBJECT_GROUP, new GeneralMibObjectParser());
	}

	/**
	 * Retrieves the parser for a given token type.
	 * @param token
	 * @return
	 */
	public static AbstractParser<?> getParser(SMIToken token)
	{
		return parsers.get(token);
	}
}

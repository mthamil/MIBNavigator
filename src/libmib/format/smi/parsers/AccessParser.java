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

import static libmib.format.smi.SMIToken.ACCESS;

import java.io.BufferedReader;
import java.io.IOException;

import libmib.MibObjectType.Access;

public class AccessParser implements SMIParser<Access>
{
	public Access parse(BufferedReader reader, String currentLine) throws IOException
	{
		int index = currentLine.indexOf(ACCESS.token()) + ACCESS.token().length();
        String access = currentLine.substring(index).trim();
        
        if (!access.equals(""))
            return Access.valueOf(access.toUpperCase().replaceAll("-", "_"));
        
        return null;
	}
}

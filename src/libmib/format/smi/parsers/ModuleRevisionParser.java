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

import static libmib.format.smi.SMIToken.DESCRIPTION;

import java.io.BufferedReader;
import java.io.IOException;

import libmib.MibModuleIdRevision;
import libmib.format.smi.SMIStructureHandler;

public class ModuleRevisionParser implements SMIParser<MibModuleIdRevision>
{
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
}

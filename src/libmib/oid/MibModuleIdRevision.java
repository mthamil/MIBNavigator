/**
 * libmib - Java SNMP Management Information Base Library
 *
 * Copyright (C) 2005, Matt Hamilton <matthew.hamilton@washburn.edu>
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

package libmib.oid;

public class MibModuleIdRevision 
{
    private String revisionId;
    private String revisionDescription;
    
    public MibModuleIdRevision()
    {
        revisionId = "";
        revisionDescription = "";
    }
    
    public MibModuleIdRevision(String newRevId, String newDesc)
    {
        revisionId = newRevId;
        revisionDescription = newDesc;
    }

    /**
     * @return Returns the revisionDescription.
     */
    public String getRevisionDescription() 
    {
        return revisionDescription;
    }

    /**
     * @param revisionDescription The revisionDescription to set.
     */
    public void setRevisionDescription(String revisionDescription) 
    {
        this.revisionDescription = revisionDescription;
    }

    /**
     * @return Returns the revisionId.
     */
    public String getRevisionId() 
    {
        return revisionId;
    }

    /**
     * @param revisionId The revisionId to set.
     */
    public void setRevisionId(String revisionId) 
    {
        this.revisionId = revisionId;
    }

}

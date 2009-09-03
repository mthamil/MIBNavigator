/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 * Copyright (C) 2009, Matt Hamilton <matthamilton@live.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package snmp;

import snmp.error.ErrorStatus;


/**
 * Abstract class that implements basic recent version trap PDU functions.
 */
public class SnmpAbstractTrapPDU extends SnmpPDU
								 implements SnmpTrapPDU
{

	/**
     *  Creates a new Trap PDU with given trapOID and sysUptime,
     *  and containing the supplied SNMP sequence as data.
     */
    public SnmpAbstractTrapPDU(SnmpTimeTicks sysUptime, SnmpObjectIdentifier snmpTrapOID, 
    		SnmpSequence varList, SnmpBERType type) throws SnmpBadValueException
    {
        super(type, 0, ErrorStatus.NoError, 0, varList);
        
        // create a variable pair for sysUptime, and insert into varBindList
        SnmpObjectIdentifier sysUptimeOID = new SnmpObjectIdentifier("1.3.6.1.2.1.1.3.0");
        SnmpVariablePair sysUptimePair = new SnmpVariablePair(sysUptimeOID, sysUptime);
        varList.insertSNMPObjectAt(sysUptimePair, 0);
        
        // create a variable pair for snmpTrapOID, and insert into varBindList
        SnmpObjectIdentifier snmpTrapOIDOID = new SnmpObjectIdentifier("1.3.6.1.6.3.1.1.4.1.0");
        SnmpVariablePair snmpOIDPair = new SnmpVariablePair(snmpTrapOIDOID, snmpTrapOID);
        varList.insertSNMPObjectAt(snmpOIDPair, 1);
    }
    
    
    /**
     *  Creates a new Trap PDU with given trapOID and sysUptime, and 
     *  containing an empty SNMP sequence (VarBindList) as additional 
     *  data.
     */
    public SnmpAbstractTrapPDU(SnmpObjectIdentifier snmpTrapOID, SnmpTimeTicks sysUptime, SnmpBERType type)
        throws SnmpBadValueException
    {
        this(sysUptime, snmpTrapOID, new SnmpSequence(), type);
    }
    
    
    /**
     *  Creates a new PDU of the specified type from the supplied BER encoding.
     *  
     *  @throws SnmpBadValueException Indicates invalid SNMP PDU encoding supplied 
     *  in encoding.
     */
    protected SnmpAbstractTrapPDU(byte[] encoding, SnmpBERType type) throws SnmpBadValueException
    {
        super(encoding, type);
        
        // validate the message: make sure the first two components of the varBindList
        // are the appropriate variable pairs
        SnmpSequence varBindList = this.getVarBindList();
        
        if (varBindList.size() < 2)
            throw new SnmpBadValueException("Bad Trap PDU: missing snmpTrapOID or sysUptime");
        
        // validate that the first variable binding is the sysUptime
        SnmpSequence variablePair = (SnmpSequence)varBindList.getSNMPObjectAt(0);
        SnmpObjectIdentifier oid = (SnmpObjectIdentifier)variablePair.getSNMPObjectAt(0);
        SnmpObject value = variablePair.getSNMPObjectAt(1);
        SnmpObjectIdentifier sysUptimeOID = new SnmpObjectIdentifier("1.3.6.1.2.1.1.3.0");
        
        if (!(value instanceof SnmpTimeTicks) || !oid.equals(sysUptimeOID))
            throw new SnmpBadValueException("Bad Trap PDU: bad sysUptime in variable binding list");
        
        // validate that the second variable binding is the snmpTrapOID
        variablePair = (SnmpSequence)varBindList.getSNMPObjectAt(1);
        oid = (SnmpObjectIdentifier)variablePair.getSNMPObjectAt(0);
        value = variablePair.getSNMPObjectAt(1);
        SnmpObjectIdentifier snmpTrapOIDOID = new SnmpObjectIdentifier("1.3.6.1.6.3.1.1.4.1.0");
        
        if (!(value instanceof SnmpObjectIdentifier) || !oid.equals(snmpTrapOIDOID))
            throw new SnmpBadValueException("Bad Trap PDU: bad snmpTrapOID in variable binding list");
    }
    

	/**
	 * @see snmp.SnmpTrapPDU#getSNMPTrapOID()
	 */
	public SnmpObjectIdentifier getSNMPTrapOID()
	{
		SnmpSequence contents = this.getVarBindList();
        SnmpSequence variablePair = (SnmpSequence)contents.getSNMPObjectAt(1);
        return (SnmpObjectIdentifier)variablePair.getSNMPObjectAt(1);
	}

	/**
	 * @see snmp.SnmpTrapPDU#getSysUptime()
	 */
	public SnmpTimeTicks getSysUptime()
	{
		SnmpSequence contents = this.getVarBindList();
        SnmpSequence variablePair = (SnmpSequence)contents.getSNMPObjectAt(0);
        return (SnmpTimeTicks)variablePair.getSNMPObjectAt(1);
	}

}

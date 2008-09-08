/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 * 			 (C) 2008, Matt Hamilton <mhamil6@uic.edu>
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


/**
 * Abstract class that implements basic recent version trap PDU functions.
 */
public class SNMPAbstractTrapPDU extends SNMPPDU
								 implements SNMPTrapPDU
{

	/**
     *  Creates a new Trap PDU with given trapOID and sysUptime,
     *  and containing the supplied SNMP sequence as data.
     */
    public SNMPAbstractTrapPDU(SNMPTimeTicks sysUptime, SNMPObjectIdentifier snmpTrapOID, 
    		SNMPSequence varList, SNMPBERType type) throws SNMPBadValueException
    {
        super(type, 0, 0, 0, varList);
        
        // create a variable pair for sysUptime, and insert into varBindList
        SNMPObjectIdentifier sysUptimeOID = new SNMPObjectIdentifier("1.3.6.1.2.1.1.3.0");
        SNMPVariablePair sysUptimePair = new SNMPVariablePair(sysUptimeOID, sysUptime);
        varList.insertSNMPObjectAt(sysUptimePair, 0);
        
        // create a variable pair for snmpTrapOID, and insert into varBindList
        SNMPObjectIdentifier snmpTrapOIDOID = new SNMPObjectIdentifier("1.3.6.1.6.3.1.1.4.1.0");
        SNMPVariablePair snmpOIDPair = new SNMPVariablePair(snmpTrapOIDOID, snmpTrapOID);
        varList.insertSNMPObjectAt(snmpOIDPair, 1);
    }
    
    
    /**
     *  Creates a new Trap PDU with given trapOID and sysUptime, and 
     *  containing an empty SNMP sequence (VarBindList) as additional 
     *  data.
     */
    public SNMPAbstractTrapPDU(SNMPObjectIdentifier snmpTrapOID, SNMPTimeTicks sysUptime, SNMPBERType type)
        throws SNMPBadValueException
    {
        this(sysUptime, snmpTrapOID, new SNMPSequence(), type);
    }
    
    
    /**
     *  Creates a new PDU of the specified type from the supplied BER encoding.
     *  
     *  @throws SNMPBadValueException Indicates invalid SNMP PDU encoding supplied 
     *  in encoding.
     */
    protected SNMPAbstractTrapPDU(byte[] encoding, SNMPBERType type) throws SNMPBadValueException
    {
        super(encoding, type);
        
        // validate the message: make sure the first two components of the varBindList
        // are the appropriate variable pairs
        SNMPSequence varBindList = this.getVarBindList();
        
        if (varBindList.size() < 2)
            throw new SNMPBadValueException("Bad Trap PDU: missing snmpTrapOID or sysUptime");
        
        // validate that the first variable binding is the sysUptime
        SNMPSequence variablePair = (SNMPSequence)varBindList.getSNMPObjectAt(0);
        SNMPObjectIdentifier oid = (SNMPObjectIdentifier)variablePair.getSNMPObjectAt(0);
        SNMPObject value = variablePair.getSNMPObjectAt(1);
        SNMPObjectIdentifier sysUptimeOID = new SNMPObjectIdentifier("1.3.6.1.2.1.1.3.0");
        
        if (!(value instanceof SNMPTimeTicks) || !oid.equals(sysUptimeOID))
            throw new SNMPBadValueException("Bad Trap PDU: bad sysUptime in variable binding list");
        
        // validate that the second variable binding is the snmpTrapOID
        variablePair = (SNMPSequence)varBindList.getSNMPObjectAt(1);
        oid = (SNMPObjectIdentifier)variablePair.getSNMPObjectAt(0);
        value = variablePair.getSNMPObjectAt(1);
        SNMPObjectIdentifier snmpTrapOIDOID = new SNMPObjectIdentifier("1.3.6.1.6.3.1.1.4.1.0");
        
        if (!(value instanceof SNMPObjectIdentifier) || !oid.equals(snmpTrapOIDOID))
            throw new SNMPBadValueException("Bad Trap PDU: bad snmpTrapOID in variable binding list");
    }
    

	/**
	 * @see snmp.SNMPTrapPDU#getSNMPTrapOID()
	 */
	public SNMPObjectIdentifier getSNMPTrapOID()
	{
		SNMPSequence contents = this.getVarBindList();
        SNMPSequence variablePair = (SNMPSequence)contents.getSNMPObjectAt(1);
        return (SNMPObjectIdentifier)variablePair.getSNMPObjectAt(1);
	}

	/**
	 * @see snmp.SNMPTrapPDU#getSysUptime()
	 */
	public SNMPTimeTicks getSysUptime()
	{
		SNMPSequence contents = this.getVarBindList();
        SNMPSequence variablePair = (SNMPSequence)contents.getSNMPObjectAt(0);
        return (SNMPTimeTicks)variablePair.getSNMPObjectAt(1);
	}

}
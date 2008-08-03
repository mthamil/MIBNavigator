/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
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
 * The SNMPv2InformRequestPDU class represents an SNMPv2 Trap PDU from <a href="http://www.ietf.org/rfc/rfc1448.txt">RFC 1448</a>,
 * as indicated below. This forms the payload of an SNMPv2 Inform Request
 * message.
 * <pre>
 * <code>
 *  -- protocol data units
 * 
 *  3.  Definitions
 * 
 *      SNMPv2-PDU DEFINITIONS ::= BEGIN
 * 
 *          IMPORTS
 *              ObjectName, ObjectSyntax, Integer32
 *                  FROM SNMPv2-SMI;
 * 
 *      -- protocol data units
 * 
 *      PDUs ::=
 *          CHOICE {
 *                  get-request
 *                      GetRequest-PDU,
 * 
 *                  get-next-request
 *                      GetNextRequest-PDU,
 * 
 *                  get-bulk-request
 *                      GetBulkRequest-PDU,
 * 
 *                  response
 *                      Response-PDU,
 * 
 *                  set-request
 *                      SetRequest-PDU,
 * 
 *                  inform-request
 *                      InformRequest-PDU,
 * 
 *                  snmpV2-trap
 *                      SNMPv2-Trap-PDU
 *                 }
 * 
 *      -- PDUs
 * 
 *      GetRequest-PDU ::=
 *          [0]
 *              IMPLICIT PDU
 * 
 *      GetNextRequest-PDU ::=
 *          [1]
 *              IMPLICIT PDU
 * 
 *      Response-PDU ::=
 *          [2]
 *              IMPLICIT PDU
 * 
 *      SetRequest-PDU ::=
 *          [3]
 *              IMPLICIT PDU
 * 
 *       -- [4] is obsolete
 * 
 *      GetBulkRequest-PDU ::=
 *          [5]
 *              IMPLICIT BulkPDU
 * 
 *      InformRequest-PDU ::=
 *          [6]
 *              IMPLICIT PDU
 * 
 *      SNMPv2-Trap-PDU ::=
 *          [7]
 *              IMPLICIT PDU
 * 
 *      max-bindings
 *          INTEGER ::= 2147483647
 * 
 *      PDU ::= 
 *          SEQUENCE { 
 *                      request-id Integer32,
 * 
 *                      error-status -- sometimes ignored 
 *                          INTEGER { 
 *                                      noError(0),
 *                                      tooBig(1),
 *                                      noSuchName(2), -- for proxy compatibility 
 *                                      badValue(3),   -- for proxy compatibility 
 *                                      readOnly(4),   -- for proxy compatibility 
 *                                      genErr(5), 
 *                                      noAccess(6),
 *                                      wrongType(7), 
 *                                      wrongLength(8), 
 *                                      wrongEncoding(9), 
 *                                      wrongValue(10),
 *                                      noCreation(11), 
 *                                      inconsistentValue(12), 
 *                                      resourceUnavailable(13),
 *                                      commitFailed(14), 
 *                                      undoFailed(15), 
 *                                      authorizationError(16), 
 *                                      notWritable(17),
 *                                      inconsistentName(18) 
 *                                   },
 * 
 *                      error-index -- sometimes ignored 
 *                          INTEGER (0..max-bindings),
 * 
 *                      variable-bindings -- values are sometimes ignored 
 *                          VarBindList 
 *                   } 
 * </code>
 * </pre>
 * @see snmp.SNMPVarBindList 
 * @see snmp.SNMPPDU
 * @see <a href="http://www.ietf.org/rfc/rfc1905.txt">RFC 1905</a>
 * @see <a href="http://www.ietf.org/rfc/rfc1448.txt">RFC 1448</a>
 */
public class SNMPv2InformRequestPDU extends SNMPPDU
{
    
    /**
     * Create a new Inform Request PDU with given trapOID and sysUptime, and
     * containing the supplied SNMP sequence as data.
     */
    public SNMPv2InformRequestPDU(SNMPTimeTicks sysUptime, SNMPObjectIdentifier snmpTrapOID, SNMPSequence varList)
        throws SNMPBadValueException
    {
        super(SNMPBERType.SNMPv2_INFORM_REQUEST, 0, 0, 0, varList);
        
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
     *  Create a new Inform Request PDU with given trapOID and sysUptime,
     *  and containing an empty SNMP sequence (VarBindList) as additional data.
     */
    public SNMPv2InformRequestPDU(SNMPObjectIdentifier snmpTrapOID, SNMPTimeTicks sysUptime)
        throws SNMPBadValueException
    {
        this(sysUptime, snmpTrapOID, new SNMPSequence());
    }
    

    /**
     *  Create a new PDU of the specified type from the supplied BER encoding.
     *  
     *  @throws SNMPBadValueException Indicates invalid SNMP PDU encoding supplied in enc.
     */
    protected SNMPv2InformRequestPDU(byte[] enc)
        throws SNMPBadValueException
    {
        super(enc, SNMPBERType.SNMPv2_INFORM_REQUEST);
        
        // validate the message: make sure the first two components of the varBindList
        // are the appropriate variable pairs
        SNMPSequence varBindList = this.getVarBindList();
        
        if (varBindList.size() < 2)
            throw new SNMPBadValueException("Bad v2 Inform Request PDU: missing snmpTrapOID or sysUptime");
        
        // validate that the first variable binding is the sysUptime
        SNMPSequence variablePair = (SNMPSequence)varBindList.getSNMPObjectAt(0);
        SNMPObjectIdentifier oid = (SNMPObjectIdentifier)variablePair.getSNMPObjectAt(0);
        SNMPObject value = variablePair.getSNMPObjectAt(1);
        SNMPObjectIdentifier sysUptimeOID = new SNMPObjectIdentifier("1.3.6.1.2.1.1.3.0");
        
        if (!(value instanceof SNMPTimeTicks) || !oid.equals(sysUptimeOID))
            throw new SNMPBadValueException("Bad v2 Inform Request PDU: bad sysUptime in variable binding list");
        
        // validate that the second variable binding is the snmpTrapOID
        variablePair = (SNMPSequence)varBindList.getSNMPObjectAt(1);
        oid = (SNMPObjectIdentifier)variablePair.getSNMPObjectAt(0);
        value = variablePair.getSNMPObjectAt(1);
        SNMPObjectIdentifier snmpTrapOIDOID = new SNMPObjectIdentifier("1.3.6.1.6.3.1.1.4.1.0");
        
        if (!(value instanceof SNMPObjectIdentifier) || !oid.equals(snmpTrapOIDOID))
            throw new SNMPBadValueException("Bad v2 Inform Request PDU: bad snmpTrapOID in variable binding list");
        
    }
    

    /** 
     *  A utility method that extracts the snmpTrapOID from the variable bind list (it's the second of the 
     *  variable pairs).
     */
    public SNMPObjectIdentifier getSNMPTrapOID()
    {
        SNMPSequence contents = this.getVarBindList();
        SNMPSequence variablePair = (SNMPSequence)contents.getSNMPObjectAt(1);
        return (SNMPObjectIdentifier)variablePair.getSNMPObjectAt(1);
    }
    
    
    /** 
     *  A utility method that extracts the sysUptime from the variable bind list (it's the first of the 
     *  variable pairs).
     */
    public SNMPTimeTicks getSysUptime()
    {
        SNMPSequence contents = this.getVarBindList();
        SNMPSequence variablePair = (SNMPSequence)contents.getSNMPObjectAt(0);
        return (SNMPTimeTicks)variablePair.getSNMPObjectAt(1);
    }
    
}
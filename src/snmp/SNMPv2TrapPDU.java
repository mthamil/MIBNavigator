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
 * The SNMPv2TrapPDU class represents an SNMPv2 Trap PDU from <a href="http://www.ietf.org/rfc/rfc1448.txt">RFC 1448</a>, as indicated below. 
 * This forms the payload of an SNMPv2 Trap message.
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
public class SNMPv2TrapPDU extends SNMPAbstractTrapPDU
{

    /**
     *  Creates a new v2 Trap PDU with given trapOID and sysUptime,
     *  and containing the supplied SNMP sequence as data.
     */
    public SNMPv2TrapPDU(SNMPTimeTicks sysUptime, SNMPObjectIdentifier snmpTrapOID, SNMPSequence varList)
        throws SNMPBadValueException
    {
        super(sysUptime, snmpTrapOID, varList, SNMPBERType.SNMPv2_TRAP);
    }
    
    
    /**
     *  Creates a new v2 Trap PDU with given trapOID and sysUptime,
     *  and containing an empty SNMP sequence (VarBindList) as 
     *  additional data.
     */
    public SNMPv2TrapPDU(SNMPObjectIdentifier snmpTrapOID, SNMPTimeTicks sysUptime)
        throws SNMPBadValueException
    {
        this(sysUptime, snmpTrapOID, new SNMPSequence());
    }
    
    
    /**
     *  Creates a new v2 Trap PDU from the supplied BER encoding.
     *  
     *  @throws SNMPBadValueException Indicates invalid SNMP PDU 
     *  encoding.
     */
    protected SNMPv2TrapPDU(byte[] encoding) throws SNMPBadValueException
    {
        super(encoding, SNMPBERType.SNMPv2_TRAP);
    }

}
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
 *  SNMPv1TrapListener is an interface that must be implemented by any class which wishes to act as a
 *  "listener" for trap messages sent from remote SNMP entities to an instance of the SNMPTrapListenerInterface
 *  class. The SNMPTrapListenerInterface class listens for trap messages, and passes any it receives on to
 *  SNMPv1TrapListener subclasses that have registered with it through its addv1TrapListener() method.
 */
public interface SNMPv1TrapListener
{
    
    public void processv1Trap(SNMPv1TrapPDU trapPDU);
    
}
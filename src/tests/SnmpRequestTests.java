/**
 * MIB Navigator
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

package tests;

import static org.junit.Assert.*;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsInstanceOf.*;
import static org.hamcrest.number.OrderingComparison.*;
import static org.hamcrest.core.StringStartsWith.*;

import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;

import snmp.SnmpV1Communicator;
import snmp.SnmpVersion;
import snmp.datatypes.SnmpNull;
import snmp.datatypes.SnmpObject;
import snmp.datatypes.SnmpObjectIdentifier;
import snmp.datatypes.sequence.SnmpSequence;
import snmp.datatypes.sequence.SnmpVarBindList;

/**
 * Note: a locally running SNMP agent is required to run these tests.
 */
public class SnmpRequestTests
{
	private SnmpV1Communicator communicator;
	
	@Before
	public void setUp()
	{
		try
		{
			communicator = new SnmpV1Communicator(SnmpVersion.SNMPv1, InetAddress.getLocalHost(), "public");
		}
		catch (Exception e)
		{
			assertTrue(e.getMessage(), false);
		}
	}
	
	@Test
	public void testRetrieveTable() 
	{
        try
        {                       
        	String oidPrefix = "1.3.6.1.2.1.1";
            SnmpVarBindList results = communicator.retrieveMIBTable(oidPrefix);
            
            assertThat(results.size(), greaterThan(0));
            for (int i = 0; i < results.size(); i++)
            {
            	SnmpSequence result = (SnmpSequence)results.getSNMPObjectAt(i);
            	
            	SnmpObjectIdentifier oid = (SnmpObjectIdentifier)result.getSNMPObjectAt(0);
            	SnmpObject value = result.getSNMPObjectAt(1);
            	
            	assertThat(oid.toString(), startsWith(oidPrefix));
            	assertThat(value, not(instanceOf(SnmpNull.class)));
            }
        }
        catch (Exception e)
        {
        	assertTrue(e.getMessage(), false);
        }
	}
	
	@Test                     
	public void testGetValue()
	{                         
		try
		{
			String nameOid = "1.3.6.1.2.1.1.5.0";
			SnmpVarBindList result = communicator.getMIBEntry(nameOid);
			assertThat(result.size(), is(1));
			
			SnmpSequence pair = (SnmpSequence)result.getSNMPObjectAt(0);
			
			SnmpObjectIdentifier oid = (SnmpObjectIdentifier)pair.getSNMPObjectAt(0);
			SnmpObject value = pair.getSNMPObjectAt(1);
			
			assertThat(oid.toString(), is(nameOid));
			assertThat(value, not(instanceOf(SnmpNull.class)));
		}
		catch (Exception e)
		{
			assertTrue(e.getMessage(), false);
		}
	}
	
	@Test                     
	public void testMultipleGetValue()
	{                         
		try
		{
			String[] oids = { "1.3.6.1.2.1.1.5.0", "1.3.6.1.2.1.1.1.0" };
			SnmpVarBindList result = communicator.getMIBEntry(oids);
			assertThat(result.size(), is(oids.length));
			
			for (int i = 0; i < result.size(); i++)
			{
				SnmpSequence pair = (SnmpSequence)result.getSNMPObjectAt(i);
				
				SnmpObjectIdentifier oid = (SnmpObjectIdentifier)pair.getSNMPObjectAt(0);
				SnmpObject value = pair.getSNMPObjectAt(1);
				
				assertThat(oid.toString(), is(oids[i]));
				assertThat(value, not(instanceOf(SnmpNull.class)));
			}
		}
		catch (Exception e)
		{
			assertTrue(e.getMessage(), false);
		}
	}
	
	@Test
	public void testGetNextValue()
	{                         
		try
		{
			String nameOid = "1.3.6.1.2.1.1.5";
			SnmpVarBindList result = communicator.getNextMIBEntry(nameOid);
			assertThat(result.size(), is(1));
			
			SnmpSequence pair = (SnmpSequence)result.getSNMPObjectAt(0);
			
			SnmpObjectIdentifier oid = (SnmpObjectIdentifier)pair.getSNMPObjectAt(0);
			SnmpObject value = pair.getSNMPObjectAt(1);
			
			assertThat(oid.toString(), is(nameOid + ".0"));
			assertThat(value, not(instanceOf(SnmpNull.class)));
		}
		catch (Exception e)
		{
			assertTrue(e.getMessage(), false);
		}
	}
	
	@Test
	public void testBufferSize()
	{
		// Test default buffer size.
		assertThat(communicator.getReceiveBufferSize(), is(512));
		
		int minSize = SnmpV1Communicator.MINIMUM_BUFFER_SIZE;
		
		// Test greater than minimum.
		communicator.setReceiveBufferSize(minSize + 1);
		assertThat(communicator.getReceiveBufferSize(), is(minSize + 1));
		
		// Test equal to minimum.
		communicator.setReceiveBufferSize(minSize);
		assertThat(communicator.getReceiveBufferSize(), is(minSize));
		
		// Test less than minimum.
		communicator.setReceiveBufferSize(minSize - 1);
		assertThat(communicator.getReceiveBufferSize(), is(minSize));
	}
	
//	@Test
//	public void testSetValue() 
//	{
//        try
//        {                       
//        	// Get the existing value.
//        	String nameOid = "1.3.6.1.2.1.1.5.0";
//            SnmpVarBindList result = communicator.getMIBEntry(nameOid);
//            assertTrue(result.size() == 1);
//            SnmpObject existingValue = ((SnmpSequence)result.getSNMPObjectAt(0)).getSNMPObjectAt(1);
//
//        	// Replace the value.
//            String testValue = "testValue";
//            SnmpVarBindList setResult = communicator.setMIBEntry(nameOid, new SnmpOctetString(testValue));
//            assertTrue(setResult.size() == 1);
//            
//            // Test the set result.
//            SnmpSequence setResultPair = (SnmpSequence)setResult.getSNMPObjectAt(0);
//            SnmpObjectIdentifier identifier = (SnmpObjectIdentifier)setResultPair.getSNMPObjectAt(0);
//            assertEquals(nameOid, identifier.toString()); 
//            
//            SnmpObject newValue = setResultPair.getSNMPObjectAt(1);
//            assertEquals(testValue, newValue.toString());
//            
//            // Test the new value.
//            result = communicator.getMIBEntry(nameOid);
//            assertTrue(result.size() == 1);
//            newValue = ((SnmpSequence)result.getSNMPObjectAt(0)).getSNMPObjectAt(1);
//            assertEquals("testValue", newValue.toString());
//            
//            // Reset the value.
//        	
//        }
//        catch (Exception e)
//        {
//        	assertTrue(e.getMessage(), false);
//        }
//	}
}

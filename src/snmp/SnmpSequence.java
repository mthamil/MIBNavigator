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

import java.util.*;
import java.io.*;

/**
 *  One of the most important SNMP classes. Represents a sequence of other SNMP data types.
 *  Virtually all compound structures are subclasses of SNMPSequence - for example, the 
 *  top-level SNMPMessage, and the SNMPPDU it contains, are both just specializations of 
 *  SNMPSequence. Sequences are frequently nested within other sequences.
 */
public class SnmpSequence extends SnmpObject
                          //implements Iterable
{
    protected List<SnmpObject> sequence;    // List of whatever is in sequence
    protected SnmpBERType tag = SnmpBERType.SnmpSequence;
        
    /**
     *  Creates a new empty sequence.
     */
    public SnmpSequence()
    {
        sequence = new Vector<SnmpObject>();
    }
    
    
    /**
     *  Creates a new SNMP sequence from the supplied Vector of SNMPObjects.
     *  
     *  @throws SnmpBadValueException Thrown if non-SNMP object supplied in Vector v.
     */
    public SnmpSequence(Vector<SnmpObject> v)
        throws SnmpBadValueException
    {
        if (v == null)
        	throw new SnmpBadValueException("Sequence cannot be null");
    	
        sequence = v;
    }
    
      
    /**
     *  Constructs an SNMPMessage from a received ASN.1 byte representation.
     *  
     *  @throws SnmpBadValueException Indicates invalid SNMP sequence encoding supplied.
     */
    protected SnmpSequence(byte[] encoding)
        throws SnmpBadValueException
    {
        decode(encoding);
    }
    
    
    /**
     *  Returns a Vector containing the SNMPObjects in the sequence.
     */
    public Object getValue()
    {
        return sequence;
    }
    
    
    /** 
     *  Sets the contained SNMP objects from a supplied Vector.
     *  
     *  @throws SnmpBadValueException Indicates an incorrect object 
     *  type supplied, or that the supplied Vector contains non-SNMPObjects.
     */
    public void setValue(Object value)
        throws SnmpBadValueException
    {
        if (value instanceof Vector)
        {
        	Vector<?> newValue = (Vector<?>)value;
        	
            // Check that all objects in vector are SNMPObjects.
            List<SnmpObject> newSequence = new Vector<SnmpObject>(newValue.size());
            for (Object element : newValue)
            {
                if ( !(element instanceof SnmpObject) )
                    throw new SnmpBadValueException("Non-SNMPObject supplied to SNMPSequence.");

                newSequence.add((SnmpObject)element);
            }
            
            this.sequence = newSequence;
        }
        else
            throw new SnmpBadValueException(" Sequence: bad object supplied to set value ");
    }
    
    
    /** 
     *  Returns the number of SNMPObjects contained in the sequence.
     */
    public int size()
    {
        return sequence.size();
    }
    
    
    /**
     *  Returns an Iterator for the SNMPSequence's internal data list.
     */
    /*public Iterator iterator()
    {
        return sequence.iterator();
    }*/
    
    
    /** 
     *  Adds the SNMP object to the end of the sequence.
     *  
     *  @throws SnmpBadValueException Relevant only in subclasses
     */
    public void addSNMPObject(SnmpObject newObject)
        throws SnmpBadValueException
    {
        sequence.add(sequence.size(), newObject);
    }
    
    
    /** 
     *  Inserts the SNMP object at the specified position in the sequence.
     *  
     *  @throws SnmpBadValueException Relevant only in subclasses
     */
    public void insertSNMPObjectAt(SnmpObject newObject, int index)
        throws SnmpBadValueException
    {
        sequence.add(index, newObject);
    }
    
    
    /** 
     *  Returns the SNMP object at the specified index. Indices are 0-based.
     */
    public SnmpObject getSNMPObjectAt(int index)
    {
        return sequence.get(index);
    }
    

    /** 
     *  Returns the BER encoding for the sequence.
     */
    protected byte[] encode()
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
          
        // recursively write contents of Vector
        byte[] data = encodeVector();
        
        // calculate encoding for length of data
        byte[] len = SnmpBERCodec.encodeLength(data.length);
        
        // encode T,L,V info
        outBytes.write(tag.getByte());
        outBytes.write(len, 0, len.length);
        outBytes.write(data, 0, data.length);
        
        return outBytes.toByteArray();
    }
    
    
    private byte[] encodeVector()
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        
        //int numElements = sequence.size();
        //for (int i = 0; i < numElements; ++i)
        for (Object item : sequence)
        {
            byte[] nextBytes = ((SnmpObject)item).encode();
            outBytes.write(nextBytes, 0, nextBytes.length);
        }
        
        return outBytes.toByteArray();
    }
    
	/**
	 * Initializes an SNMP sequence with values extracted from a byte encoding.
	 * @param encoding
	 * @throws SnmpBadValueException
	 */
    protected void decode(byte[] encoding)
        throws SnmpBadValueException
    {
        List<SnmpObject> newVector = new Vector<SnmpObject>();
        
        int totalLength = encoding.length;
        int position = 0;
        
        while (position < totalLength)
        {
            SnmpTLV nextTLV = SnmpBERCodec.extractNextTLV(encoding, position);
            newVector.add(newVector.size(), SnmpBERCodec.decode(nextTLV));
            position += nextTLV.length;
        }
        
        sequence = newVector;
    }
    
    
    /** 
     *  Returns a sequence of representations of the contained objects, separated by spaces
     *  and enclosed in parentheses.
     */
    public String toString()
    {
        StringBuffer valueStringBuffer = new StringBuffer("(");
        
        //for (int i = 0; i < sequence.size(); ++i)
        for (Object item : sequence)
        {
            valueStringBuffer.append(" ");
            valueStringBuffer.append(item.toString());
            valueStringBuffer.append(" ");
        }
        
        valueStringBuffer.append(")");
        return valueStringBuffer.toString();
    }
    
}
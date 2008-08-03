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
public class SNMPSequence extends SNMPObject
                          //implements Iterable
{
    protected Vector sequence;    // Vector of whatever is in sequence
    
    protected SNMPBERType tag = SNMPBERType.SNMP_SEQUENCE;
        
    /**
     *  Create a new empty sequence.
     */
    public SNMPSequence()
    {
        sequence = new Vector();
    }
    
    
    /**
     *  Create a new SNMP sequence from the supplied Vector of SNMPObjects.
     *  
     *  @throws SNMPBadValueException Thrown if non-SNMP object supplied in Vector v.
     */
    public SNMPSequence(Vector v)
        throws SNMPBadValueException
    {
        for (Object item : v)
        {
            if ( !(item instanceof SNMPObject) )
                throw new SNMPBadValueException("Non-SNMPObject supplied to SNMPSequence.");
        }
        
        /*Enumeration e = v.elements();
        
        while (e.hasMoreElements())
        {
            if (!(e.nextElement() instanceof SNMPObject))
                throw new SNMPBadValueException("Non-SNMPObject supplied to SNMPSequence.");
        }*/
        
        sequence = v;
    }
    
      
    /**
     *  Construct an SNMPMessage from a received ASN.1 byte representation.
     *  
     *  @throws SNMPBadValueException Indicates invalid SNMP sequence encoding supplied.
     */
    protected SNMPSequence(byte[] enc)
        throws SNMPBadValueException
    {
        extractFromBEREncoding(enc);
    }
    
    
    /**
     *  Returns a Vector containing the SNMPObjects in the sequence.
     */
    public Object getValue()
    {
        return sequence;
    }
    
    
    /** 
     *  Used to set the contained SNMP objects from a supplied Vector.
     *  
     *  @throws SNMPBadValueException Indicates an incorrect object type supplied, or that the supplied
     *  Vector contains non-SNMPObjects.
     */
    public void setValue(Object newSequence)
        throws SNMPBadValueException
    {
        if (newSequence instanceof Vector)
        {
            // check that all objects in vector are SNMPObjects
            Vector newValue = (Vector)newSequence;
            for (Object item : newValue)
            {
                if ( !(item instanceof SNMPObject) )
                    throw new SNMPBadValueException("Non-SNMPObject supplied to SNMPSequence.");
            }
            
            /*Enumeration e = ((Vector)newSequence).elements();
            
            while (e.hasMoreElements())
            {
                if (!(e.nextElement() instanceof SNMPObject))
                    throw new SNMPBadValueException("Non-SNMPObject supplied to SNMPSequence.");
            }*/
            
            this.sequence = (Vector)newSequence;
        }
        else
            throw new SNMPBadValueException(" Sequence: bad object supplied to set value ");
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
     *  Add the SNMP object to the end of the sequence.
     *  
     *  @throws SNMPBadValueException Relevant only in subclasses
     */
    @SuppressWarnings("unchecked")
    public void addSNMPObject(SNMPObject newObject)
        throws SNMPBadValueException
    {
        sequence.add(sequence.size(), newObject);
    }
    
    
    /** 
     *  Insert the SNMP object at the specified position in the sequence.
     *  
     *  @throws SNMPBadValueException Relevant only in subclasses
     */
    @SuppressWarnings("unchecked")
    public void insertSNMPObjectAt(SNMPObject newObject, int index)
        throws SNMPBadValueException
    {
        sequence.add(index, newObject);
    }
    
    
    /** 
     *  Return the SNMP object at the specified index. Indices are 0-based.
     */
    public SNMPObject getSNMPObjectAt(int index)
    {
        return (SNMPObject)(sequence.get(index));
    }
    

    /** 
     *  Return the BER encoding for the sequence.
     */
    protected byte[] getBEREncoding()
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
          
        // recursively write contents of Vector
        byte[] data = encodeVector();
        
        // calculate encoding for length of data
        byte[] len = SNMPBERCodec.encodeLength(data.length);
        
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
            byte[] nextBytes = ((SNMPObject)item).getBEREncoding();
            outBytes.write(nextBytes, 0, nextBytes.length);
        }
        
        return outBytes.toByteArray();
    }
    

    protected void extractFromBEREncoding(byte[] enc)
        throws SNMPBadValueException
    {
        Vector<SNMPObject> newVector = new Vector<SNMPObject>();
        
        int totalLength = enc.length;
        int position = 0;
        
        while (position < totalLength)
        {
            SNMPTLV nextTLV = SNMPBERCodec.extractNextTLV(enc, position);
            newVector.add(newVector.size(), SNMPBERCodec.extractEncoding(nextTLV));
            position += nextTLV.totalLength;
        }
        
        sequence = newVector;
    }
    
    
    /** 
     *  Return a sequence of representations of the contained objects, separated by spaces
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
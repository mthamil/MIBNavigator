/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 * Copyright (C) 2010, Matt Hamilton <matthamilton@live.com>
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

package snmp.datatypes;

import java.io.*;
import java.util.Arrays;

import snmp.error.SnmpBadValueException;
import utilities.Strings;

/**
 *  Class representing ASN.1 object identifiers. These are unbounded sequences (arrays) of
 *  natural numbers, written as dot-separated strings.
 */
public class SnmpObjectIdentifier extends SnmpObject
{
    private long[] digits;    // array of longs

    protected SnmpBERType tag = SnmpBERType.SnmpObjectIdentifier;

    /**
     *  Creates a new empty object identifier (0-length array).
     */
    public SnmpObjectIdentifier()
    {
        digits = new long[0];
    }

    
    /**
     *  Creates a new object identifier from the supplied string of dot-separated nonegative
     *  decimal integer values.
     *  
     *  @throws SnmpBadValueException Indicates incorrectly-formatted string supplied.
     */
    public SnmpObjectIdentifier(String digitString)
        throws SnmpBadValueException
    {
        this.digits = SnmpObjectIdentifier.parseObjectIdentifier(digitString);
    }


    /**
     *  Creates a new object identifier from the supplied array of non-negative
     *  integer values.
     *  
     *  @throws SnmpBadValueException Negative value(s) supplied.
     */
    public SnmpObjectIdentifier(int[] digits)
        throws SnmpBadValueException
    {
        long[] longDigits = new long[digits.length];

        for (int i = 0; i < digits.length; i++)
        {
            if (digits[i] < 0)
                throw new SnmpBadValueException("Negative value supplied for SNMPObjectIdentifier.");

            longDigits[i] = digits[i];
        }

        this.digits = longDigits;
    }

    
    /**
     *  Creates a new object identifier from the supplied array of nonegative
     *  long values.
     *  
     *  @throws SnmpBadValueException Negative value(s) supplied.
     */
    public SnmpObjectIdentifier(long[] newDigits)
        throws SnmpBadValueException
    {
    	this.digits = new long[newDigits.length];
        for (int i = 0; i < newDigits.length; i++)
        {
            if (newDigits[i] < 0)
                throw new SnmpBadValueException("Negative value supplied for SNMPObjectIdentifier.");
            
            this.digits[i] = newDigits[i];
        }
    }


    /**
     *  Initializes from the BER encoding, as received in a response from
     *  an SNMP device responding to an SNMPGetRequest.
     *  
     *  @throws SnmpBadValueException Indicates an invalid BER encoding supplied. Shouldn't
     *  occur in normal operation, i.e., when valid responses are received from devices.
     */
    public SnmpObjectIdentifier(byte[] encoding)
        throws SnmpBadValueException
    {
        extractFromBEREncoding(encoding);
    }


    /**
     *  Returns array of integers corresponding to components of identifier.
     */
    @Override
    public Object getValue()
    {
        return digits;
    }


    /**
     *  Sets the value from an integer or long array containing the identifier components, or from
     *  a String containing a dot-separated sequence of nonegative values.
     *  
     *  @throws SnmpBadValueException Indicates an incorrect object type supplied, or negative array
     *  elements, or an incorrectly formatted String.
     */
    @Override
    public void setValue(Object newDigits)
        throws SnmpBadValueException
    {
        if (newDigits instanceof long[])
        {
        	long[] longDigits = (long[])newDigits;
        	
        	this.digits = new long[longDigits.length];
            for (int i = 0; i < longDigits.length; i++)
            {
                if (longDigits[i] < 0)
                    throw new SnmpBadValueException("Negative value supplied for SNMPObjectIdentifier.");
                
                this.digits[i] = longDigits[i];
            }
        }
        else if (newDigits instanceof int[])
        {
        	int[] intDigits = (int[])newDigits;
        	
        	this.digits = new long[intDigits.length];
            for (int i = 0; i < intDigits.length; i++)
            {
                if (intDigits[i] < 0)
                    throw new SnmpBadValueException("Negative value supplied for SNMPObjectIdentifier.");

                this.digits[i] = intDigits[i];
            }
        }
        else if (newDigits instanceof String)
        {
        	this.digits = SnmpObjectIdentifier.parseObjectIdentifier((String)newDigits);
        }
        else
        {
            throw new SnmpBadValueException(" Object Identifier: bad object supplied to set value ");
        }
    }


    /**
     *  Returns the BER encoding for this object identifier.
     */
    @Override
    public byte[] encode()
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

        // write contents of array of values
        byte[] data = encodeArray();

        // calculate encoding for length of data
        byte[] len = SnmpBERCodec.encodeLength(data.length);

        // encode T,L,V info
        outBytes.write(tag.getByte());
        outBytes.write(len, 0, len.length);
        outBytes.write(data, 0, data.length);

        return outBytes.toByteArray();
    }


    private byte[] encodeArray()
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

        int numElements = digits.length;

        // encode first two identifier digits as one byte, using the 40*x + y rule;
        // of course, if only one element, just use 40*x; if none, do nothing
        if (numElements >= 2)
            outBytes.write((byte)(40*digits[0] + digits[1]));
        else if (numElements ==1)
            outBytes.write((byte)(40*digits[0]));


        for (int i = 2; i < numElements; ++i)
        {
            byte[] nextBytes = encodeValue(digits[i]);
            outBytes.write(nextBytes, 0, nextBytes.length);
        }

        return outBytes.toByteArray();
    }


    private byte[] encodeValue(long v)
    {
        // see how many bytes are needed: each value uses just
        // 7 bits of each byte, with high-order bit functioning as
        // a continuation marker
        int numBytes = 0;
        long temp = v;

        do
        {
            ++numBytes;
            temp = (long)Math.floor(temp / 128);
        }
        while (temp > 0);


        byte[] enc = new byte[numBytes];
        // encode lowest-order byte, without setting high bit
        enc[numBytes - 1] = (byte)(v % 128);
        v = (long)Math.floor(v / 128);

        //.encode other bytes with high bit set
        for (int i = numBytes - 2; i >= 0; --i)
        {
            enc[i] = (byte)((v % 128) + 128);
            v = (long)Math.floor(v / 128);
        }

        return enc;
    }


    /**
     * Attempts to parse an OID string into a long array.
     * @param digitString
     * @throws SnmpBadValueException
     */
    public static long[] parseObjectIdentifier(String digitString)
        throws SnmpBadValueException
    {
		try
		{
			// Matt Hamilton on 1/31/06:
			// Rewritten to use String's split method instead of StringTokenizer.

			String[] oidArray = digitString.split("\\.");

			long[] returnDigits = new long[oidArray.length];

			for (int i = 0; i < oidArray.length; i++)
			{
				returnDigits[i] = Long.parseLong(oidArray[i]);
				if (returnDigits[i] < 0)
					throw new SnmpBadValueException(" Object Identifier: values must greater than or equal to zero. ");
			}

			return returnDigits;

		}
		catch (NumberFormatException e)
		{
			throw new SnmpBadValueException(" Object Identifier: bad string supplied for object identifier value ");
        }

    }


    private void extractFromBEREncoding(byte[] encoding)
        throws SnmpBadValueException
    {
        // first, compute number of "digits";
        // will just be number of bytes with leading 0's
        int numInts = 0;
        for (int i = 0; i < encoding.length; i++)
        {
            if (!highOrderBitIsSet(encoding[i]))        //high-order bit not set; count
                numInts++;
        }

        if (numInts > 0)
        {
            // create new int array to hold digits; since first value is 40*x + y,
            // need one extra entry in array to hold this.
            digits = new long[numInts + 1];

            int currentByte = -1;    // will be incremented to 0

            long value = 0;

            // read in values 'til get leading 0 in byte
            do
            {
                currentByte++;
                value = value * 128 + (encoding[currentByte] & highBitMask);
            }
            while (highOrderBitIsSet(encoding[currentByte]));    // implies high bit set!

            // now handle 40a + b
            digits[0] = (long)Math.floor(value / 40);
            digits[1] = value % 40;

            // now read in rest!
            for (int i = 2; i < numInts + 1; i++)
            {
                // read in values 'til get leading 0 in byte
                value = 0;
                do
                {
                    currentByte++;
                    value = value*128 + (encoding[currentByte] & highBitMask);
                }
                while (highOrderBitIsSet(encoding[currentByte]));

                digits[i] = value;
            }

        }
        else
        {
            // no digits; create empty digit array
            digits = new long[0];
        }

    }
    
    private static boolean highOrderBitIsSet(byte b)
    {
    	return (b & bitTest) > 0;
    }
    
    // note: masks must be ints; byte internal representation issue(?)
    private static final int highBitMask = 0x7F;    // mask out high bit for value
    private static final int bitTest = 0x80;    	// test for leading 1


    /**
     *  Checks the internal arrays for equality.
     */
    @Override
    public boolean equals(Object other)
    {
        // false if other is null
        if (other == null)
            return false;

        // check first to see that they're both of the same class
        if (!this.getClass().equals(other.getClass()))
            return false;

        SnmpObjectIdentifier otherSNMPObject = (SnmpObjectIdentifier)other;

        // see if their embedded arrays are equal
        if (Arrays.equals((long[])this.getValue(), (long[])otherSNMPObject.getValue()))
            return true;
         
        return false;
    }


    /**
     *  Generates a hash value so SNMP Object Identifiers can be used in Hashtables.
     */
    @Override
    public int hashCode()
    {
        int hash = 0;

        // generate a hashcode from the embedded array
        for (int i = 0; i < digits.length; i++)
        {
            hash += (int)(digits[i] ^ (digits[i] >> 32));
            hash += (hash << 10);
            hash ^= (hash >> 6);
        }

        hash += (hash << 3);
        hash ^= (hash >> 11);
        hash += (hash << 15);

        return hash;
    }


    /**
     *  Returns a dot-separated sequence of decimal values.
     */
    @Override
    public String toString()
    {
        return Strings.join(".", digits);
    }

}
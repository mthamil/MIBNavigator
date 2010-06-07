/**
 * Utilities
 *
 * Copyright (C) 2010, Matt Hamilton <matthamilton@live.com>
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

package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * Class containing useful IO utility methods.
 */
public class IOUtilities
{
	private IOUtilities() { }
	
    /**
     * Simply copies a file from one location to another.
     * 
     * @param sourceFile the source file to copy
     * @param destFile the destination of the copy operation
     * @return whether the copy succeeded or not
     */
    public static boolean copyFile(File sourceFile, File destFile)
    {
    	boolean succeeded = true;
    	FileChannel source = null;
        FileChannel destination = null;
    	
        try
        {
        	source = new FileInputStream(sourceFile).getChannel();
        	destination = new FileOutputStream(destFile).getChannel();
	    
	        destination.transferFrom(source, 0, source.size());
        }
        catch(IOException e)
        {
        	succeeded = false;
        }
        finally
        {
        	closeQuietly(source);
        	closeQuietly(destination);
        }
        
        return succeeded;
    }
    
    
    /**
     * Taking a cue from Apache commons, unconditionally closes an 
     * input stream.
     * 
     * @param stream
     */
    public static void closeQuietly(InputStream stream)
    {
    	try
	    {
			if (stream != null)
				stream.close();
	    }
	    catch (IOException e)
	    {
	    	// Do nothing, do not care.
	    }
    }
    
    
    /**
     * Taking a cue from Apache commons, unconditionally closes an 
     * output stream.
     * 
     * @param stream
     */
    public static void closeQuietly(OutputStream stream)
    {
    	try
	    {
			if (stream != null)
				stream.close();
	    }
	    catch (IOException e)
	    {
	    	// Do nothing, do not care.
	    }
    }
    
    /**
     * Taking a cue from Apache commons, unconditionally closes a
     * reader.
     * 
     * @param reader
     */
    public static void closeQuietly(Reader reader)
    {
    	try
	    {
			if (reader != null)
				reader.close();
	    }
	    catch (IOException e)
	    {
	    	// Do nothing, do not care.
	    }
    }
    
    /**
     * Taking a cue from Apache commons, unconditionally closes a
     * writer.
     * 
     * @param writer
     */
    public static void closeQuietly(Writer writer)
    {
    	try
	    {
			if (writer != null)
				writer.close();
	    }
	    catch (IOException e)
	    {
	    	// Do nothing, do not care.
	    }
    }
    
    
    /**
     * Unconditionally closes a channel.
     * 
     * @param channel
     */
    public static void closeQuietly(Channel channel)
    {
    	
    	try
    	{
    		if (channel != null)
    			channel.close();
    	}
    	catch (IOException e)
    	{
    		// Do nothing, do not care.
    	}
    	
    	
    }

}

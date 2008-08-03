/**
 * MIB Navigator
 *
 * Copyright (C) 2005, Matt Hamilton <matthew.hamilton@washburn.edu>
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Class for managing MIBNavigator application saved properties.  It provides an application specific 
 * layer to an underlying Properties object so that saved state values can be more easily processed.
 */
public class MIBNavigatorSettings
{
    private final File SETTINGS_FILE = new File("." + File.separator + "properties.xml");
    private static final String IP_PROPERTY = "IPAddressList";
    private static final String MAX_ADDRESS_PROPERTY = "MaximumStoredAddresses";
    private static final int DEFAULT_MAX_ADDRESSES = 15;
    
    private Properties settings;
    private boolean settingsChanged;
    private int maxAddresses;
    private List<String> addressList;
    
    /**
     * Initializes MibBrowser settings.
     */
    public MIBNavigatorSettings()
    {
        settings = new Properties();
        settingsChanged = false;
    }
    
    /**
     * Gets the maximum allowed number of stored IP addresses.
     */
    public int getMaxAddresses()
    {    
        return maxAddresses;
    }
    
    /**
     * Sets the maximum allowed number of stored IP addresses.
     * Negative values will be treated as choosing to save no addresses.
     */
    public void setMaxAddresses(int newMax)
    {
        if (newMax < 0)
            newMax = 0;
        
        if (newMax != maxAddresses)
        {
            maxAddresses = newMax;
            settingsChanged = true;
        }
    }
    
    /**
     * Gets the list of saved IP addresses.
     */
    public List<String> getAddressList()
    { 
        return addressList;
    }
    
    /**
     * Sets the list of saved IP addresses.
     */
    public void setAddressList(List<String> newList)
    {
        if (newList == null)
            throw new IllegalArgumentException("Address list cannot be null.");
        
        if (newList != addressList)
        {
            // Check to to see if the Lists are equivalent.
            if (!newList.equals(addressList))
            {
                addressList = newList; 
                settingsChanged = true;
            }
        }
    }
    
    
    
    /**
     * Loads saved settings from a properties file.
     */
    public void loadSettings()
    {
        FileInputStream settingsIn = null;
        String hostIPAddresses = "";
        String addressNum = "";
        try
        {
            settingsIn = new FileInputStream(SETTINGS_FILE);
            settings.loadFromXML(settingsIn);
            if (settingsIn != null)
                settingsIn.close();

            addressNum = settings.getProperty(MAX_ADDRESS_PROPERTY, "15");
            hostIPAddresses = settings.getProperty(IP_PROPERTY, "");
        }
        catch (IOException e)
        {
            System.out.println("Error loading properties file.");
        }
        
        
        // Parse the the max address property.
        if (!addressNum.equals(""))
        {
            try
            {
                maxAddresses = Integer.parseInt(addressNum);
                
                if (maxAddresses < 0)  // Why anyone would do this, I don't know.
                    maxAddresses = 0;
            }
            catch (NumberFormatException e)
            {
                maxAddresses = DEFAULT_MAX_ADDRESSES;
            }
        }
        else
            maxAddresses = DEFAULT_MAX_ADDRESSES;
        
            
        // Parse the address list property.  If the property retrieved was an empty String, an empty List will be created.
        StringTokenizer tokenizer = new StringTokenizer(hostIPAddresses, ",");
        int i = 0;
        addressList = new ArrayList<String>(maxAddresses);
        while (tokenizer.hasMoreTokens() && i < maxAddresses)
        {
            addressList.add(tokenizer.nextToken());
            i++;
        }
    }
    
    
    
    /**
     *  Saves settings to a properties file.
     */
    public void saveSettings()
    {
        if (settingsChanged)
        {
            if (!addressList.isEmpty())
            {
                // Put IP addresses into a single, comma delimited string. The addresses that are saved 
                // are limited to the first N addresses of the list, with N determined maxAddresses.
                StringBuilder ipAddresses = new StringBuilder();
                
                int maxItems = Math.min(addressList.size(), maxAddresses);
                for (int i = 0; i < maxItems; i++)  
                    ipAddresses.append("," + addressList.get(i));
                
                // Trim off the leading comma.
                String addresses = ipAddresses.toString();
                if (addresses.startsWith(","))
                    addresses = addresses.substring(addresses.indexOf(",") + 1);
                
                settings.setProperty(IP_PROPERTY, addresses);
            }
                
            settings.setProperty(MAX_ADDRESS_PROPERTY, String.valueOf(maxAddresses));
            
            // Save program state settings to file.
            FileOutputStream settingsOut = null;
            try
            {
                settingsOut = new FileOutputStream(SETTINGS_FILE);
                settings.storeToXML(settingsOut, null);
                if (settingsOut != null)
                    settingsOut.close();
            }
            catch (IOException e)
            {
                System.out.println("Error saving properties file.");
            }
        }
    }
    
    
    
    // Code testing a different method for saving settings.
   /* public static void main(String[] args)
    {
        try
        {
            XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("Test.xml")));
            
            MIBNavigatorSettings test = new MIBNavigatorSettings();
            test.loadSettings();
            
            encoder.writeObject(test);
            encoder.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        
        try
        {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("Test.xml")));
            
            MIBNavigatorSettings test = (MIBNavigatorSettings)decoder.readObject();
            decoder.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        
    }*/
        
}

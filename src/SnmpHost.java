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

import snmp.SnmpV1Communicator;

/**
 * Class containing information necessary for communication with a host device
 * providing SNMP services.
 */
public class SnmpHost
{
    private String communityString;
    private String addressString;
    private int hostPort;
    private int hostTimeout;

    /**
     * Creates new SnmpHost communication parameters using the default
     * port number.
     * @param communityString the SNMP community string for a target host
     * @param addressString the <code>String</code> representation of a target host's IP address
     * @param timeout the request timeout
     */
    public SnmpHost(final String communityString, final String addressString, final int timeout)
    {
    	this.communityString = communityString;
    	this.addressString = addressString;
        this.hostPort = SnmpV1Communicator.DEFAULT_SNMP_PORT;
        this.hostTimeout = timeout;
    }
    
    /**
     * Creates new SnmpHost communication parameters.
     * @param communityString the SNMP community string for a target host
     * @param addressString the <code>String</code> representation of a target host's IP address
     * @param port the host port
     * @param timeout the request timeout
     */
    public SnmpHost(final String communityString, final String addressString, final int port, final int timeout)
    {
    	this.communityString = communityString;
    	this.addressString = addressString;
        this.hostPort = port;
        this.hostTimeout = timeout;
    }
    
    /**
     * Sets the community string.
     * @param newCommunityString
     */
    public void setCommunityString(String newCommunityString)
    {
    	communityString = newCommunityString;
    }
    
    /**
     * Gets the community string.
     */
    public String getCommunityString()
    {
    	return communityString;
    }
	
    /**
     * Sets the IP address string.
     * @param newAddress
     */
    public void setAddress(String newAddress)
    {
    	addressString = newAddress;
    }
    
    /**
     * Gets the IP address string.
     */
    public String getAddress()
    {
    	return addressString;
    }
    
	 /**
     * Sets the port number used for requests.
     * @param newPort
     */
    public void setPort(int newPort)
    {
        hostPort = newPort;
    }
    
	 /**
     * Gets the port number used for requests.
     */
    public int getPort()
    {
        return hostPort;
    }
    
    /**
     * Sets the timeout in milliseconds for requests.
     * @param newTimeout
     */
    public void setTimeout(int newTimeout)
    {
        hostTimeout = newTimeout;
    }
    
    /**
     * Gets the timeout in milliseconds for requests.
     */
    public int getTimeout()
    {
        return hostTimeout;
    }
}

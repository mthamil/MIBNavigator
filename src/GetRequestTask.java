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

import java.io.InterruptedIOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;

import snmp.SnmpBadValueException;
import snmp.SnmpGetException;
import snmp.SnmpInteger;
import snmp.SnmpObject;
import snmp.SnmpObjectIdentifier;
import snmp.SnmpSequence;
import snmp.SnmpV1Communicator;
import snmp.SnmpVarBindList;

import libmib.MibObjectType;
import libmib.mibtree.MibTreeNode;
import libmib.mibtree.MibTreeNode.NodeSearchOption;

/**
 *  This class retrieves OID values using SNMP GetNextRequests for a given host.  It executes in 
 *  a separate thread so that the user interface of the application that uses this class does not 
 *  freeze. It extends the Java <code>SwingWorker</code> abstract class that is recommended for 
 *  use in updating Swing components from other threads.
 *  <br><br>
 *  The retrieval process begins with a specified base OID and stops when the next OID no longer 
 *  begins with this base OID, when the user interrupts the process, or an exception occurs.
 *  <br><br>
 *  <code>GetRequestTask</code> notifies other objects of incoming data and state changes through 
 *  a simplified form of the event listener pattern commonly used by many other Java classes.  As 
 *  is usually the case with this pattern, the listening class must implement a listener interface, 
 *  in this case <code>GetRequestListener</code>, in order to receive data from the <code>GetRequestWorker</code>.  
 *  Finally, the 3 kinds of data events generated by <code>GetRequestTask</code> are so disjoint 
 *  that they may seem to warrant 3 separate listener interfaces, but for simplicity a single listener 
 *  interface is used even though no single type of event is generated.
 */
public class GetRequestTask extends SwingWorker<String, GetRequestResult>
{
	private EventListenerList requestListeners = new EventListenerList();
    private GetRequestResultProcessor resultProcessor;
	
    private final SnmpHost host;
    private final String oidInputString;
    private final MibTreeNode root;
    
    private static final int SNMP_VERSION = 0;
    private static final String STD_PREFIX = "iso.org.dod.internet.mgmt.mib-2.";
    private static final String ENT_PREFIX = "iso.org.dod.internet.private.enterprises.";
    
    /**
     * Initializes the get request task with all necessary values.
     * 
     * @param host parameters for communicating with an SNMP host device
     * @param oidString the starting OID for the GetRequest
     * @param rootNode the <code>MibTreeNode</code> root of the MIB tree
     */
    public GetRequestTask(final SnmpHost host, final String oidString, final MibTreeNode rootNode)
    {
        if (host == null)
        	throw new IllegalArgumentException("Host cannot be null");
    	
        if (rootNode == null)
            throw new IllegalArgumentException("Root node cannot be null.");
        
        this.host = host;
        this.oidInputString = oidString;
        this.root = rootNode;
    }
    
    /**
     * Sets the processor that will handle the individual results of each get request.
     * @param processor
     */
    public void setResultProcessor(GetRequestResultProcessor processor)
    {
    	resultProcessor = processor;
    }
    
    /**
     * Adds a new GetRequestListener object so that it can handle worker events. 
     * This method is not safe once the Worker's start method has been called.
     * 
     * @param newListener the GetRequestListener that will receive data
     */
    public void addGetRequestListener(GetRequestListener newListener) 
    {
    	requestListeners.add(GetRequestListener.class, newListener);
    }


	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#done()
	 */
	@Override
	protected void done()
	{
		if (!isCancelled())
		{
			try
			{
				this.fireRequestTerminationEvent(this.get().toString());
			}
			catch (InterruptedException e)
			{
				this.fireRequestTerminationEvent("");
			}
			catch (ExecutionException e)
			{
				this.fireRequestTerminationEvent("");
			}
		}
		else
		{
			this.fireRequestTerminationEvent("");
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#process(java.util.List)
	 */
	@Override
	protected void process(List<GetRequestResult> chunks)
	{
		//System.out.println(chunks.size());
		if (resultProcessor != null)
		{
			for (GetRequestResult result : chunks)
				resultProcessor.processResult(result);
		}
	}

	@Override
	protected String doInBackground() throws Exception
	{
		return doGetRequest();
	}
	
	 /**
     * Retrieves SNMP values via GetNextRequests. GetRequestListeners are updated with the 
     * results through operations wrapped in Runnables and executed using invokeLater when 
     * appropriate in order to update GUI components in the Event Dispatch Thread.
     * 
     * @return the final String result produced by the thread. In this case, the 
     *         method will return an empty String on successful execution, and a String error 
     *         message when an exception occurs.
     */
    private String doGetRequest() 
    {
        String baseOid = oidInputString;

        // Create local copies for data integrity during the GetRequest process since these fields can be set after construction.
        int port = host.getPort();
        int timeout = host.getTimeout();
        
        try
        {
            // Get the IP Address and attempt to resolve it; if the address is valid, update the interface.
            // Unfortunately, pressing the Stop button during address resolution/lookup will have no immediate
            // effect since these calls cannot be interrupted. However, they also cannot be done outside of this thread
            // because an exception in address resolution that indicates an invalid address should stop the GET process.
            InetAddress address = InetAddress.getByName(host.getAddress());
            String resolvedAddr = address.getCanonicalHostName();
            this.fireAddressResolvedEvent(host.getAddress(), resolvedAddr);  // this will occur if the host address is valid

            // Establish a new SNMPv1 interface with the given data.
            SnmpV1Communicator snmpInterface = new SnmpV1Communicator(SNMP_VERSION, address, host.getCommunityString());
            snmpInterface.setTimeout(timeout);
            snmpInterface.setPort(port);

            SnmpVarBindList newVarBinds;
            SnmpObjectIdentifier receivedOid;
            SnmpObject snmpValue;
            SnmpSequence pair;

            String nextOid = baseOid;

            // Retrieve all values until the next OID does not start with the base OID (walk the tree)
            // or the thread is interrupted by the user pressing the stop button.
            while (nextOid.startsWith(baseOid))
            {
                // Instead of checking at the while condition like normal Threads,
                // check for an interrupt here so that an exception can be thrown.
                if (Thread.interrupted()) 
                    throw new InterruptedException();

                newVarBinds = snmpInterface.getNextMIBEntry(nextOid);
 
                // Extract OID information from the VarBindList.
                pair = (SnmpSequence)newVarBinds.getSNMPObjectAt(0);
                
                receivedOid = (SnmpObjectIdentifier)pair.getSNMPObjectAt(0);
                nextOid = receivedOid.toString();

                // This check stops the last OID, which will not start 
                // with with the base OID, from being displayed.
                if (nextOid.startsWith(baseOid)) 
                {
                    // NOTE: the remaining interactions with any Swing components in this thread are all
                    // with data models, and none of them are updated.  From what I've read,
                    // the single thread rule applies to UPDATING Swing VISUAL components.
                    
                    MibTreeNode node = root.getNodeByOid(nextOid, NodeSearchOption.MatchNearestPath);
                    
                    String displayOid = nextOid;
                    
                    // If the OID or the nearest OID was found in the tree, resolve and format the OID for display.
                    if (node != null)
                        displayOid = formatDisplayOid(node, nextOid);

                    // Extract the returned value from the VarBindList and convert it to a String.
                    snmpValue = pair.getSNMPObjectAt(1);
                    String snmpValueString = snmpValue.toString();                     
                    
                    // There is a potential problem here because the closest node is returned if the exact
                    // match is not found.  However, it seems inefficient to do another search with the
                    // option to return the exact node.
                    if (node != null && (snmpValue instanceof SnmpInteger) && (node.getUserObject() instanceof MibObjectType))
                    {
                        MibObjectType mibObject = (MibObjectType)node.getUserObject();
                        if (mibObject.hasNameValuePairs())
                        {
                            int value = ((BigInteger)snmpValue.getValue()).intValue();
                            String name = mibObject.getSyntax().findValueName(value);
                            
                            // Name will be empty if either the value wasn't found or for some reason the name was "".
                            // Either way, the number is more informative than an empty String in this case.
                            if (!name.equals(""))
                                snmpValueString = name; 
                        }
                    }

                    GetRequestResult result = new GetRequestResult(displayOid, nextOid, snmpValueString);
                    this.publish(result);
                    //this.fireResultReceivedEvent(result);
                }

                // Attempt to slow this sucker down a bit so it doesn't swamp the agent device
                // and so requests are more easily cancellable.  Also, a long running request
                // tends to freeze eventually. This helps alleviate things.
                //Thread.sleep(5);
            }
            
            return ""; // successful execution and normal termination
        }
        catch (InterruptedException e)
        {
            return "";  
        }
        catch (SnmpBadValueException e)
        {
            return e.getMessage();
        }
        catch (SocketTimeoutException e)
        {
            return StringResources.getString("timeoutGetErrorMessage") + e.getMessage();
        }
        catch (InterruptedIOException e)
        {
            return StringResources.getString("interruptedGetErrorMEssage") + e.getMessage();
        }
        catch (UnknownHostException e) 
        {
            return StringResources.getString("unknownHostErrorMEssage") + e.getMessage();
        }
        catch (SnmpGetException e)
        {
            return e.getMessage();
        }
        catch (Exception e) // not recommended, but exceptional circumstances will likely always prevent successful execution of the GetRequest process
        {
            return StringResources.getString("generalGetErrorMessage") + e.getMessage();
        }
    }
    
    
    /**
     * Replaces a portion of a numerical oid with its equivalent named OID as found in a 
     * MibTree and trims the beginning path.  For example: 1.3.6.1.2.1.1.1.0 will be 
     * changed to system.sysDescr.0
     * 
     * @return a resolved and formatted display OID String
     */
    private static String formatDisplayOid(MibTreeNode node, String oidString)
    {
        // Get the full name and numeral paths of the node.
        String[] paths = node.getOidPaths(); 
        String oidNumeralPath = paths[0];
        String oidNamePath = paths[1];

        if (oidString.startsWith(oidNumeralPath)) // make sure the OID numeral pattern isn't matched elsewhere in a really long OID
        {
            oidString = oidString.replaceFirst(oidNumeralPath, oidNamePath);

            // This is a bit of a hack since I'm trying to replicate the way GetIf displays
            // OID names during a GET. All it does is chop off the beginning parts of the 
            // OID paths to improve display.
            if (oidString.contains(STD_PREFIX))
                oidString = oidString.substring(oidString.indexOf(STD_PREFIX) + STD_PREFIX.length());
            else if (oidString.contains(ENT_PREFIX))
                oidString = oidString.substring(oidString.indexOf(ENT_PREFIX) + ENT_PREFIX.length());
        }
        
        return oidString;
    }
    
    

    // *** Firing methods for updating the GetRequestListeners ***

    private void fireAddressResolvedEvent(final String address, final String resolvedAddress)
    {
        final Object[] listeners = requestListeners.getListenerList();
        
        for (int i = listeners.length - 2; i >= 0; i -= 2) 
        {
            if (listeners[i] == GetRequestListener.class) 
            {
                final GetRequestListener currentListener = (GetRequestListener)listeners[i + 1];
                Runnable doFireAddressResolved = new Runnable() 
                {
                    public void run() 
                    {
                        currentListener.hostAddressResolved(address, resolvedAddress);
                    }
                };
                SwingUtilities.invokeLater(doFireAddressResolved);
            }
        }
    }
    
    private void fireResultReceivedEvent(final GetRequestResult result)
    {
        final Object[] listeners = requestListeners.getListenerList();
        //boolean test = SwingUtilities.isEventDispatchThread();
        for (int i = listeners.length - 2; i >= 0; i -= 2) 
        {
            if (listeners[i] == GetRequestListener.class) 
            {
                final GetRequestListener currentListener = (GetRequestListener)listeners[i + 1];
                Runnable doFireResultReceived = new Runnable() 
                {
                    public void run() 
                    {
                        currentListener.requestResultReceived(result);
                    }
                };
                SwingUtilities.invokeLater(doFireResultReceived);
            }
        }
    }
    
    private void fireRequestTerminationEvent(final String statusMessage)
    {
        final Object[] listeners = requestListeners.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) 
        {
            if (listeners[i] == GetRequestListener.class) 
            {
                final GetRequestListener currentListener = (GetRequestListener)listeners[i + 1];
                Runnable doRequestTerminated = new Runnable() 
                {
                    public void run() 
                    {
                        currentListener.requestTerminated(statusMessage);
                    }
                };
                SwingUtilities.invokeLater(doRequestTerminated);
            }
        }
    }
    

}

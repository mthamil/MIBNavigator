# MIB Navigator
MIB Navigator is a Java-based GUI desktop application for browsing SNMP MIBs and querying hosts that are running SNMPv1 agents.
Each "object" OID in an SNMP MIB is represented as a node in a tree, and when a node is selected, certain information about 
that particular OID is displayed.  Functionality is provided for adding new MIB definition files to the tree.  

Additional MIB files can be found in the `extra-mibs` directory. These are taken from the MIBs available for download here: http://www.wtcs.org/snmp4tpc/FILES/Tools/SNMP/getif/GETIF-MIBS.ZIP.

## Installation
To install, unzip the downloaded archive file (ie. `mibnavigator-<version>-bin.zip`) to a location, and a directory containing all needed files will be created.


## Instructions
To start the application, run the self-executing JAR file, `MIBNavigator.jar` by either double clicking it (if supported) or using the console command `java -jar MIBNavigator.jar`.

In order to query an SNMP-enabled host, enter an IP address, community string, and an OID.  Then, click the `Get Data` button. This will walk the tree below this OID and display the results in the `Results` pane. The timeout amount and port used by the request can be entered as well,but most users should leave the default values.

OIDs can be selected from the MIB tree or entered manually.
A numerical OID can be searched for in the MIB tree by entering the OID in the `OID` input field and pressing enter. If it is found, the appropriate node will be selected in the tree.

To add a MIB to the tree temporarily (meaning the MIB will not be re-added on next program start-up), select the `Add MIB` option from the `Options` menu on the menu bar.
 
To add a MIB to the tree permanently (meaning the MIB will be copied to the default MIB directory for loading on subsequent start-ups), select the `Import MIB` option from the `Options` menu on the menu bar. MIB files can also be added to the default directory manually.

If a given MIB is added to the tree but does not appear, check the dependencies of the MIBs, since another module may need to be loaded first.
 
 
## Settings
Certain aspects of the application can be changed using the configuration settings stored in `proprties.xml`, which is saved on a per-user basis (ie. on Linux, in `/home/<username>/.mibnavigator`). 

The following describes each setting:
- MibDirectory: The default directory that MIB Navigator uses to load MIB files on startup.

- MaximumAddresses: The maximum number of IP addresses MIB Navigator remembers. The default number of addresses is 15.

- IPAddresses: When an IP address is successfully contacted by the application, it is added to this list of addresses and displayed in the address drop-down.
NOTE: If the maximum number of addresses to save is changed to be less than the number of addresses saved in the list, a loss of addresses may occur due to address list truncation. Remember to back up the properties file after modifying the number of addresses.

- Port: The last port number used to query an SNMP host.

- Timeout: The amount of time in milliseconds to wait for a response from an SNMP host.
 
This version of MIB Navigator only supports versions of Java that are 6.0(1.6) or higher due to new language features. It has been tested on Windows and Linux.

Acknowledgements: Jon Sevy, for creating and releasing Java SNMP: https://jsevy.com/wordpress/index.php/java-and-android/snmp/
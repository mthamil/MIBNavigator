# Changelog

## v1.5
 - Updated compatibility for Java 6.  Now requires at least Java 6.
 - Added external resources for strings.
 - Added external resources for platform-specific UI settings such as text field and button sizes, font sizes, etc.
 - Added support for GTK look and feel.
 - Added configuration settings for the MIB file format to use, the default MIB directory location, port, and timeout. 
 - SMI is now the default MIB format.
 - Switched to Java 6's SwingWorker.
 - Added validation to port and timeout text fields.
 - Continued refactoring of application internals.

## v1.0.1
 - Fixed a bug in MibTreeBuilderSmi where the parser failed to properly ignore "MACRO" definitions. (Credit goes to JJMolini at SourceForge)
 - Moved the code shared by MibTreeBuilderXml and MibTreeBuilderSmi into AbstractMibTreeBuilder.
 - Refactored some of the SMI parsing code to increase sharing between MIBToXMLConverter and MibTreeBuilderSmi.
 - Significantly refactored the code in the SNMP package in order to increase code reuse and enforce type safety.

## v1.0
 - Added user input fields for the port used by SNMP and timeout used during requests in milliseconds.  The port option was included because
 some SNMP agents can be configured to listen on other ports.
 - Added a property in the 'properties.xml' file to allow the user to set the number of IP addresses MIB Navigator should save.
 - Changed the way settings worked regarding the maximum number of addresses.  Now the maximum number of addresses no longer affects how many addresses can be displayed while the program is running, but only the last 'N' addresses that will be saved and loaded.

## v0.9.1
 - Further modularization of the application code, specifically the interface components.
 - Various algorithm optimizations.
 - Began using Java 5.0 enumerations for access and status oid attributes.
 - Miscellaneous code refactoring.

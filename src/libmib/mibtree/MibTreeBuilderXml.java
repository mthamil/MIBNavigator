/**
 * libmib - Java SNMP Management Information Base Library
 *
 * Copyright (C) 2008, Matt Hamilton <mhamilton2383@comcast.net>
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

package libmib.mibtree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import libmib.InvalidXmlMibFormatException;
import libmib.oid.MibObjectType;
import libmib.oid.MibSyntax;
import libmib.oid.MibNameValuePair;
import libmib.oid.MibObjectType.Access;
import libmib.oid.MibObjectType.Status;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * This class reads XML files that conform to the mib XML schema definition. The schema 
 * specifies an XML format for defining a MIB module. MibTreeBuilderXml reads these XML 
 * files, validates them against the schema, and contructs DOM trees using Java's XML 
 * library.  
 * <br><br>
 * Relevant information is then extracted from the DOM and put into a DefaultTreeModel.  
 * The builder only pays attention to a simple subset of the XML MIB schema definition 
 * because not all information is currently used. Nodes in the tree are indexed by a 
 * HashMap that maps MIB object names to MIBTreeNodes.
 */
public class MibTreeBuilderXml extends AbstractMibTreeBuilder
{
    private DocumentBuilder docBuilder; //XML parser
    private Validator mibValidator;     //Validator based on the schema

    /**
     * Constructs a new MibTreeBuilderXml that reads XML MIB files.  The tree data 
     * structure that will contain the MIB objects is initialized with a basic set 
     * of nodes.
     * 
     * @param newMIBSchemaFile the XML schema file that will be used to validate 
     *        XML MIB files
     * 
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created 
     *         which satisfies the configuration requested
     * @throws SAXException if a SAX error occurs when parsing the schema file
     */
    public MibTreeBuilderXml(File newMIBSchemaFile) throws ParserConfigurationException, SAXException
    {
        // Configure the parser.
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        docBuilderFactory.setIgnoringComments(true);
        
        docBuilder = docBuilderFactory.newDocumentBuilder();
        docBuilder.setErrorHandler(new BasicXmlErrorHandler());
        
        // Create a Validator based on the schema file.
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaSource = new StreamSource(newMIBSchemaFile);
        Schema mibSchema = factory.newSchema(schemaSource);
        mibValidator = mibSchema.newValidator();
    }
    
    
    /**
     * Parses and validates XML MIB files and adds their elements to the MIB tree model.
     * 
     * @param mibFile the File to add to the MIB Tree
     * 
     * @throws InvalidXmlMibFormatException if the MIB file is invalid
     */
    protected void addMIBToTree(File mibFile) throws InvalidXmlMibFormatException
    {
        // The following parsing and validation code uses the new validation API introduced in Java 1.5.  This
        // separates parsing and validating.  This functionality is available through JAXP 1.3 included with
        // the latest Java release or with Java 1.4 combined with a separate JAXP 1.3 release. The GNU CLASSPATH
        // project also has a JAXP 1.3 partial implementation available that appears sufficient.
        try
        {
            Document doc = null;
            try
            {
                // parse the XML file to construct a document
                doc = docBuilder.parse(mibFile);
                
                // validate the document against the schema
                mibValidator.validate(new DOMSource(doc));
            }
            catch (SAXParseException e)
            {
                // If the file does not validate or a parsing error occurs, throw an exception with 
                // the file that failed and line and column details from the parsing exception.
                String errorMsg = "\nFailed at line: " + e.getLineNumber() + ", column: " + e.getColumnNumber() 
                    + "\nCause: " + e.getMessage();
                throw new InvalidXmlMibFormatException(errorMsg, mibFile);
            }
            catch (SAXException e)
            {
                // If the file does not validate or a parsing error occurs,
                // throw an exception with the file that failed and the message of the parsing exception.
                throw new InvalidXmlMibFormatException("\nCause: " + e.getMessage(), mibFile);
            }

            // This code will be reached if the MIB is a valid XML file, conforms to the schema, and validates.
            doc.getDocumentElement().normalize();
            
            String mibName = doc.getDocumentElement().getAttribute("definition").trim();

            // get the list of all MIB objects and loop through them
            NodeList mibObjectList = doc.getElementsByTagName("mibObject");
            for (int i = 0; i < mibObjectList.getLength(); i++)
                this.handleMIBObjectNode(mibObjectList.item(i), mibName);

        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
    
    
    /**
     * Reads the information in a &lt;mibObject&gt; element and puts that information into a 
     * new MibObjectType. This MibObjectType is added to the TreeModel if its name does not 
     * match an existing node.
     * 
     * @param mibObjectNode the Node to extract data from
     */
    private void handleMIBObjectNode(Node mibObjectNode, String mibName)
    {      
        if (mibObjectNode.getNodeType() == Node.ELEMENT_NODE)
        {
            mibObjectNode.normalize();
            Element mibObjectElement = (Element)mibObjectNode;

            // *** Only valid MIB object types will be accepted due to validation ***
            
            // Get the MIB object's name.
            String name = extractSubElementText(mibObjectElement, "objectName");

            // Check to see if this node is already in the tree or if the name is empty.
            // If either occur, do not attempt to construct a node and add it to the tree.
            if (!name.equals("") && !nodeMap.containsKey(name)) //check the HashMap
            {
                MibObjectType mibObject = new MibObjectType();
                mibObject.setName(name);
                //System.out.println(name);

                // Get the MIB object's id.
                NodeList idList = mibObjectElement.getElementsByTagName("objectId");
                Element idElement = (Element)idList.item(0);

                NodeList idText = idElement.getChildNodes();
                String idString = idText.item(0).getNodeValue().trim();
                mibObject.setId(Integer.parseInt(idString));

                // Get the MIB object's syntax.
                NodeList syntaxList = mibObjectElement.getElementsByTagName("syntax");

                // Parse the syntax element.
                if (syntaxList.getLength() > 0)
                {
                    Element syntaxElement = (Element)syntaxList.item(0);
                    MibSyntax objSyntax = this.parseSyntaxElement(syntaxElement);
                    if (objSyntax != null)
                        mibObject.setSyntax(objSyntax);
                }

                // Get the MIB object's access.
                String access = extractSubElementText(mibObjectElement, "access");
                if (!access.equals(""))
                    mibObject.setAccess(Access.valueOf(access.toUpperCase().replaceAll("-", "_")));

                // Get the MIB object's status.
                String status = extractSubElementText(mibObjectElement, "status");
                if (!status.equals(""))
                    mibObject.setStatus(Status.valueOf(status.toUpperCase()));

                // Get the MIB object's description.
                String description = extractSubElementText(mibObjectElement, "description");
                if (!description.equals(""))
                    mibObject.setDescription(description);

                mibObject.setMibName(mibName);

                // Get the MIB object's parent and see if it exists.
                NodeList parentList = mibObjectElement.getElementsByTagName("parent");
                Element parentElement = (Element)parentList.item(0);

                NodeList parentText = parentElement.getChildNodes();
                String parentName = parentText.item(0).getNodeValue().trim();

                this.addMibObject(mibObject, parentName);

            } // if the node didn't already exist and it had a valid name element
        } // if the object node is an element
    }
    
    
    /**
     * Retrieves the contents of an element that contains text.
     * If there is a structure such as:
     * <pre>
     *  &lt;element&gt;
     *     &lt;subelement&gt;TEXT&lt;/subelement&gt;
     *  &lt;/element&gt;
     * </pre>
     * then this method will return the "TEXT" of the subelement specified by
     * the String elementName.
     * 
     * @param objectElement the XML element representing a MIB object
     * @param elementName the name of the sub-element to retrieve
     * @return the String value of element's contents or an empty string if the content was
     *         not available
     */
    private String extractSubElementText(Element objectElement, String elementName)
    {
        // Get the element specified by elementName.
        NodeList elementList = objectElement.getElementsByTagName(elementName);
        if (elementList.getLength() > 0)
        {
            Element curElement = (Element)elementList.item(0);
            NodeList textElementList = curElement.getChildNodes();

            // Don't allow empty element nodes, even though the schema forbids it anyway.
            // Also, make sure the node is a text node, though according to the schema, it should be.
            Node textNode = textElementList.item(0);
            if ( textNode != null && textNode.getNodeType() == Node.TEXT_NODE )
            {
                String elementContents = textNode.getNodeValue().trim(); //the trim is crucial here
                return elementContents;
            }
        }
        
        return ""; // return an empty string if either the element was missing or the element was empty
    }
    
    

    private MibSyntax parseSyntaxElement(Element syntaxElement)
    {
        MibSyntax objSyntax = null;
        
        // Get the data type or sequence from the syntax element.
        NodeList typeList = syntaxElement.getElementsByTagName("type");
        if (typeList.getLength() > 0)
        {
            // Get data type element.
            Element typeElement = (Element)typeList.item(0);

            NodeList typeText = typeElement.getChildNodes();
            objSyntax = new MibSyntax( typeText.item(0).getNodeValue().trim() );
        }
        else
        {
            // Get sequence element.
            NodeList sequenceList = syntaxElement.getElementsByTagName("sequence");
            Element sequenceElement = (Element)sequenceList.item(0);

            NodeList sequenceText = sequenceElement.getChildNodes();
            objSyntax = new MibSyntax("Sequence of " + sequenceText.item(0).getNodeValue().trim() );
        }

        // Get the value list from the syntax element if one exists.
        NodeList valuesList = syntaxElement.getElementsByTagName("valueList");
        if (valuesList.getLength() > 0)
        {
            List<MibNameValuePair> values = new ArrayList<MibNameValuePair>(); 
            
            Element valuesListElement = (Element)valuesList.item(0);
            NodeList valueItemList = valuesListElement.getElementsByTagName("valueItem");

            // Loop through all of the value items in the list.
            for (int j = 0; j < valueItemList.getLength(); j++)
            {
                Element valueItemElement = (Element)valueItemList.item(j);

                // Get the value item's label.
                NodeList labelList = valueItemElement.getElementsByTagName("label");
                Element labelElement = (Element)labelList.item(0);

                NodeList labelText = labelElement.getChildNodes();
                String valueLabel = labelText.item(0).getNodeValue().trim();

                // get the value item's number
                NodeList valList = valueItemElement.getElementsByTagName("val");
                Element valElement = (Element)valList.item(0);

                NodeList valText = valElement.getChildNodes();
                String numberString = valText.item(0).getNodeValue().trim();
                int numberValue = Integer.parseInt(numberString);

                // Add the value list entry.
                MibNameValuePair curValueItem = new MibNameValuePair(valueLabel, numberValue);
                values.add(curValueItem);
            }

            // Since the MIB file must have validated, objSyntax should always be non-null at this point,
            // but you can never be too careful.
            if (objSyntax != null)
                objSyntax.setValues(values);
        }
        
        return objSyntax;
    }


    /**
     * @see libmib.mibtree.MibTreeBuilder#getMibFolder() 
     */
	@Override
	public String getMibFolder()
	{
		return "xml";
	}

}

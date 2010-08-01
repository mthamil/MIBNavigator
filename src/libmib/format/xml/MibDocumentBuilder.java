/**
 * libmib - Java SNMP Management Information Base Library
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

package libmib.format.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import utilities.IOUtilities;

import libmib.MibImport;
import libmib.MibModuleIdRevision;
import libmib.MibObjectExtended;
import libmib.MibSyntax;
import libmib.format.smi.SMIToken;
import libmib.mibtree.BasicXmlErrorHandler;

/**
 * This class uses JAXP to construct an XML Document corresponding to a MIB module.  MIB objects and imports can be directly added
 * and they will be converted into Elements of the MIB document.  A method is also provided for writing the Document to a file
 * as XML.  The DocumentBuilder that produces the Document is validating and uses the schema defined in "mib.xsd".
 */
public class MibDocumentBuilder 
{
    private Document mibDocument;
    private Element mibRoot;        //root element
    private Element mibImports;     //imports section
    private Element mibObjects;     //objects section
    
    private static final String SCHEMA_LANGUAGE_ATTRIBUTE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";
    private static final String SCHEMA_SOURCE_ATTRIBUTE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    private final File MIB_SCHEMA = new File("schema" + File.separator + "mib.xsd");
    
    private static Map<String, String> smiToXmlTypeMap = new HashMap<String, String>();
    
    static
    {
    	smiToXmlTypeMap.put(SMIToken.OBJECT_ID.token(), ElementNames.OBJECT_ID);
    	smiToXmlTypeMap.put(SMIToken.OBJECT_TYPE.token(), ElementNames.OBJECT_TYPE);
    	smiToXmlTypeMap.put(SMIToken.OBJECT_GROUP.token(), ElementNames.OBJECT_GROUP);
    	smiToXmlTypeMap.put(SMIToken.NOTIF.token(), ElementNames.NOTIFICATION);
    	smiToXmlTypeMap.put(SMIToken.MODULE_ID.token(), ElementNames.MODULE_ID);
    	smiToXmlTypeMap.put(SMIToken.MODULE_COMP.token(), ElementNames.MODULE_COMPLIANCE);
    	smiToXmlTypeMap.put(SMIToken.NOTIF_GROUP.token(), ElementNames.NOTIFICATION_GROUP);
    }
    
    /**
     * Creates a new <code>MibDocumentBuilder</code> that validates and uses the mib.xsd schema.
     * A Document object is created and initialized with a "mib" root node.
     */
    public MibDocumentBuilder()
    {
    	FileInputStream schemaStream = null;
        try 
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setValidating(true);
            docBuilderFactory.setNamespaceAware(true);
            docBuilderFactory.setAttribute(SCHEMA_LANGUAGE_ATTRIBUTE, SCHEMA_LANGUAGE);
            
            schemaStream = new FileInputStream(MIB_SCHEMA);
            docBuilderFactory.setAttribute(SCHEMA_SOURCE_ATTRIBUTE, new InputSource(schemaStream));

            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            docBuilder.setErrorHandler(new BasicXmlErrorHandler());

            mibDocument = docBuilder.newDocument();
            
            //create root element
            mibRoot = mibDocument.createElement(ElementNames.ROOT);
            mibDocument.appendChild(mibRoot);
            
        }
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (ParserConfigurationException e) 
        {
            e.printStackTrace();
        }
        finally
        {
        	IOUtilities.closeQuietly(schemaStream);
        }
    }
    
    /**
     * Adds a MIB module definition attribute to the root element.  That is, this attribute specifies
     * the name of the MIB module that the document describes.
     * 
     * @param mibName the name of the MIB module defined by the MIB document
     */
    public void addDefinitionAttribute(String mibName)
    {
    	addNewAttribute(mibRoot, ElementNames.NAME_ATTR, mibName);
    }
    
    /**
     * Adds a <code>MibImport</code> item to the MIB document.  If the "imports" element has not
     * yet been created, it will be initialized and added to the root element so that import items can
     * be added.  Import items use the structure defined in the MIB schema.
     * 
     * @param newImport the <code>MibImport</code> to add to the MIB document
     */
    public void addImportElement(MibImport newImport)
    {
        if (newImport == null)
            throw new NullPointerException();
        
        if (mibImports == null)
        {
            mibImports = mibDocument.createElement(ElementNames.IMPORTS);
            mibRoot.appendChild(mibImports);
        }
        
        Element importElement = mibDocument.createElement(ElementNames.SOURCE);
        addNewAttribute(importElement, ElementNames.NAME_ATTR, newImport.getSource());
        
        List<String> imports = newImport.getImports();
        for (String importName : imports)
            addNewTextElement(importElement, ElementNames.IMPORT, importName);
        
        mibImports.appendChild(importElement);
    }
    
    
    /**
     * Adds a <code>MibObjectType</code> to the MIB document.  If the "objects" element has
     * not yet been created, it will be initialized and added to the root element so that
     * mib objects can be added.  Objects use the structure defined in the MIB schema.
     * 
     * @param newObject the <code>MibObjectType</code> to add to the MIB document
     */
    public void addObjectElement(MibObjectExtended newObject)
    {
        if (newObject == null)
            throw new NullPointerException();
        
        if (mibObjects == null)
        {
            mibObjects = mibDocument.createElement(ElementNames.OBJECTS);
            mibRoot.appendChild(mibObjects);
        }
        
        Element objectElement = mibDocument.createElement(ElementNames.OBJECT);
        mibObjects.appendChild(objectElement);
        
        String objType = newObject.getObjectType();
        String nodeName = smiToXmlTypeMap.get(objType);
        
        Element typeElement = mibDocument.createElement(nodeName);
        objectElement.appendChild(typeElement);
        
        addNewTextElement(typeElement, ElementNames.NAME, newObject.getName());
        
        addNewTextElement(typeElement, ElementNames.ID, String.valueOf(newObject.getId()));
        
        addNewTextElement(typeElement, ElementNames.PARENT, newObject.getParent());
        

        // SYNTAX
        MibSyntax syntax = newObject.getSyntax();
        if (syntax != null)
        {
            Element syntaxElement = createSyntaxElement(syntax);
            typeElement.appendChild(syntaxElement);
        }
        
        // GROUPS
        if (newObject.hasGroupMembers())
        {
            Element groupElement = mibDocument.createElement(ElementNames.MEMBERS);
            typeElement.appendChild(groupElement);
            
            List<String> members = newObject.getGroupMembers();
            for (String member : members)
                addNewTextElement(groupElement, ElementNames.MEMBER, member);
        }
        
        // ACCESS
        if (newObject.getAccess() != null)
            addNewTextElement(typeElement, ElementNames.ACCESS, newObject.getAccess().toString());

        // STATUS
        if (newObject.getStatus() != null)
            addNewTextElement(typeElement, ElementNames.STATUS, newObject.getStatus().toString());
        
        // LAST UPDATED
        String lastUpdated = newObject.getLastUpdated();
        if (!lastUpdated.equals(""))
            addNewTextElement(typeElement, ElementNames.UPDATED, lastUpdated);
        
        // ORGANIZATION
        String org = newObject.getOrganization();
        if (!org.equals(""))
            addNewTextElement(typeElement, ElementNames.ORG, org);
        
        // CONTACT-INFO
        String contact = newObject.getContactInfo();
        if (!contact.equals(""))
            addNewTextElement(typeElement, ElementNames.CONTACT, contact);
        
        // DESCRIPTION
        String description = newObject.getDescription();
        if (!description.equals(""))
            addNewTextElement(typeElement, ElementNames.DESCRIPTION, description);
        
        // REFERENCE
        String reference = newObject.getReference();
        if (!reference.equals(""))
            addNewTextElement(typeElement, ElementNames.REF, reference);
 
        // INDEX LIST
        if (newObject.hasIndices())
        {
            Element indicesElement = mibDocument.createElement(ElementNames.INDICES);
            typeElement.appendChild(indicesElement);
            
            List<String> indices = newObject.getIndices();
            for (String index : indices)
                addNewTextElement(indicesElement, ElementNames.INDEX, index);
        }
        
        // REVISIONS
        if (newObject.hasRevisions())
        {
            List<MibModuleIdRevision> revisions = newObject.getRevisions();
            for (MibModuleIdRevision revision : revisions)
            {
                Element revElement = mibDocument.createElement(ElementNames.REVISION);
                addNewAttribute(revElement, ElementNames.REV_ID_ATTR, revision.getRevisionId());
                typeElement.appendChild(revElement);

                addNewTextElement(revElement, ElementNames.DESCRIPTION, revision.getRevisionDescription());
            }
        }
    }
    
    
    /**
     * Constructs a MIB syntax element from a <code>MibSyntax</code> object. 
     * 
     * @param newSyntax the <code>MibSyntax</code> object to convert into an Element
     * @return an Element representing the <code>MibSyntax</code> object
     */
    private Element createSyntaxElement(MibSyntax newSyntax)
    {
        Element syntaxElement = mibDocument.createElement(ElementNames.SYNTAX);

        // DATA TYPE
        String type = newSyntax.getDataType();
        if (!type.equals(""))
        {
            final String SEQ = "SEQUENCE OF";
            if (type.contains(SEQ))
                addNewTextElement(syntaxElement, ElementNames.SEQUENCE, type.substring(SEQ.length() + 1));
            else
                addNewTextElement(syntaxElement, ElementNames.TYPE, type);
        }
        
        // NAME-VALUE PAIRS
        if (newSyntax.hasValues())
        {
            Element pairsElement = mibDocument.createElement(ElementNames.PAIRS);
            syntaxElement.appendChild(pairsElement);
            
            Map<Integer, String> pairs = newSyntax.getValuePairs();
            for (Entry<Integer, String> entry : pairs.entrySet())
            {    
                Element pairElement = mibDocument.createElement(ElementNames.PAIR);
                pairsElement.appendChild(pairElement);
                
                addNewTextElement(pairElement, ElementNames.PAIR_NAME, entry.getValue());
                addNewTextElement(pairElement, ElementNames.PAIR_VALUE, String.valueOf(entry.getKey()));
            }
        }
          
        // DEFAULT VALUE                  
        String defaultValue = newSyntax.getDefaultValue();
        if (!defaultValue.equals(""))
            addNewTextElement(syntaxElement, ElementNames.DEFAULT, defaultValue);
        
        return syntaxElement;
    }
    
    private void addNewTextElement(Element parentElement, String elementName, String text)
    {
    	Element newElement = mibDocument.createElement(elementName);
        Text newText = mibDocument.createTextNode(text);
        newElement.appendChild(newText);
        parentElement.appendChild(newElement);
    }
    
    private void addNewAttribute(Element parentElement, String name, String value)
    {
    	Attr attribute = createAttributeWithValue(name, value);
    	parentElement.setAttributeNode(attribute);
    }
    
    private Attr createAttributeWithValue(String name, String value)
    {
    	Attr attribute = mibDocument.createAttribute(name);
    	attribute.setValue(value);
    	return attribute;
    }

    
    /**
     * Transforms the MIB document and writes it to a file as XML.  UTF-8 encoding is used
     * and an indention size of 4 spaces is used for formatting.
     * 
     * @param outputFile the <code>File</code> to write the XML to
     */
    public void writeDocument(File outputFile)
    {
        try 
        {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            tFactory.setAttribute("indent-number", Integer.valueOf(4));

            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT , "yes");
            
            DOMSource domSource = new DOMSource(mibDocument);
            StreamResult streamResult = new StreamResult(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
            transformer.transform(domSource,streamResult);
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (TransformerConfigurationException e) 
        {
            e.printStackTrace();
        } 
        catch (TransformerException e) 
        {
            e.printStackTrace();
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Returns the <code>Document</code> object used to construct the MIB document.
     * 
     * @return the <code>MibDocumentBuilder</code>'s MIB document
     */
    public Document getMIBDocument()
    {
        if (mibDocument == null)
            throw new NullPointerException();
        
        return mibDocument;
    }
    
}

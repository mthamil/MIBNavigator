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

package libmib.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

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

import libmib.MibImport;
import libmib.MibModuleIdRevision;
import libmib.NameValuePair;
import libmib.MibObjectExtended;
import libmib.MibSyntax;
import libmib.mibtree.BasicXmlErrorHandler;

/**
 * This class uses JAXP to construct a Document corresponding to a MIB module.  MIB objects and imports can be directly added
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
    
    /**
     * Creates a new <code>MibDocumentBuilder</code> that validates and uses the mib.xsd schema.
     * A Document object is created and initialized with a "mib" root node.
     */
    public MibDocumentBuilder()
    {
        try 
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setValidating(true);
            docBuilderFactory.setNamespaceAware(true);
            docBuilderFactory.setAttribute(SCHEMA_LANGUAGE_ATTRIBUTE, SCHEMA_LANGUAGE);
            docBuilderFactory.setAttribute(SCHEMA_SOURCE_ATTRIBUTE, new InputSource(new FileInputStream(MIB_SCHEMA)));

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
    }
    
    /**
     * Adds a MIB module definition attribute to the root element.  That is, this attribute specifies
     * the name of the MIB module that the document describes.
     * 
     * @param mibName the name of the MIB module defined by the MIB document
     */
    public void addDefinitionAttribute(String mibName)
    {
        Attr mibDefAttr = mibDocument.createAttribute(ElementNames.NAME_ATTR);
        mibDefAttr.setValue(mibName);
        mibRoot.setAttributeNode(mibDefAttr);
    }
    
    
    private void addSourceNameAttribute(Element importElement, String source)
    {
    	Attr sourceAttr = mibDocument.createAttribute(ElementNames.NAME_ATTR);
    	sourceAttr.setValue(source);
        importElement.setAttributeNode(sourceAttr);
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
        this.addSourceNameAttribute(importElement, newImport.getSource());
        
        List<String> imports = newImport.getImports();
        for (String importName : imports)
        {
            Element importItemElement = mibDocument.createElement(ElementNames.IMPORT);
            Text importItemText = mibDocument.createTextNode(importName);
            importItemElement.appendChild(importItemText);
            importElement.appendChild(importItemElement);
        }
        
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
        if (objType.equals(SmiTokens.OBJECT_ID))
            objType = ElementNames.OBJECT_ID;
        else if (objType.equals(SmiTokens.OBJECT_TYPE))
            objType = ElementNames.OBJECT_TYPE;
        else if (objType.equals(SmiTokens.OBJECT_GRP))
            objType = ElementNames.OBJECT_GROUP;
        else if (objType.equals(SmiTokens.NOTIF))
            objType = ElementNames.NOTIFICATION;
        else if (objType.equals(SmiTokens.MODULE_ID))
            objType = ElementNames.MODULE_ID;
        else if (objType.equals(SmiTokens.MODULE_COMP))
            objType = ElementNames.MODULE_COMPLIANCE;
        else if (objType.contains(SmiTokens.NOTIF_GRP))
            objType = ElementNames.NOTIFICATION_GROUP;
        
        Element typeElement = mibDocument.createElement(objType);
        objectElement.appendChild(typeElement);
        
        Element nameElement = mibDocument.createElement(ElementNames.NAME);
        Text nameText = mibDocument.createTextNode(newObject.getName());
        nameElement.appendChild(nameText);
        typeElement.appendChild(nameElement);
        
        Element idElement = mibDocument.createElement(ElementNames.ID);
        Text idText = mibDocument.createTextNode(String.valueOf(newObject.getId()));
        idElement.appendChild(idText);
        typeElement.appendChild(idElement);
        
        Element parentElement = mibDocument.createElement(ElementNames.PARENT);
        Text parentText = mibDocument.createTextNode(newObject.getParent());
        parentElement.appendChild(parentText);
        typeElement.appendChild(parentElement);
        

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
            
            List<String> grpMembers = newObject.getGroupMembers();
            for (String member : grpMembers)
            {
                Element memberElement = mibDocument.createElement(ElementNames.MEMBER);
                Text memberText = mibDocument.createTextNode(member);
                memberElement.appendChild(memberText);
                groupElement.appendChild(memberElement);
            } 
        }
        
        // ACCESS
        if (newObject.getAccess() != null)
        {
            Element accessElement = mibDocument.createElement(ElementNames.ACCESS);
            Text accessText = mibDocument.createTextNode(newObject.getAccess().toString());
            accessElement.appendChild(accessText);
            typeElement.appendChild(accessElement);
        }

        // STATUS
        if (newObject.getStatus() != null)
        {
            Element statusElement = mibDocument.createElement(ElementNames.STATUS);
            Text statusText = mibDocument.createTextNode(newObject.getStatus().toString());
            statusElement.appendChild(statusText);
            typeElement.appendChild(statusElement);
        }
        
        // LAST UPDATED
        String lastUpdated = newObject.getLastUpdated();
        if (!lastUpdated.equals(""))
        {
            Element updateElement = mibDocument.createElement(ElementNames.UPDATED);
            Text updateText = mibDocument.createTextNode(lastUpdated);
            updateElement.appendChild(updateText);
            typeElement.appendChild(updateElement);
        }
        
        // ORGANIZATION
        String org = newObject.getOrganization();
        if (!org.equals(""))
        {
            Element orgElement = mibDocument.createElement(ElementNames.ORG);
            Text orgText = mibDocument.createTextNode(org);
            orgElement.appendChild(orgText);
            typeElement.appendChild(orgElement);
        }
        
        // CONTACT-INFO
        String contact = newObject.getContactInfo();
        if (!contact.equals(""))
        {
            Element contactElement = mibDocument.createElement(ElementNames.CONTACT);
            Text contactText = mibDocument.createTextNode(contact);
            contactElement.appendChild(contactText);
            typeElement.appendChild(contactElement);
        }
        
        // DESCRIPTION
        String desc = newObject.getDescription();
        if (!desc.equals(""))
        {
            Element descElement = mibDocument.createElement(ElementNames.DESCRIPTION);
            Text descText = mibDocument.createTextNode(desc);
            descElement.appendChild(descText);
            typeElement.appendChild(descElement);
        }
        
        // REFERENCE
        String ref = newObject.getReference();
        if (!ref.equals(""))
        {
            Element refElement = mibDocument.createElement(ElementNames.REF);
            Text refText = mibDocument.createTextNode(ref);
            refElement.appendChild(refText);
            typeElement.appendChild(refElement);
        }
 
        // INDEX LIST
        if (newObject.hasIndices())
        {
            Element indicesElement = mibDocument.createElement(ElementNames.INDICES);
            typeElement.appendChild(indicesElement);
            
            List<String> indices = newObject.getIndices();
            for (String index : indices)
            {
                Element indexElement = mibDocument.createElement(ElementNames.INDEX);
                Text indexText = mibDocument.createTextNode(index);
                indexElement.appendChild(indexText);
                indicesElement.appendChild(indexElement);
            } 
        }
        
        // REVISIONS
        if (newObject.hasRevisions())
        {
            List<MibModuleIdRevision> revList = newObject.getRevisions();
            for (MibModuleIdRevision rev : revList)
            {
                Element revElement = mibDocument.createElement(ElementNames.REVISION);
                Attr revIdAttr = mibDocument.createAttribute(ElementNames.REV_ID_ATTR);
                revIdAttr.setValue(rev.getRevisionId());
                revElement.setAttributeNode(revIdAttr);
                typeElement.appendChild(revElement);
                
                Element revDescElement = mibDocument.createElement(ElementNames.DESCRIPTION);
                Text revDescText = mibDocument.createTextNode(rev.getRevisionDescription());
                revDescElement.appendChild(revDescText);
                revElement.appendChild(revDescElement);
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
            String SEQ = "SEQUENCE OF";
            if (type.contains(SEQ))
            {
                Element seqElement = mibDocument.createElement(ElementNames.SEQUENCE);
                Text seqText = mibDocument.createTextNode(type.substring(SEQ.length() + 1));
                seqElement.appendChild(seqText);
                syntaxElement.appendChild(seqElement);
            }
            else
            {
                Element dataTypeElement = mibDocument.createElement(ElementNames.TYPE);
                Text dataTypeText = mibDocument.createTextNode(type);
                dataTypeElement.appendChild(dataTypeText);
                syntaxElement.appendChild(dataTypeElement);
            }
        }
        
        // NAME-VALUE PAIRS
        if (newSyntax.hasValues())
        {
            Element pairsElement = mibDocument.createElement(ElementNames.PAIRS);
            syntaxElement.appendChild(pairsElement);
            
            List<NameValuePair> pairs = newSyntax.getValuePairs();
            for (NameValuePair pair : pairs)
            {    
                Element pairElement = mibDocument.createElement(ElementNames.PAIR);
                pairsElement.appendChild(pairElement);
                
                Element nameElement = mibDocument.createElement(ElementNames.PAIR_NAME);
                Text nameText = mibDocument.createTextNode(pair.getName());
                nameElement.appendChild(nameText);
                pairElement.appendChild(nameElement);
                
                Element valueElement = mibDocument.createElement(ElementNames.PAIR_VALUE);
                Text valueText = mibDocument.createTextNode(String.valueOf(pair.getValue()));
                valueElement.appendChild(valueText);
                pairElement.appendChild(valueElement);
            }
        }
          
        // DEFAULT VALUE                  
        String defaultValue = newSyntax.getDefaultValue();
        if (!defaultValue.equals(""))
        {
            Element defaultElement = mibDocument.createElement(ElementNames.DEFAULT);
            Text defaultText = mibDocument.createTextNode(defaultValue);
            defaultElement.appendChild(defaultText);
            syntaxElement.appendChild(defaultElement);
        }
        
        return syntaxElement;
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
            tFactory.setAttribute("indent-number", new Integer(4));

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

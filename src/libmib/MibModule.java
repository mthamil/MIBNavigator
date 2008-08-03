package libmib;
import java.util.ArrayList;
import java.util.List;

import libmib.oid.MibObjectType;

/**
 * Class representing a MIB module definition.  It stores Imports, Data Types, 
 * and OIDs.
 */
public class MibModule 
{
    private String mibName;
    
    //lists that store the MIB elements
    private List<MibImport> importList = null;
    private List<MibObjectType> objectList = null;
    
    public MibModule()
    {
       mibName = "";
       initializeElementLists();
    }
    
    public MibModule(String newMibName)
    {
        mibName = newMibName;
        initializeElementLists();
    }
    
    private void initializeElementLists()
    {
        importList = new ArrayList<MibImport>();
        objectList = new ArrayList<MibObjectType>();
    }
    
    public void setMibName(String newMibName)
    {
        mibName = newMibName;
    }
    
    public String getMibName()
    {
        return mibName;
    }
    
    
    public void addMibImport(MibImport newImport)
    {
        importList.add(newImport);
    }
    
    public void addMibObject(MibObjectType newObject)
    {
        objectList.add(newObject);
    }
    
   
    public List<MibImport> getMibImports()
    {
        return importList;
    }
    
    public List<MibObjectType> getMibObjects()
    {
        return objectList;
    }
}

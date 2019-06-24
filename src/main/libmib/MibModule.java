package libmib;
import java.util.ArrayList;
import java.util.List;


/**
 * Class representing a MIB module definition.  It stores Imports, Data Types, 
 * and OIDs.
 */
public class MibModule 
{
    private String name;
    
    // lists that store the MIB elements
    private List<MibImport> imports = null;
    private List<MibObjectType> objects = null;
    
    public MibModule()
    {
       name = "";
       initializeElements();
    }
    
    public MibModule(String newName)
    {
        name = newName;
        initializeElements();
    }
    
    private void initializeElements()
    {
        imports = new ArrayList<MibImport>();
        objects = new ArrayList<MibObjectType>();
    }
    
    public void setName(String newName)
    {
        name = newName;
    }
    
    public String getName()
    {
        return name;
    }
    
    
    public void addImport(MibImport newImport)
    {
        imports.add(newImport);
    }
    
    public List<MibImport> getImports()
    {
        return imports;
    }
    
    
    public void addObject(MibObjectType newObject)
    {
        objects.add(newObject);
    }

    public List<MibObjectType> getObjects()
    {
        return objects;
    }
}

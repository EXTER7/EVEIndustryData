package exter.eveindustry.dataprovider.index;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import exter.eveindustry.dataprovider.item.Item;
import exter.eveindustry.dataprovider.item.ItemDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class Index
{
  public class Entry
  {
    public final Item Item;
    public final int Group;
    
    public Entry(Item item,int g)
    {
      this.Item = item;;
      this.Group = g;
    }
  }
  
  public class Group
  {
    public final int ID;
    public final String Name;

    public Group(int i,String n)
    {
      ID = i;
      Name = n;
    }

  }
  
  private List<Entry> entries;
  private List<Item> items;
  private Map<Integer,Group> groups;
  private List<Group> groups_list;
  
  public Index(File eid_zip,String path, ItemDA inventory)
  {
    groups = new HashMap<Integer,Group>();
    groups_list = new ArrayList<Group>();
    entries = new ArrayList<Entry>();
    items = new ArrayList<Item>();
    
    try
    {
      ZipFile zip = new ZipFile(eid_zip);
      try
      {
        InputStream raw = zip.getInputStream(zip.getEntry(path));
        try
        {
          TSLObject node = new TSLObject();
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          if(!reader.getName().equals("index"))
          {
            throw new RuntimeException("TSL file is not an index");
          }
          while(true)
          {
            reader.moveNext();
            TSLReader.State type = reader.getState();
            if(type == TSLReader.State.ENDOBJECT)
            {
              break;
            } else if(type == TSLReader.State.OBJECT)
            {
              String node_name = reader.getName();
              if(node_name.equals("item"))
              {
                node.loadFromReader(reader);
                int id = node.getStringAsInt("id", -1);
                int group = node.getStringAsInt("group", -1);
                if(group < 0)
                {
                  throw new RuntimeException("Invalid group value.");
                }
                Item item = inventory.items.get(id);
                if(item == null)
                {
                  throw new RuntimeException("Invalid item value.");
                }
                entries.add(new Entry(item, group));
                items.add(item);
              } else if(node_name.equals("group"))
              {
                node.loadFromReader(reader);
                int id = node.getStringAsInt("id", -1);
                String name = node.getString("name", null);
                if(id < -1 || name == null)
                {
                  throw new RuntimeException();
                }
                Group g = new Group(id, name);
                groups.put(g.ID, g);
                groups_list.add(g);
              } else
              {
                reader.skipObject();
              }
            }
          }
        } catch(InvalidTSLException e)
        {
          zip.close();
          raw.close();
          throw new RuntimeException(e);
        }
      } catch(IOException e)
      {
        zip.close();
        throw new RuntimeException(e);
      }
      zip.close();
    } catch(IOException e1)
    {
      throw new RuntimeException(e1);
    }
  }
  
  public List<Entry> getEntries()
  {
    return entries;
  }
  
  public List<Item> getItems()
  {
    return items;
  }

  public List<Group> getGroups()
  {
    return groups_list;
  }

}

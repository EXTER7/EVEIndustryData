package exter.eveindustry.dataprovider.item;

import exter.eveindustry.data.inventory.IItem;
import exter.tsl.TSLObject;

public class Item implements IItem
{
  public final int ID;
  public final String Name;
  public final int Group;
  public final double Volume;
  public final boolean Market;
  public final int MetaGroup;
  public final int Icon;
  
  public final String NameLowercase;

  @Override
  public int hashCode()
  {
    return ID;
  }

  public boolean equals(Object obj)
  {
    return (obj instanceof Item) && equals((Item)obj);
  }

  public boolean equals(Item it)
  {
    return ID == it.ID;
  }
  
  public Item(TSLObject tsl)
  {
    
    ID = tsl.getStringAsInt("id", -1);
    Name = tsl.getString("name", null);
    Group = tsl.getStringAsInt("gid", -1);
    Volume = tsl.getStringAsFloat("vol", -1);
    Market = tsl.getStringAsInt("market",0) != 0;
    MetaGroup = tsl.getStringAsInt("mg", -1);
    Icon = tsl.getStringAsInt("icon", -1);
    NameLowercase = Name.toLowerCase();
  }

  @Override
  public int getID()
  {
    return ID;
  }


  @Override
  public int getGroupID()
  {
    return Group;
  }
}

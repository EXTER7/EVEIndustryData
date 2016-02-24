package exter.eveindustry.dataprovider.item;

import exter.tsl.TSLObject;

public class ItemGroup
{
  public final int ID;
  public final int Category;
  public final String Name;
  
  public ItemGroup(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id", -1);
    Name = tsl.getString("name", null);
    Category = tsl.getStringAsInt("cid", -1);
  }
}

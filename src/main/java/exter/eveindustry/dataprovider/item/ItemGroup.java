package exter.eveindustry.dataprovider.item;

import exter.tsl.TSLObject;

public class ItemGroup
{
  public final int ID;
  public final int Category;
  public final String Name;
  public final int Icon;
  public final boolean Blueprints;
  
  public ItemGroup(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id", -1);
    Name = tsl.getString("name", null);
    Category = tsl.getStringAsInt("cid", -1);
    Icon = tsl.getStringAsInt("icon", -1);
    Blueprints = tsl.getStringAsInt("blueprints", 0) == 1;
  }
}

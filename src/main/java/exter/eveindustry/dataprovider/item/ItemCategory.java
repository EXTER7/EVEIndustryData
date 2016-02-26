package exter.eveindustry.dataprovider.item;

import exter.tsl.TSLObject;

public class ItemCategory
{
  public final int ID;
  public final String Name;
  public final int Icon;
  public final boolean Blueprints;
  
  public ItemCategory(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id", -1);
    Name = tsl.getString("name", null);
    Icon = tsl.getStringAsInt("icon", -1);
    Blueprints = tsl.getStringAsInt("blueprints", 0) == 1;
  }

}

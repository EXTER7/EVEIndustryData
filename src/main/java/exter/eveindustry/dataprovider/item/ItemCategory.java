package exter.eveindustry.dataprovider.item;

import exter.tsl.TSLObject;

public class ItemCategory
{
  public final int ID;
  public final String Name;
  
  public ItemCategory(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id", -1);
    Name = tsl.getString("name", null);
  }

}

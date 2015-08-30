package exter.eveindustry.dataprovider.inventory;

import exter.tsl.TSLObject;

public class ItemMetaGroup
{
  public final int ID;
  public final String Name;

  public ItemMetaGroup(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id", -1);
    Name = tsl.getString("name", null);
  }

}

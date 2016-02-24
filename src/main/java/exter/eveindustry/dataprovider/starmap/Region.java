package exter.eveindustry.dataprovider.starmap;

import exter.tsl.TSLObject;

public class Region
{
  public final int ID;
  public final String Name;
  
  public Region(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id", -1);
    Name = tsl.getString("name", null);
  }
}

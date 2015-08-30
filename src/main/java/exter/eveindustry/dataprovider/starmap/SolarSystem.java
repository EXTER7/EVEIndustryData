package exter.eveindustry.dataprovider.starmap;

import exter.tsl.TSLObject;

public class SolarSystem
{
  public final int ID;
  public final String Name;
  public final int Region;
  
  public SolarSystem(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id", -1);
    Name = tsl.getString("name", null);
    Region = tsl.getStringAsInt("region", -1);
  }
}

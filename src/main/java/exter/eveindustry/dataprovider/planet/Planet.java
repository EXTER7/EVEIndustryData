package exter.eveindustry.dataprovider.planet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.data.planet.IPlanet;
import exter.eveindustry.dataprovider.inventory.InventoryDA;
import exter.tsl.TSLObject;

public class Planet implements IPlanet
{
  public final List<IItem> Resources;
  public final String TypeName;
  public final int ID;
  public final boolean Advanced;
  
  public Planet(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id",-1);
    TypeName = tsl.getString("name",null);
    Advanced = (tsl.getStringAsInt("advanced",0) != 0);
    
    List<IItem> res = new ArrayList<IItem>();
    for(Integer id:tsl.getStringAsIntegerList("resource"))
    {
      res.add(InventoryDA.items.get(id));
    }
    Resources = Collections.unmodifiableList(res);
  }

  @Override
  public List<IItem> getResources()
  {
    return Resources;
  }

  @Override
  public int getID()
  {
    return ID;
  }

  @Override
  public boolean isAdvanced()
  {
    return Advanced;
  }
}

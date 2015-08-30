package exter.eveindustry.dataprovider.starbase;

import exter.eveindustry.data.reaction.IStarbaseTower;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.test.data.inventory.InventoryDA;
import exter.eveindustry.test.data.inventory.Item;
import exter.tsl.TSLObject;

public class StarbaseTower implements IStarbaseTower
{
  public final Item TowerItem;
  public final ItemStack RequiredFuel;
  public final String Name;

  public StarbaseTower(TSLObject tsl)
  {
    TowerItem = InventoryDA.items.get(tsl.getStringAsInt("id",-1));
    RequiredFuel = new ItemStack( InventoryDA.items.get(tsl.getStringAsInt("fuel_id",-1)),tsl.getStringAsInt("fuel_amount",-1));
    Name = tsl.getString("name",null);
  }

  @Override
  public int getID()
  {
    return TowerItem.getID();
  }

  @Override
  public ItemStack getRequiredFuel()
  {
    return RequiredFuel;
  }
}
